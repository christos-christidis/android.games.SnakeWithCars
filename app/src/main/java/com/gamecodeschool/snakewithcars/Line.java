package com.gamecodeschool.snakewithcars;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

class Line extends Shape {
    private final PointF mPoint1;
    private final PointF mPoint2;

    Line(PointF point1, PointF point2) {
        mPoint1 = new PointF(point1.x, point1.y);
        mPoint2 = new PointF(point2.x, point2.y);
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        canvas.drawLine(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y, paint);
    }
}
