package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.model.RssiRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BLE-app";

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> listAdapter;
    private List<Beacons> beaconsList = new ArrayList<>();

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
                listView.setAdapter(listAdapter);
            }
        });
        buttonStopRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for(Beacons beacons : beaconsList){
                    beacons.stopReacording();
                }
                export();
            }
        });
    }

    public void export() {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Name,Addres,RSSI");

        for (RssiRecord record : Util.recordsList) {
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