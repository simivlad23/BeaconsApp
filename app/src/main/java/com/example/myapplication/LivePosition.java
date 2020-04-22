package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.example.myapplication.views.LiveView;

public class LivePosition extends AppCompatActivity {

    LiveView liveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        liveView = new LiveView(this,size);
        Log.i("DISPLAY","x ="+ size.x+ "  y="+size.y);
        setContentView(liveView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        liveView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        liveView.pause();
    }

}
