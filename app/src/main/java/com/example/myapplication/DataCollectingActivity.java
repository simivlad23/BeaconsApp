package com.example.myapplication;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.colectview.DataCollectingView;
import com.example.myapplication.model.FilterRecoard;
import com.example.myapplication.util.Util;

import java.util.Timer;
import java.util.TimerTask;

public class DataCollectingActivity extends AppCompatActivity {

    DataCollectingView dataCollectingView;
    ImageButton buttonUp;
    ImageButton buttonDown;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonSend;
    boolean isCollecting = false;
    Timer timerSendData;

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

                if (isCollecting) {
                    timerSendData.cancel();
                    Util.makeTaost("Collecting was stopped", getApplicationContext());
                    isCollecting = false;
                } else {

                    Util.makeTaost("Start Collecting in 5 sec", getApplicationContext());

                    isCollecting = true;
                    timerSendData = new Timer();
                    timerSendData.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            Point point = Util.convertFromPixelToCm(dataCollectingView.presOnScreenX, dataCollectingView.presOnScreeny);
                            synchronized (Util.beaconsMap) {
                                for (Beacons beacons : Util.beaconsMap.values()) {
//                                    FilterRssi filterRssi = new FilterRssi(beacons.getRssiValue(),
//                                            beacons.getMeanRssi(),
//                                            beacons.getKalmanRssi(),
//                                            beacons.getArmaRssi(),
//                                            beacons.getRssiDist(),
//                                            beacons.getMeanDist(),
//                                            beacons.getKalmanDist(),
//                                            beacons.getArmaDist(),
//                                            beacons.getRssiDist2(),
//                                            beacons.getMeanDist2(),
//                                            beacons.getKalmanDist2(),
//                                            beacons.getArmaDist2(),
//                                            point.y);

                                    FilterRecoard filterRssi;
                                    synchronized (beacons) {
                                        filterRssi = new FilterRecoard(beacons.getRssiValue(),
                                                beacons.getMeanRssi(),
                                                beacons.getKalmanRssi(),
                                                beacons.getArmaRssi(),
                                                point.y);
                                    }

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Util.makeTaost("Colect",getApplicationContext());
                                        }
                                    });

                                    //Log.i("COLLECT", "Collecting: " + filterRssi);
                                    Util.db.collection("filters_-0dB").add(filterRssi);
                                }
                            }
                        }
                    }, 5 * 1000, 2500);
                }
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
        if (timerSendData != null) {
            timerSendData.cancel();
        }
        dataCollectingView.pause();

    }

}
