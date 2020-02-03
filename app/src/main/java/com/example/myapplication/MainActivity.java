package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.model.RSSI_Record;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "example2.txt";


    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    BluetoothGatt bluetoothGatt;
    BluetoothGatt bluetoothGatt1;
    BluetoothGatt bluetoothGatt2;
    BluetoothGatt bluetoothGatt3;
    BluetoothGatt bluetoothGatt4;

    private static final String TAG = "BLE-app";
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayAdapter<String> listAdapter;
    private List<RSSI_Record> records = new ArrayList<>();
    private List<Beacons> beaconsList = new ArrayList<>();


    private final double RF_A = 40; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private final double RF_N = 2.3; // n is the signal transmission constant


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mDeviceList);

        listView.setAdapter(listAdapter);

        Button buttonStartRead = findViewById(R.id.button1);
        Button buttonStopRead = findViewById(R.id.button2);


        buttonStartRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                for(BluetoothDevice bt : pairedDevices){
                    Beacons beacons = new Beacons(bt,MainActivity.this);
                    beaconsList.add(beacons);
                }

//                int i = 0;
//                for (BluetoothDevice bt : pairedDevices) {
//
//                    if (i == 0) {
//                        bluetoothGatt = bt.connectGatt(MainActivity.this, true, gattCallback);
//                    }
//                    if (i == 1) {
//                        bluetoothGatt1 = bt.connectGatt(MainActivity.this, true, gattCallback1);
//                    }
//                    if (i == 2) {
//                        bluetoothGatt2 = bt.connectGatt(MainActivity.this, true, gattCallback2);
//                    }
//                    if (i == 3) {
//                        bluetoothGatt3 = bt.connectGatt(MainActivity.this, true, gattCallback3);
//                    }
//                    if (i == 4) {
//                        bluetoothGatt4 = bt.connectGatt(MainActivity.this, true, gattCallback4);
//                    }
//
//                    i++;
//                }
                listView.setAdapter(listAdapter);
            }
        });
        buttonStopRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for(Beacons beacons : beaconsList){
                    beacons.stopReacording();
                }
//                bluetoothGatt.close();
//                bluetoothGatt.disconnect();
//
//                bluetoothGatt1.close();
//                bluetoothGatt1.disconnect();
//
//                bluetoothGatt2.close();
//                bluetoothGatt2.disconnect();
//
//                bluetoothGatt3.close();
//                bluetoothGatt3.disconnect();
//
//                bluetoothGatt4.close();
//                bluetoothGatt4.disconnect();
                export();

            }
        });
    }

    protected BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        Timer timier;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt = gatt;

                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean rssiStatus = bluetoothGatt.readRemoteRssi();
                    }
                }, 0, 1000);

                boolean discoverServicesOk = gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "status is " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = getDistance(rssi, 4);


                records.add(new RSSI_Record(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + getDistance(rssi, 1), rssi));

            }
        }
    };

    protected BluetoothGattCallback gattCallback1 = new BluetoothGattCallback() {
        Timer timier;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt1 = gatt;

                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean rssiStatus = bluetoothGatt1.readRemoteRssi();
                    }
                }, 0, 1000);

                boolean discoverServicesOk = gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "status is " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = getDistance(rssi, 4);


                records.add(new RSSI_Record(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + getDistance(rssi, 1), rssi));
                Log.i(TAG, "Distance is: " + getDistance(rssi, 1));
            }
        }
    };

    protected BluetoothGattCallback gattCallback2 = new BluetoothGattCallback() {
        Timer timier;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt2 = gatt;

                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean rssiStatus = bluetoothGatt2.readRemoteRssi();
                    }
                }, 0, 1000);

                boolean discoverServicesOk = gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "status is " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = getDistance(rssi, 4);


                records.add(new RSSI_Record(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + getDistance(rssi, 1), rssi));

            }
        }
    };

    protected BluetoothGattCallback gattCallback3 = new BluetoothGattCallback() {
        Timer timier;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());
                bluetoothGatt3 = gatt;

                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean rssiStatus = bluetoothGatt3.readRemoteRssi();
                    }
                }, 0, 1000);

                boolean discoverServicesOk = gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "status is " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = getDistance(rssi, 4);


                records.add(new RSSI_Record(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + getDistance(rssi, 1), rssi));

            }
        }
    };

    protected BluetoothGattCallback gattCallback4 = new BluetoothGattCallback() {
        Timer timier;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED   " + gatt.getDevice().getAddress());


                bluetoothGatt4 = gatt;

                timier = new Timer();
                timier.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        boolean rssiStatus = bluetoothGatt4.readRemoteRssi();
                    }
                }, 0, 1000);
                boolean discoverServicesOk = gatt.discoverServices();


            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_DISCONNECTED  " + gatt.getDevice().getAddress());
                timier.cancel();
                timier = null;
            }
        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "status is " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                String deviceName = gatt.getDevice().getName();
                String deviceAddress = gatt.getDevice().getAddress();
                double distanceCalculated = getDistance(rssi, 4);


                records.add(new RSSI_Record(deviceName, deviceAddress, rssi, distanceCalculated));
                Log.d(TAG, String.format("BluetoothGatt ReadRssi from " + gatt.getDevice().getAddress() + " value : [%d]  and distance calculated :" + getDistance(rssi, 1), rssi));

            }
        }
    };

    public double getDistance(double rssi, double txPower) {
        double distance = (Math.pow(10d, -((rssi + RF_A) / 10 * RF_N))) / 10.0;

        return distance;
    }


    public void export() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Name,Addres,RSSI");

        for (RSSI_Record record : Util.recordsList) {
            data.append("\n" + record.getDeviceName() + "," + record.getDeviceAddress() + "," + String.valueOf(record.getRssiValue()));
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