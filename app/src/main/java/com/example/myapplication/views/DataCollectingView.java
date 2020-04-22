package com.example.myapplication.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myapplication.Beacons;
import com.example.myapplication.R;
import com.example.myapplication.Util;
import com.example.myapplication.model.Position;

import java.util.Iterator;

public class DataCollectingView extends SurfaceView implements Runnable {

    private Thread thread = null;
    private Context context;

    private int screenX;
    private int screenY;

    //measured in meters
    private static int FOOR_WIDE = 5;
    private static int FLOOR_HEIGHT = 8;

    private int blockSize;

    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    private int presOnScreenX=0;
    private int presOnScreeny=0;

    private volatile boolean isReading;
    private volatile boolean update = false;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Drawable flooriamge;

    public DataCollectingView(Context context, Point size) {
        super(context);
        this.context = context;
        flooriamge = context.getResources().getDrawable(R.drawable.house_plan);

        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;

        surfaceHolder = getHolder();
        paint = new Paint();

    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            Rect imageBounds = canvas.getClipBounds();

            flooriamge.setBounds(imageBounds);
            flooriamge.draw(canvas);

            paint.setColor(Color.BLUE);
            Iterator<Beacons> iterator = Util.beaconsList.iterator();

            while (iterator.hasNext()) {

                Beacons beacons = iterator.next();
                double beaconX = beacons.getLat();
                double beaconY = beacons.getLng();
                Point scalePositionBeacon = convertCoordinates(beaconX, beaconY);

                Log.d("POZ", " x= " + scalePositionBeacon.x * blockSize + " ; y=" + scalePositionBeacon.y * blockSize);
                canvas.drawRect(scalePositionBeacon.x * blockSize,
                        (scalePositionBeacon.y * blockSize),
                        (scalePositionBeacon.x * blockSize) + blockSize,
                        (scalePositionBeacon.y * blockSize) + blockSize,
                        paint);
            }

            paint.setColor(Color.argb(255, 255, 0, 0));
            canvas.drawRect(presOnScreenX ,
                    presOnScreeny ,
                    presOnScreenX + blockSize,
                    presOnScreeny  + blockSize,
                    paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TAG", "touched down");

                presOnScreenX = (int) motionEvent.getX();
                presOnScreeny =  (int) motionEvent.getY();

                Util.makeTaost("", context);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("TAG", "moving:");
                break;
            case MotionEvent.ACTION_UP:
                update=true;
                Log.i("TAG", "touched up");
                break;
        }

        return true;

    }

    public Point convertCoordinates(double x, double y) {
        double scaleX = x / FOOR_WIDE;
        double scaleY = y / FLOOR_HEIGHT;

        int newX = (int) (scaleX * NUM_BLOCKS_WIDE);
        int newY = (int) (scaleY * numBlocksHigh);

        return new Point(newX, newY);
    }


    @Override
    public void run() {

        while (isReading) {
            if (update) {
                draw();
                update=false;
            }

        }
    }

    public void pause() {
        isReading = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        isReading = true;
        thread = new Thread(this);
        thread.start();
    }


}
