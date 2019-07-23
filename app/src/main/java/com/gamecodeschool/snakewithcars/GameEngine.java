package com.gamecodeschool.snakewithcars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressWarnings("ViewConstructor")
class GameEngine extends SurfaceView implements Runnable {

    static final boolean DEBUGGING = false;

    // for the game loop/thread
    private Thread mThread = null;
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    private long mFPS;

    private final Point mScreenSize;

    private final GameResources mGameResources;

    private final Paint mPaint;

    private final Fuel mFuel;
    private final Car mCar;

    private int mScore;

    GameEngine(Context context, Point screenSize) {
        super(context);

        mScreenSize = new Point(screenSize);

        mGameResources = new GameResources(context, screenSize);

        mPaint = new Paint();

        // I will instantiate only one fuel and move it around with spawn
        mFuel = new Fuel(mGameResources);
        mCar = new Car(mGameResources, screenSize);
    }

    private void newGame() {
        mScore = 0;

        mCar.spawn(mScreenSize);
        mFuel.spawn(mScreenSize);
    }

    void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

    void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            if (!mPaused) {
                update();
                detectCollisions();
            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                final long MILLIS_IN_SECOND = 1000;
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    private void update() {
        mCar.update(mFPS);
    }

    private void detectCollisions() {
        if (mCar.runsOver(mFuel)) {
            mScore += 1;
            mGameResources.playEatSound();
//            mCar.addSegment();
            mFuel.spawn(mScreenSize);
        }

        if (mCar.crashes(mScreenSize)) {
            mPaused = true;
            mGameResources.playCrashSound();
        }
    }

    private void draw() {
        SurfaceHolder surfaceHolder = getHolder();

        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(120);
            canvas.drawText("Score: " + mScore, 20, 120, mPaint);

            if (mPaused) {
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(250);
                float textWidth = mPaint.measureText(getResources().getString(R.string.tap_to_play));
                canvas.drawText(getResources().getString(R.string.tap_to_play),
                        (float) mScreenSize.x / 2 - textWidth / 2, (float) mScreenSize.y / 2, mPaint);
            }

            mFuel.draw(canvas, mPaint);
            mCar.draw(canvas, mPaint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int i = event.getActionIndex();
        int touchX = (int) event.getX(i);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mPaused) {
                    newGame();
                    mPaused = false;
                } else {
                    mCar.stopTurning();
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (touchX < mScreenSize.x / 2) {
                    mCar.startLeftTurn();
                } else {
                    mCar.startRightTurn();
                }
                break;
        }

        return true;
    }
}
