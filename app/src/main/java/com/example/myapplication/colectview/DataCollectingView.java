package com.example.myapplication.colectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myapplication.Util;

public class DataCollectingView extends SurfaceView implements Runnable {

    private Thread thread = null;
    private Context context;

    public int presOnScreenX = 0;
    public int presOnScreeny = 0;

    private volatile boolean isReading;
    public volatile boolean update = false;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Paint paint;

    public DataCollectingView(Context context) {
        super(context);
        init(context);
    }

    public DataCollectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DataCollectingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        surfaceHolder = getHolder();
        paint = new Paint();

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

            //####################  DRAW TOUCH POSITION ####################
            paint.setColor(Color.argb(255, 255, 0, 0));
            canvas.drawRect(presOnScreenX,
                    presOnScreeny,
                    presOnScreenX + Util.BLOCK_SIZE,
                    presOnScreeny + Util.BLOCK_SIZE,
                    paint);

            paint.setColor(Color.RED);
            paint.setTextSize(40);
            Point nowLocation = Util.convertFromPixelToCm(presOnScreenX, presOnScreeny);
            canvas.drawText("X:" + nowLocation.x + "  y:" + nowLocation.y, (float) presOnScreenX, (float) presOnScreeny, paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
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

        int offsetWall = (int) (vericalWallWidth / 2);

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


        //######### DRAW HORIZONTALS WALLS  #########
        paint.setStrokeWidth(horizontalWallWidth);

        offsetWall = (int) (horizontalWallWidth / 2);

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
    public void run() {
        while (isReading) {
            if (update) {
                draw();
                update = false;
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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                presOnScreenX = (int) motionEvent.getX();
                presOnScreeny = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                update = true;
                break;
        }
        return true;
    }
}
