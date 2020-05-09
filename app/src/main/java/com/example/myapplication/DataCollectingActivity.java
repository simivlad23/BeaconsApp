package com.example.myapplication;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.colectview.DataCollectingView;
import com.example.myapplication.model.MLReacord;

public class DataCollectingActivity extends AppCompatActivity {

    DataCollectingView dataCollectingView;
    ImageButton buttonUp;
    ImageButton buttonDown;
    ImageButton buttonLeft;
    ImageButton buttonRight;
    ImageButton buttonSend;

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
                Point point = Util.convertFromPixelToCm(dataCollectingView.presOnScreenX, dataCollectingView.presOnScreeny);
                Util.makeTaost("Change the position and wait 5 sec", getApplicationContext());
                Log.i("COLECT LOCATION", "x= " + point.x + "   y= " + point.y);

                MLReacord mlReacord = new MLReacord();
                mlReacord.setTimeReacord(Util.convertFromEpochToDate());
                mlReacord.setBeacon1(Util.beaconsMap.get("C7:7E:A2:BD:51:4C").getAverageRssiValue());
                mlReacord.setBeacon2(Util.beaconsMap.get("D2:83:6A:5E:AB:F8").getAverageRssiValue());
                mlReacord.setBeacon3(Util.beaconsMap.get("D1:A4:D2:15:51:00").getAverageRssiValue());
                mlReacord.setBeacon4(Util.beaconsMap.get("C0:08:B4:0E:37:0E").getAverageRssiValue());
                mlReacord.setX(point.x);
                mlReacord.setY(point.y);

                Util.db.collection("rssi_beacon").add(mlReacord);
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
        dataCollectingView.pause();
    }

}
