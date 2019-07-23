package com.gamecodeschool.snakewithcars;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class Tires {

    // This is list cause I need the copy-on-write...
    private final CopyOnWriteArrayList<PointF> mTirePoints;

    // for clarity
    @SuppressWarnings("FieldCanBeLocal")
    private final int FRONT_LEFT = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private final int FRONT_RIGHT = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private final int BACK_LEFT = 2;
    @SuppressWarnings("FieldCanBeLocal")
    private final int BACK_RIGHT = 3;

    private final PointF[] mPreviousTirePoints;
    private final List<Shape> mShapesToDraw;

    Tires() {
        mTirePoints = new CopyOnWriteArrayList<>();
        mPreviousTirePoints = new PointF[4];
        for (int i = 0; i < 4; i++) {
            mTirePoints.add(new PointF());
            mPreviousTirePoints[i] = new PointF();
        }

        mShapesToDraw = new ArrayList<>();
    }

    // TODO: different distances of the tires from front/back
    void align(PointF carCenter, double currentAngle, CarCollider collider) {
        final float DISTANCE_FROM_END = collider.getLength() / 7f;
        float halfWidth = collider.getWidth() / 2f;
        float halfLength = collider.getLength() / 2f - DISTANCE_FROM_END;

        // NOTE: it pays to carefully arrange the points in this order so we always know which point
        // is which as the car moves.
        MathEngine.alignRectangle(carCenter, currentAngle, halfWidth, halfLength,
                getFrontLeftTire(), getFrontRightTire(), getBackLeftTire(), getBackRightTire());
    }

    void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);

//        for (Shape shape : mShapesToDraw) {
//            shape.draw(canvas, paint);
//        }

        if (GameEngine.DEBUGGING) {
            // draw tire points
            paint.setColor(Color.RED);
            for (PointF point : mTirePoints) {
                canvas.drawRect(point.x, point.y, point.x + 5, point.y + 5, paint);
            }
        }
    }

    void offset(float dx, float dy) {
        saveCurrentTirePoints();

        for (PointF point : mTirePoints) {
            point.offset(dx, dy);
        }

//        for (int i = 0; i < mTirePoints.size(); i++) {
//            Line line = new Line(mPreviousTirePoints[i], mTirePoints.get(i));
//            mShapesToDraw.add(line);
//        }
    }

    void rotate(PointF rotationCenter, double deltaAngle, double startingAngle) {
        saveCurrentTirePoints();

        for (PointF point : mTirePoints) {
            MathEngine.rotatePoint(point, rotationCenter, deltaAngle);
        }

//        for (int i = 0; i < mTirePoints.size(); i++) {
//            float radius = (float) MathEngine.distance(rotationCenter, mTirePoints.get(i));
//            float left = rotationCenter.x - radius;
//            float top = rotationCenter.y - radius;
//
//            RectF oval = new RectF(left, top, left + radius * 2, top + radius * 2);
//            startingAngle *= 180 / Math.PI;
//            Arc arc = new Arc(oval, (float) startingAngle, (float) deltaAngle);
//
//            mShapesToDraw.add(arc);
//        }
    }

    private void saveCurrentTirePoints() {
        for (int i = 0; i < mTirePoints.size(); i++) {
            mPreviousTirePoints[i].set(mTirePoints.get(i));
        }
    }

    PointF getFrontLeftTire() {
        return mTirePoints.get(FRONT_LEFT);
    }

    PointF getFrontRightTire() {
        return mTirePoints.get(FRONT_RIGHT);
    }

    PointF getBackLeftTire() {
        return mTirePoints.get(BACK_LEFT);
    }

    PointF getBackRightTire() {
        return mTirePoints.get(BACK_RIGHT);
    }

    boolean touchCircle(PointF center, float radius) {
        for (PointF point : mTirePoints) {
            if (Math.pow(point.x - center.x, 2) + Math.pow(point.y - center.y, 2) <= Math.pow(radius, 2)) {
                return true;
            }
        }

        return false;
    }

    void clearTracks() {
        // TODO: must do this only while drawing is disallowed!
        mShapesToDraw.clear();
    }
}
