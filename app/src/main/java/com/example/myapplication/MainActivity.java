package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.model.AdvertisingPacket;
import com.example.myapplication.model.FilterRecoard;
import com.example.myapplication.util.BeaconDistanceCalculator;
import com.example.myapplication.util.Util;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BLE-app";
    private final static int REQUEST_ENABLE_BT = 1;

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner;

    private ArrayAdapter<String> listAdapter;
    private TextView textViewLat;
    private TextView textViewLng;

    private Timer timerView;
    private Timer timerFilter;
    private Timer timerTrim;
    private Timer timerGetLocation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        Util.db.setFirestoreSettings(settings);

        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        listView = findViewById(R.id.listView);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDeviceList);
        listView.setAdapter(listAdapter);

        Button buttonStartDetect = findViewById(R.id.button1);
        Button buttonStopRead = findViewById(R.id.button2);
        Button buttonStartCollecting = findViewById(R.id.button4);
        Button buttonGetLivePosition = findViewById(R.id.getLivePositionButton);


        buttonStartDetect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startScanning();

                timerView = new Timer();
                timerView.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceList.clear();
                                for (Beacons beacons : Util.beaconsMap.values()) {
                                    mDeviceList.add(beacons.getBluetoothDevice().getName() + "\n" +
                                                    " rssi mean: " + beacons.getAverageRssiValue() + "\n" +
                                                    " rssi now: " + beacons.getRssiValue() + "\n" +
                                                    " rssi kalman: " + beacons.getKalmanRssi() + "\n" +
                                                    " rssi arma: " + beacons.getArmaRssi() + "\n"
//                                            " dis2: " + beacons.getDistanceFormula2() + " --- " +
//                                            " dis3: " + beacons.getDistanceFormula3() + "\n" +
//                                            " dis2Mean: " + Util.getDistance2(beacons.getAverageRssiValue(), -62) +
//                                            " dis3Mean: " + Util.getDistance3(beacons.getAverageRssiValue(), -62)
                                    );
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                    }
                }, 10, 100);

                timerFilter = new Timer();
                timerFilter.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        synchronized (Util.beaconsMap) {
                            for (Beacons beacons : Util.beaconsMap.values()) {

                                float kalmanRssi = Util.kalmanFilter.filter(beacons);
                                float meanRssi = Util.meanFilter.filter(beacons);
                                float armaRssi = Util.armaFilter.filter(beacons);


                                if (!Float.isNaN(kalmanRssi)) {
                                    beacons.setArmaRssi(armaRssi);
                                    beacons.setMeanRssi(meanRssi);
                                    beacons.setKalmanRssi(kalmanRssi);
                                }

                                float nowDistance = BeaconDistanceCalculator.calculateDistance((float) beacons.getRssiValue());
                                float kalmanRssiDist = BeaconDistanceCalculator.calculateDistance(kalmanRssi);
                                float meanRssiDist = BeaconDistanceCalculator.calculateDistance(meanRssi);
                                float armaDistance = BeaconDistanceCalculator.calculateDistance(armaRssi);

                                float nowDistance2 = BeaconDistanceCalculator.calculateDistanceFormula2((float) beacons.getRssiValue());
                                float kalmanRssiDist2 = BeaconDistanceCalculator.calculateDistanceFormula2(kalmanRssi);
                                float meanRssiDist2 = BeaconDistanceCalculator.calculateDistanceFormula2(meanRssi);
                                float armaDistance2 = BeaconDistanceCalculator.calculateDistanceFormula2(armaRssi);

                                beacons.setRssiDist(nowDistance);
                                beacons.setKalmanDist(kalmanRssiDist);
                                beacons.setMeanDist(meanRssiDist);
                                beacons.setArmaDist(armaDistance);

                                beacons.setRssiDist2(nowDistance2);
                                beacons.setKalmanDist2(kalmanRssiDist2);
                                beacons.setMeanDist2(meanRssiDist2);
                                beacons.setArmaDist2(armaDistance2);

                            }
                        }
                    }
                }, 10000, 1000);


                timerTrim = new Timer();
                timerTrim.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (Util.beaconsMap){
                            for (Beacons beacons : Util.beaconsMap.values()) {
                                beacons.trimAdvertisingPackets();
                                Log.i("TRIM", beacons.getBluetoothDevice().getAddress() + " size=" + beacons.getAdvertisingPackets().size());
                            }
                        }

                    }
                }, 60 * 1000, 20 * 1000);


                Util.makeTaost("Starting reading", getApplicationContext());
                listView.setAdapter(listAdapter);
            }
        });

        buttonStopRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopScanning();
                Util.makeTaost("Stop reading", getApplicationContext());
                export();
            }
        });


        buttonStartCollecting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Util.initBeaconAndTestPositions();
                Intent myIntent = new Intent(MainActivity.this, DataCollectingActivity.class);
                MainActivity.this.startActivity(myIntent);
                Util.makeTaost("Start collecting data", getApplicationContext());

            }
        });

        buttonGetLivePosition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Util.initBeaconAndTestPositions();
                Intent myIntent = new Intent(MainActivity.this, LivePosition.class);
                MainActivity.this.startActivity(myIntent);

            }
        });

        enableBluetoothAndLocation();

    }

    private ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (isDuplicate(result.getDevice().getAddress())) {
                synchronized (result.getDevice()) {
                    Beacons beacons = Util.beaconsMap.get(result.getDevice().getAddress());
                    AdvertisingPacket advertisingPacket = new AdvertisingPacket(result.getRssi());
                    beacons.advertisingPackets.add(advertisingPacket);
                    beacons.smootingAlgoritm(result);

                }
            } else {
                synchronized (result.getDevice()) {
                    Beacons beacons = new Beacons(result.getDevice(), getApplicationContext());
                    beacons.advertisingPackets.add(new AdvertisingPacket(result.getRssi()));
                    beacons.setRssiValue(result.getRssi());
                    Util.beaconsMap.put(result.getDevice().getAddress(), beacons);
                    Util.setBeaconsPosition();
                }
            }
        }

    };

    private boolean isDuplicate(String deviceAddress) {
        if (Util.beaconsMap.containsKey(deviceAddress))
            return true;
        else
            return false;
    }

    public void startScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ScanFilter> filters_v2 = new ArrayList<>();
                ScanFilter scanFilter1 = new ScanFilter.Builder()
                        .setDeviceAddress("C7:7E:A2:BD:51:4C").build();
                ScanFilter scanFilter2 = new ScanFilter.Builder()
                        .setDeviceAddress("D1:A4:D2:15:51:00").build();
                ScanFilter scanFilter3 = new ScanFilter.Builder()
                        .setDeviceAddress("D2:83:6A:5E:AB:F8").build();
                ScanFilter scanFilter4 = new ScanFilter.Builder()
                        .setDeviceAddress("C0:08:B4:0E:37:0E").build();
                ScanFilter scanFilter5 = new ScanFilter.Builder()
                        .setDeviceAddress("DF:08:5C:3A:4B:81").build();

                filters_v2.add(scanFilter1);
                filters_v2.add(scanFilter2);
                filters_v2.add(scanFilter3);
                filters_v2.add(scanFilter4);
                filters_v2.add(scanFilter5);

                ScanSettings setings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                        .setReportDelay(0L)
                        .build();

                bluetoothLeScanner.startScan(filters_v2, setings, leScanCallback);

            }
        });
    }

    public void stopScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(leScanCallback);
            }
        });

        cancelTimers();

    }

    public void export() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Time,Rssi,MeanRssi,KalmanRssi,ArmaRssi,RealDistance");


        for (FilterRecoard record : Util.filterRecoards) {
            data.append("\n" + record.getTimeReacord() + "," +
                    record.getUnfilteredRssi() + "," +
                    record.getMeanRssi() + "," +
                    record.getKalmanRssi() + "," +
                    record.getArmaRssi() + "," +
                    record.getRealDistance()
            );
        }

        try {
            //saving the file into device
            FileOutputStream out = openFileOutput("filter_-8dB.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "filter_-8dB.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.myapplication.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void enableBluetoothAndLocation() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }

    public void cancelTimers() {

        if (this.timerView != null) {
            this.timerView.cancel();
        }
        if (this.timerFilter != null) {
            this.timerFilter.cancel();
        }
        if (this.timerTrim != null) {
            this.timerTrim.cancel();
        }
        if (this.timerGetLocation != null) {
            this.timerGetLocation.cancel();
        }
    }

}