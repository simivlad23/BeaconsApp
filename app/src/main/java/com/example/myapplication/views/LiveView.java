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

public class LiveView extends SurfaceView implements Runnable {


    private Thread thread = null;
    private Context context;

    private int screenX;
    private int screenY;

    //measured in meters
    private static double FOOR_WIDE = 8.7;
    private static double FLOOR_HEIGHT = 14.3 ;

    private int blockSize;

    private final int NUM_BLOCKS_WIDE = 100;
    private int numBlocksHigh;

    private long nextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_PER_SECOND = 1000;

    private volatile boolean isPlaying;


    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private Drawable flooriamge;

    public LiveView(Context context, Point size) {
        super(context);
        this.context = context;
        flooriamge = context.getResources().getDrawable(R.drawable.house_plan);

        screenX = size.x;
        screenY = size.y;

        blockSize = screenX / NUM_BLOCKS_WIDE;
        numBlocksHigh = screenY / blockSize;

        surfaceHolder = getHolder();
        paint = new Paint();

        nextFrameTime = System.currentTimeMillis();

    }

    @Override
    public void run() {

        while (isPlaying) {
            if (updateRequired()) {
                draw();
            }

        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void draw() {

        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            Rect imageBounds = canvas.getClipBounds();

            flooriamge.setBounds(imageBounds);
            flooriamge.draw(canvas);

            paint.setColor(Color.BLUE);

            for(Beacons beacons: Util.beaconsList){

                double beaconX = beacons.getLat();
                double beaconY = beacons.getLng();
                Point scalePositionBeacon = convertCoordinates(beaconX,beaconY);

                Log.d("POZ"," x= "+ scalePositionBeacon.x * blockSize +" ; y="+ scalePositionBeacon.y * blockSize );
                canvas.drawRect(scalePositionBeacon.x * blockSize,
                        (scalePositionBeacon.y * blockSize),
                        (scalePositionBeacon.x  * blockSize) + blockSize,
                        (scalePositionBeacon.y * blockSize) + blockSize,
                        paint);
            }

            //####################  DRAW CURRENT POSITION  BASED MEAN RSSI VALUE ####################
            paint.setColor(Color.argb(255, 255, 0, 0));
            Position position = Util.calcutateBasedMeanRssi();
            Point scalePositionNow = convertCoordinates(position.getLat(),position.getLng());

            canvas.drawRect(scalePositionNow.x * blockSize,
                    (scalePositionNow.y * blockSize),
                    (scalePositionNow.x * blockSize) + blockSize,
                    (scalePositionNow.y * blockSize) + blockSize,
                    paint);


            //####################  DRAW CURRENT POSITION  ####################
            paint.setColor(Color.argb(255, 0, 255, 0));
            Position position2 = Util.calcutateBasedNowRssi();
            Point scalePositionNow2 = convertCoordinates(position2.getLat(),position2.getLng());

            canvas.drawRect(scalePositionNow2.x * blockSize,
                    (scalePositionNow2.y * blockSize),
                    (scalePositionNow2.x * blockSize) + blockSize,
                    (scalePositionNow2.y * blockSize) + blockSize,
                    paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        if (nextFrameTime <= System.currentTimeMillis()) {
            nextFrameTime = System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TAG", "touched down");
                Util.makeTaost("touched down",context);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("TAG", "moving: (" + x + ", " + y + ")");
                Util.makeTaost("moving: (" + x + ", " + y + ")",context);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("TAG", "touched up");
                Util.makeTaost("touched up",context);
                break;
        }

        return true;

    }

    public Point convertCoordinates(double x, double y) {
        //added 1 because of margin
        double scaleX = (x + Util.MARGIN_LEFT) / FOOR_WIDE;
        double scaleY = (y + Util.MARGIN_UP) / FLOOR_HEIGHT;

        int newX = (int) (scaleX * NUM_BLOCKS_WIDE);
        int newY = (int) (scaleY * numBlocksHigh);

        return new Point(newX,newY);
    }
}
