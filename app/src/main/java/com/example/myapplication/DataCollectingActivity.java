package com.example.myapplication;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.colectview.DataCollectingView;
import com.example.myapplication.model.FilterRssi;
import com.example.myapplication.model.MLReacord;

import java.util.Timer;
import java.util.TimerTask;

public class DataCollectingActivity extends AppCompatActivity {

    DataCollectingView dataCollectingView;
    ImageButton buttonUp;
    ImageButton buttonDown;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonSend;
    Timer timier2;
    Timer timier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collecting);
        dataCollectingView = findViewById(R.id.colectSurface);
        buttonUp = findViewById(R.id.button_up);
        buttonDown = findViewById(R.id.botton_down);
        buttonLeft = findViewById(R.id.button_left);
        buttonRight = findViewById(R.id.button_right);
        buttonSend = findViewById(R.id.button_send);

        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataCollectingView.presOnScreeny > 5) {
                    dataCollectingView.presOnScreeny = dataCollectingView.presOnScreeny - 5;
                    dataCollectingView.update = true;
                }
            }
        });

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataCollectingView.presOnScreeny < Util.SCREEN_Y - 5) {
                    dataCollectingView.presOnScreeny = dataCollectingView.presOnScreeny + 5;
                    dataCollectingView.update = true;
                }
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataCollectingView.presOnScreenX > 5) {
                    dataCollectingView.presOnScreenX = dataCollectingView.presOnScreenX - 5;
                    dataCollectingView.update = true;
                }
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataCollectingView.presOnScreenX < Util.SCREEN_X - 5) {
                    dataCollectingView.presOnScreenX = dataCollectingView.presOnScreenX + 5;
                    dataCollectingView.update = true;
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.makeTaost("Start Collecting", getApplicationContext());

//                timier = new Timer();
//                timier.scheduleAtFixedRate(new TimerTask() {
//                    @Override
//                    public void run() {
//                        Point point = Util.convertFromPixelToCm(dataCollectingView.presOnScreenX, dataCollectingView.presOnScreeny);
//                        MLReacord mlReacord = new MLReacord();
//                        mlReacord.setTimeReacord(Util.convertFromEpochToDate());
//                        mlReacord.setBeaconC7(Util.beaconsMap.get("C7:7E:A2:BD:51:4C").getAverageRssiValue());
//                        mlReacord.setBeaconD2(Util.beaconsMap.get("D2:83:6A:5E:AB:F8").getAverageRssiValue());
//                        mlReacord.setBeaconD1(Util.beaconsMap.get("D1:A4:D2:15:51:00").getAverageRssiValue());
//                        mlReacord.setBeaconC0(Util.beaconsMap.get("C0:08:B4:0E:37:0E").getAverageRssiValue());
//                        mlReacord.setX(point.x);
//                        mlReacord.setY(point.y);
//
//                        //TODO Save
//                        //TODO was stoped save to firestore
//                        //Util.db.collection("rssi_beacon_2").add(mlReacord);
//
//                    }
//                }, 0, 1500);


                //############### SAVE TO FIREBASE FILTER COMPORATION ##########
                timier2 = new Timer();
                timier2.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Point point = Util.convertFromPixelToCm(dataCollectingView.presOnScreenX, dataCollectingView.presOnScreeny);
                        for(Beacons beacons: Util.beaconsMap.values())
                        {
                            FilterRssi filterRssi = new FilterRssi(beacons.getRssiValue(),
                                                                    beacons.getMeanRssi(),
                                                                    beacons.getKalmanRssi(),
                                                                    beacons.getArmaRssi(),
                                                                    beacons.getRssiDist(),
                                                                    beacons.getMeanDist(),
                                                                    beacons.getKalmanDist(),
                                                                    beacons.getArmaDist(),
                                                                    point.y);
                                Util.db.collection("filter_comparation2").add(filterRssi);
                        }
                    }
                }, 0, 1000);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataCollectingView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //timier.cancel();
        timier2.cancel();
        dataCollectingView.pause();

    }

}
