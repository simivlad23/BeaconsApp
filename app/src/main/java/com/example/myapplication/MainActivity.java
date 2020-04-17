package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.RssiRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothProfile.GATT;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BLE-app";

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private WifiManager wifiManager;

    private ArrayAdapter<String> listAdapter;
    private TextView textViewLat;
    private TextView textViewLng;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        //wifiManager.startScan();
        Util.wifiList = wifiManager.getScanResults();
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mDeviceList);
        listView.setAdapter(listAdapter);

        Button buttonStartDetect = findViewById(R.id.button1);
        Button buttonStopRead = findViewById(R.id.button2);
        Button buttonNrDevices = findViewById(R.id.button3);
        Button buttonStartReading = findViewById(R.id.button4);
        Button buttonSetTimeReading = findViewById(R.id.button5);
        Button buttongetPosition = findViewById(R.id.button6);

        textViewLat = findViewById(R.id.textView3);
        textViewLng = findViewById(R.id.textView5);



        buttonStartDetect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (BluetoothDevice bt : pairedDevices) {
                    Beacons beacons = new Beacons(bt, MainActivity.this);
                    beacons.connectToGATT();
                    Util.beaconsList.add(beacons);
                }


                final Timer timerWifi = new Timer();
                timerWifi.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        wifiManager.startScan();
                        Util.wifiList= wifiManager.getScanResults();
                    }
                },0,300);


                final Timer timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceList.clear();
                                for (Beacons beacons : Util.beaconsList) {

                                    mDeviceList.add(beacons.getBluetoothDevice().getAddress() + " rssi: " + beacons.getRssiValue() + "; dis:" + beacons.getDistance());
                                    listAdapter.notifyDataSetChanged();

                                }
                                for (ScanResult scanResult : Util.wifiList) {
                                    int level = scanResult.level;
                                    mDeviceList.add(scanResult.BSSID + " rssi: " + level + "; dis:" + Util.getDistance2(level,4));
                                    listAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                    }
                }, 500, 300);


                Util.makeTaost("Starting reading", getApplicationContext());
                listView.setAdapter(listAdapter);
            }
        });
        buttonStopRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (Beacons beacons : Util.beaconsList) {
                    beacons.stopReacording();
                }
                Util.makeTaost("Stop reading", getApplicationContext());
                export();
            }
        });
        buttonNrDevices.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
                List<BluetoothDevice> connected = manager.getConnectedDevices(GATT);
                Log.i("Connected Devices: ", connected.size() + "");

            }
        });

        buttonStartReading.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (Beacons beacons : Util.beaconsList) {
                    beacons.startReading();
                }
                Util.makeTaost("Start reading", getApplicationContext());


            }
        });

        buttonSetTimeReading.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Util.initStartDateReading();
                Util.makeTaost("Time was seting", getApplicationContext());


            }
        });

        buttongetPosition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Util.setBeaconsPosition();

                final Timer timier2 = new Timer();
                timier2.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Position position = Util.calcutate();
                                //Util.updateBeaconsDistances();
                                Util.positionsList.add(position);

                            }
                        });

                    }
                }, 1500, 1500);

                final Timer timier3 = new Timer();
                timier2.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Position position = Util.getMedianPosition();
                                textViewLat.setText(String.valueOf(position.getLat()));
                                textViewLng.setText(String.valueOf(position.getLng()));

                            }
                        });

                    }
                }, 1600, 1500);


            }
        });


    }

    public void export() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Time,Name,Addres,RSSI,distance");

        Util.filterRecorderList();

        for (RssiRecord record : Util.recordsList) {
            data.append("\n" + record.getTimeReacord() + "," + record.getDeviceName() + "," + record.getDeviceAddress() + "," + String.valueOf(record.getRssiValue()) + "," + String.valueOf(record.getDistanceCalculated()));
        }

        try {
            //saving the file into device
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
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
}