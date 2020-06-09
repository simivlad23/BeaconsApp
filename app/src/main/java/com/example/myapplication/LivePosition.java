package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.example.myapplication.model.Position;
import com.example.myapplication.model.PredictModel;
import com.example.myapplication.liveviews.LiveView;
import com.example.myapplication.util.Util;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LivePosition extends AppCompatActivity {

    private LiveView liveView;
    private static final String DELOPY_DOMAIN = "testcmd23.nw.r.appspot.com";
    private static final String IP_LOCAL_NETWORK = "192.168.0.192:5000";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String postUrl = "http://" + DELOPY_DOMAIN + "/predict_location";
    private Gson gson = new Gson();
    private Timer timerGetLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        liveView = new LiveView(this, size);
        setContentView(liveView);

        timerGetLocation = new Timer();
        timerGetLocation.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        PredictModel model = new PredictModel(1, 1, 1, 1);
                        synchronized (Util.beaconsMap) {
                            for (Beacons beacons : Util.beaconsMap.values()) {
                                switch (beacons.getBluetoothDevice().getAddress()) {
                                    case "C7:7E:A2:BD:51:4C":
                                        model.setBeaconC7(beacons.getKalmanRssi());
                                        break;
                                    case "D2:83:6A:5E:AB:F8":
                                        model.setBeaconD2(beacons.getKalmanRssi());
                                        break;
                                    case "D1:A4:D2:15:51:00":
                                        model.setBeaconD1(beacons.getKalmanRssi());
                                        break;
                                    case "C0:08:B4:0E:37:0E":
                                        model.setBeaconC0(beacons.getKalmanRssi());
                                        break;
                                }
                            }
                        }

                        String json = gson.toJson(model);
                        RequestBody body = RequestBody.create(JSON, json);
                        postRequest(postUrl, body);

                    }
                });
            }
        }, 1500, 1000);

    }

    void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                System.out.println(e.getMessage());
                call.cancel();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String resBody = response.body().string();
                System.out.println(resBody);
                Position position = gson.fromJson(resBody, Position.class);
                synchronized (Util.predictPosition) {
                    Util.predictPosition.setLat(position.getLat());
                    Util.predictPosition.setLat(position.getLat());
                }

            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        liveView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timerGetLocation != null){
            timerGetLocation.cancel();
        }
        liveView.pause();
    }

}
