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
        flooriamge = context.getResources().getDrawable(R.drawable.house_plan_crop);

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
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            Rect imageBounds = canvas.getClipBounds();

            flooriamge.setBounds(imageBounds);
            flooriamge.draw(canvas);
            //####################  DRAW BEACONS POSITION ##################
            paint.setColor(Color.BLUE);

            for (Point position : Util.beaconsPosition) {
                canvas.drawRect((position.x),
                        (position.y),
                        (position.x) + Util.BLOCK_SIZE,
                        (position.y) + Util.BLOCK_SIZE,
                        paint);
            }

            //####################  DRAW TEST POSITION ####################
            paint.setColor(Color.YELLOW);

            for (Point position : Util.testPosition) {
                canvas.drawRect((position.x),
                        (position.y),
                        (position.x) + Util.BLOCK_SIZE,
                        (position.y) + Util.BLOCK_SIZE,
                        paint);
            }

            //####################  DRAW CURRENT POSITION  BASED MEAN RSSI VALUE ####################
            paint.setColor(Color.argb(255, 255, 0, 0));
            Position position = Util.calcutateBasedMeanRssi();
            Point scalePositionNow = Util.convertCoordinates(position.getLat(), position.getLng());

            canvas.drawRect(scalePositionNow.x,
                    (scalePositionNow.y),
                    (scalePositionNow.x) + Util.BLOCK_SIZE,
                    (scalePositionNow.y) + Util.BLOCK_SIZE,
                    paint);


            //####################  DRAW CURRENT POSITION  ####################
            paint.setColor(Color.argb(255, 0, 255, 0));
            Position position2 = Util.calcutateBasedNowRssi();
            Point scalePositionNow2 = Util.convertCoordinates(position2.getLat(), position2.getLng());

            canvas.drawRect(scalePositionNow2.x,
                    (scalePositionNow2.y),
                    (scalePositionNow2.x) + Util.BLOCK_SIZE,
                    (scalePositionNow2.y) + Util.BLOCK_SIZE,
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

        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TAG", "moving: (" + x + ", " + y + ")");
                Util.makeTaost("moving: (" + x + ", " + y + ")", context);
                break;
        }

        return true;

    }
}
