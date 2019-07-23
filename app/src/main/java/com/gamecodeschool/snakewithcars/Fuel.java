package com.gamecodeschool.snakewithcars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.Random;

class Fuel {

    private final Bitmap mBitmap;

    private final PointF mColliderCenter;
    private final int mColliderRadius;

    private final Random mRandom = new Random();

    Fuel(GameResources gameResources) {
        mBitmap = gameResources.getBitmapFuel();
        mColliderRadius = mBitmap.getWidth() / 2;

        // Hide the fuel until game begins
        mColliderCenter = new PointF(-1000, -1000);
    }

    void spawn(Point screenSize) {
        int max_x = screenSize.x - mColliderRadius;
        int max_y = screenSize.y - mColliderRadius;

        int x = Math.min(mRandom.nextInt(screenSize.x), max_x);
        int y = Math.min(mRandom.nextInt(screenSize.y), max_y);

        mColliderCenter.set(x, y);
    }

    void draw(Canvas canvas, Paint paint) {
        float left = mColliderCenter.x - mColliderRadius;
        float top = mColliderCenter.y - mColliderRadius;
        canvas.drawBitmap(mBitmap, left, top, paint);
    }

    PointF getColliderCenter() {
        return mColliderCenter;
    }

    float getColliderRadius() {
        return mColliderRadius;
    }
}
