package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.example.myapplication.views.DataCollectingView;
import com.example.myapplication.views.LiveView;

import java.util.Timer;
import java.util.TimerTask;

public class DataCollectingActivity extends AppCompatActivity {

    DataCollectingView  dataCollectingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        dataCollectingView= new DataCollectingView(this,size);
        setContentView(dataCollectingView);

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
