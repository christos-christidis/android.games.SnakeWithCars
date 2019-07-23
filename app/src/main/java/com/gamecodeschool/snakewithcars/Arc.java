package com.gamecodeschool.snakewithcars;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

class Arc extends Shape {

    private final RectF mOval;
    private final float mStartingAngle;
    private final float mSweepAngle;

    Arc(RectF oval, float startingAngle, float sweepAngle) {
        mOval = oval;
        mStartingAngle = startingAngle;
        mSweepAngle = sweepAngle;
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        Paint.Style style = paint.getStyle();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mOval, mStartingAngle, mSweepAngle, false, paint);
        paint.setStyle(style);
    }
}
