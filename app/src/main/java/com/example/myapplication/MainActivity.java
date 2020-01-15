package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import java.util.Set;




public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    BluetoothGatt bluetoothGatt;
    private static final String TAG = "BLE-app";

    private final double RF_A =40; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private final double RF_N =2.3; // n is the signal transmission constant


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplication().getBaseContext(),
                android.R.layout.simple_list_item_1, mDeviceList);


        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mDeviceList));
        Button buuton = findViewById(R.id.button1);
        buuton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listView.setAdapter(null);
                for(BluetoothDevice bt : pairedDevices) {

                    bluetoothGatt =  bt.connectGatt(MainActivity.this, true, gattCallback);
                    listView.setAdapter(listAdapter);

                }
            }
        });
    }


    protected BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange() - STATE_CONNECTED");
                bluetoothGatt = gatt;

                boolean rssiStatus = bluetoothGatt.readRemoteRssi();
                boolean discoverServicesOk = gatt.discoverServices();
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

           if(status == BluetoothGatt.GATT_SUCCESS){

               //TODO save the rssi values in a file

                mDeviceList.add(gatt.getDevice().getName() + "\n" + gatt.getDevice().getAddress() + " RSSI: " + rssi);
                Log.d(TAG,String.format("BluetoothGatt ReadRssi [%d]",rssi));
                Log.i(TAG,"Distance is: "+ getDistance(rssi,1));
            }
        }
    };

    public double getDistance(double rssi, double txPower)
    {
        double distance = (Math.pow(10d,-((rssi + RF_A)/10*RF_N)))/10.0;

        return distance;
    }
}
