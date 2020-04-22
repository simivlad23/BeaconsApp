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
    private static int FOOR_WIDE = 5;
    private static int FLOOR_HEIGHT = 8;

    private int blockSize;

    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    private long nextFrameTime;
    private final long FPS = 2;
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
            Iterator<Beacons> iterator = Util.beaconsList.iterator();

            while (iterator.hasNext()){

                Beacons beacons = iterator.next();
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


            //####################  DRAW CURRENT POSITION ####################
            paint.setColor(Color.argb(255, 255, 0, 0));
            Position position = Util.calcutate();
            Point scalePositionNow = convertCoordinates(position.getLat(),position.getLng());

            canvas.drawRect(scalePositionNow.x * blockSize,
                    (scalePositionNow.y * blockSize),
                    (scalePositionNow.x * blockSize) + blockSize,
                    (scalePositionNow.y * blockSize) + blockSize,
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
        double scaleX = x / FOOR_WIDE;
        double scaleY = y / FLOOR_HEIGHT;

        int newX = (int) (scaleX * NUM_BLOCKS_WIDE);
        int newY = (int) (scaleY * numBlocksHigh);

        return new Point(newX,newY);
    }
}
