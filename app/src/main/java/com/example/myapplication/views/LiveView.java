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

            initFloorPlan(canvas);

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
            paint.setColor(Color.CYAN);
            for (Point position : Util.testPosition) {
                canvas.drawRect(position.x,
                        position.y,
                        position.x + Util.BLOCK_SIZE,
                        position.y + Util.BLOCK_SIZE,
                        paint);
            }

            //####################  DRAW CURRENT POSITION  BASED MEAN RSSI VALUE ####################
            paint.setColor(Color.argb(255, 255, 0, 0));
            Position position = Util.calcutateBasedMeanRssi();
            Point scalePositionNow = Util.convertFromCmToPixels(position.getLat(), position.getLng());

            canvas.drawRect(scalePositionNow.x,
                    (scalePositionNow.y),
                    (scalePositionNow.x) + Util.BLOCK_SIZE,
                    (scalePositionNow.y) + Util.BLOCK_SIZE,
                    paint);


            //####################  DRAW CURRENT POSITION  ####################
            paint.setColor(Color.argb(255, 0, 255, 0));
            Position position2 = Util.calcutateBasedNowRssi();
            Point scalePositionNow2 = Util.convertFromCmToPixels(position2.getLat(), position2.getLng());

            canvas.drawRect(scalePositionNow2.x,
                    (scalePositionNow2.y),
                    (scalePositionNow2.x) + Util.BLOCK_SIZE,
                    (scalePositionNow2.y) + Util.BLOCK_SIZE,
                    paint);

            int x = (int) position.getLat();
            int y = (int) position.getLng();

            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            canvas.drawText("X:"+ x+ "  y:"+ y, (float) scalePositionNow.x, (float)scalePositionNow.y, paint);

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

    public void initFloorPlan(Canvas canvas) {

        //DRAW BACKGROUND WHITE
        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        float horizontalWallWidth = (float) (Util.WALL_WIDTH * Util.PIXELS_PER_CM_Y);
        float vericalWallWidth = (float) (Util.WALL_WIDTH * Util.PIXELS_PER_CM_X);

        Point point1 = Util.convertFromCmToPixels(440, 540);
        Point point2 = Util.convertFromCmToPixels(430, 750);
        Point point3 = Util.convertFromCmToPixels(120, 1055);
        Point point4 = Util.convertFromCmToPixels(580, 1055);


        //######### DRAW VERTICAL WALLS  #########
        paint.setStrokeWidth(vericalWallWidth);

        int offsetWall = (int) (vericalWallWidth/ 2);

        //left side
        canvas.drawLine(offsetWall, 0, offsetWall, Util.SCREEN_Y, paint);
        //right side
        canvas.drawLine(Util.SCREEN_X - offsetWall, 0, Util.SCREEN_X - offsetWall, Util.SCREEN_Y, paint);
        //middle up line
        canvas.drawLine(point1.x - offsetWall, 0, point1.x - offsetWall, point1.y, paint);
        //left down
        canvas.drawLine(point3.x + offsetWall, point2.y, point3.x + offsetWall, point3.y, paint);
        //right down
        canvas.drawLine(point4.x + offsetWall, point2.y, point4.x + offsetWall, point4.y, paint);


        //######### DRAW VERTICAL WALLS  #########
        paint.setStrokeWidth(horizontalWallWidth);

        offsetWall = (int) (horizontalWallWidth/ 2);

        //buttom side
        canvas.drawLine(0, (float) (Util.SCREEN_Y - offsetWall), Util.SCREEN_X, (float) (Util.SCREEN_Y - offsetWall), paint);
        //up side
        canvas.drawLine(offsetWall, offsetWall, Util.SCREEN_X, offsetWall, paint);
        //middle horizontal line
        canvas.drawLine(0, point1.y - offsetWall, Util.SCREEN_X, point1.y - offsetWall, paint);
        //second horizontal line
        canvas.drawLine(0, point2.y - offsetWall, Util.SCREEN_X, point2.y - offsetWall, paint);
        // last horizontal line
        canvas.drawLine(point3.x, point3.y - offsetWall, Util.SCREEN_X, point3.y - offsetWall, paint);


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
