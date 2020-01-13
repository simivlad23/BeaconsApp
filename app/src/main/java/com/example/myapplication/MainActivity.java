package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    BluetoothGatt bluetoothGatt;
    BluetoothLeScanner bluetoothLeScanner;
    private static final String TAG = "BLE-app";

    private final double RF_A =40; // the absolute energy which is represent by dBm at a distance of 1 meter from the transmitter
    private final double RF_N =2.3; // n is the signal transmission constant


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        listView = findViewById(R.id.listView);


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();



        Button buuton = findViewById(R.id.button1);
        buuton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                listView.setAdapter(null);

                List<String> itemList = new ArrayList<>();
                for(BluetoothDevice bt : pairedDevices) {

                    bluetoothGatt =  bt.connectGatt(MainActivity.this, true, gattCallback);
                    itemList.add(bt.getName());
                    System.out.println("-------------  " + bt.getAddress());
                    mDeviceList.add(bt.getName() + "\n" + bt.getAddress() + " RSSI: " + bluetoothGatt);


                    ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplication().getBaseContext(),
                            android.R.layout.simple_list_item_1, mDeviceList);

                    listView.setAdapter(listAdapter);

                }

                //BTAdapter.enable();
               // BTAdapter.startDiscovery();


            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MAX_VALUE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
              //  if (device.getAddress().equals("30:45:96:48:18:A6")) {
//                     if (device.getAddress().equals("94:65:2D:D5:94:BD")) {
                mDeviceList.add(device.getName() + "\n" + device.getAddress() + " RSSI: " + rssi   +"   distance: "+ getDistance(rssi,0.8));
                Log.i("BT", device.getName() + "\n" + device.getAddress());

                //TODO to print in a csv file rssi value and distance calculated
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
                 }

            System.out.println("Ceva");

           // }
        }
    };


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

//            if(status == BluetoothGatt.GATT_SUCCESS){
                System.out.println("Rsii "+ rssi);
                Log.d(TAG,String.format("BluetoothGatt ReadRssi [%d]",rssi));
                Log.i(TAG,"Distance is: "+ getDistance(rssi,1));
            //}
        }
    };

    public double getDistance(double rssi, double txPower)
    {
        double distance = (Math.pow(10d,-((rssi + RF_A)/10*RF_N)))/10.0;

        return distance;
    }
}
