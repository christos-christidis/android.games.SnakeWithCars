package com.gamecodeschool.snakewithcars;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

class CarCollider {

    // static so we don't create a new Point every time getBitmapOrigin() is called
    private final static PointF bitmapOrigin = new PointF();

    private final PointF[] mBitmapPoints;
    private final PointF[] mColliderPoints;

    // for clarity
    private final int FRONT_LEFT = 0;
    private final int FRONT_RIGHT = 1;
    private final int BACK_LEFT = 2;
    private final int BACK_RIGHT = 3;
    // These additional fields are necessary for better collision detection
    @SuppressWarnings("FieldCanBeLocal")
    private final int MID_FRONT = 4;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MID_BACK = 5;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MID_LEFT = 6;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MID_RIGHT = 7;

    CarCollider() {
        mColliderPoints = new PointF[8];
        for (int i = 0; i < mColliderPoints.length; i++) {
            mColliderPoints[i] = new PointF(-1000, -1000);
        }

        mBitmapPoints = new PointF[4];
        for (int i = 0; i < mBitmapPoints.length; i++) {
            mBitmapPoints[i] = new PointF(-1000, -1000);
        }
    }

    void align(PointF carCenter, double currentAngle, int carLength, int carWidth) {
        float halfWidth = carWidth / 2f;
        float halfLength = carLength / 2f;

        final float WIDTH_ADJUSTMENT = carWidth / 15f;
        final float LENGTH_ADJUSTMENT = carLength / 15f;

        // NOTE: it pays to carefully arrange the points in this order so we always know which point
        // is which as the car moves.
        MathEngine.alignRectangle(carCenter, currentAngle,
                halfWidth - WIDTH_ADJUSTMENT, halfLength - LENGTH_ADJUSTMENT,
                getFrontLeft(), getFrontRight(), getBackLeft(), getBackRight());

        MathEngine.setMiddlePoint(getFrontLeft(), getFrontRight(), getMidFront());
        MathEngine.setMiddlePoint(getBackLeft(), getBackRight(), getMidBack());
        MathEngine.setMiddlePoint(getFrontLeft(), getBackLeft(), getMidLeft());
        MathEngine.setMiddlePoint(getFrontRight(), getBackRight(), getMidRight());

        // TODO: simply pass the whole array of points???
        MathEngine.alignRectangle(carCenter, currentAngle, halfWidth, halfLength,
                mBitmapPoints[FRONT_LEFT], mBitmapPoints[FRONT_RIGHT],
                mBitmapPoints[BACK_LEFT], mBitmapPoints[BACK_RIGHT]);
    }

    void rotate(PointF rotationCenter, double deltaAngle) {
        for (PointF point : mColliderPoints) {
            MathEngine.rotatePoint(point, rotationCenter, deltaAngle);
        }
        for (PointF point : mBitmapPoints) {
            MathEngine.rotatePoint(point, rotationCenter, deltaAngle);
        }
    }

    void offset(float dx, float dy) {
        for (PointF point : mColliderPoints) {
            point.offset(dx, dy);
        }
        for (PointF point : mBitmapPoints) {
            point.offset(dx, dy);
        }
    }

    PointF getBitmapOrigin() {
        bitmapOrigin.set(mBitmapPoints[FRONT_LEFT]);

        for (PointF point : mBitmapPoints) {
            bitmapOrigin.x = Math.min(point.x, bitmapOrigin.x);
            bitmapOrigin.y = Math.min(point.y, bitmapOrigin.y);
        }

        return bitmapOrigin;
    }

    boolean touchesCircle(PointF center, float radius, double currentAngle) {
        for (PointF point : mColliderPoints) {
            if (MathEngine.pointInCircle(point, center, radius)) {
                return true;
            }
        }

        return false;
    }

    void draw(Canvas canvas, Paint paint) {
        for (PointF point : mColliderPoints) {
            canvas.drawCircle(point.x, point.y, 5, paint);
        }
    }

    boolean outOfBounds(Point screenSize) {
        for (PointF point : mColliderPoints) {
            if (point.x < 0 || point.x > screenSize.x ||
                    point.y < 0 || point.y > screenSize.y) {
                return true;
            }
        }

        return false;
    }

    private PointF getFrontLeft() {
        return mColliderPoints[FRONT_LEFT];
    }

    private PointF getFrontRight() {
        return mColliderPoints[FRONT_RIGHT];
    }

    private PointF getBackLeft() {
        return mColliderPoints[BACK_LEFT];
    }

    private PointF getBackRight() {
        return mColliderPoints[BACK_RIGHT];
    }

    private PointF getMidFront() {
        // these additional points are necessary for better collision detection
        return mColliderPoints[MID_FRONT];
    }

    private PointF getMidBack() {
        return mColliderPoints[MID_BACK];
    }

    private PointF getMidLeft() {
        return mColliderPoints[MID_LEFT];
    }

    private PointF getMidRight() {
        return mColliderPoints[MID_RIGHT];
    }

    float getWidth() {
        return Math.abs(getFrontLeft().y - getFrontRight().y);
    }

    float getLength() {
        return Math.abs(getFrontLeft().x - getBackLeft().x);
    }
}
