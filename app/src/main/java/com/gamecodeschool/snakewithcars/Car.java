package com.gamecodeschool.snakewithcars;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

class Car {

    private final GameResources mGameResources;

    private final int SPEED;

    // TODO: fix collider spawn after car runsIntoWall while turning!
    private final PointF mCenter;
    private double mCurrentAngle;   // this is the absolute angle of the car eg PI if going left
    private final CarCollider mCollider;
    private final Tires mTires;

    private final double TURNING_RADIUS;
    private final PointF mRotationPoint;

    private double mPreviousAngle = 101010;  // some random value that can't be equal to mCurrentAngle...
    private Bitmap mCarBitmap = null;

    void stopTurning() {
        mTurning = Turn.NO_TURN;
    }

    void startLeftTurn() {
        mTurning = Turn.COUNTER_CLOCKWISE;
    }

    void startRightTurn() {
        mTurning = Turn.CLOCKWISE;
    }

    enum Turn {
        CLOCKWISE, COUNTER_CLOCKWISE, NO_TURN,
    }

    private volatile Turn mTurning;

    Car(GameResources gameResources, Point screenSize) {
        mGameResources = gameResources;

        SPEED = screenSize.x / 5;

        mCenter = new PointF();
        mCollider = new CarCollider();
        mTires = new Tires();

        TURNING_RADIUS = gameResources.getCarLength() / 2f;
        mRotationPoint = new PointF();
    }

    void spawn(Point screenSize) {
        mCenter.set(screenSize.x / 2f, screenSize.y / 2f);
        mCurrentAngle = 0;
        mTurning = Turn.NO_TURN;
        align();
        mTires.clearTracks();
    }

    // This alignment is necessary because after many turns (and all the small angle adjustments they
    // entail), the error becomes large and mCurrentAngle very wrong.
    private void align() {
        int carWidth = mGameResources.getCarWidth();
        int carLength = mGameResources.getCarLength();

        mCollider.align(mCenter, mCurrentAngle, carLength, carWidth);
        mTires.align(mCenter, mCurrentAngle, mCollider);
    }

    void update(long fps) {
        if (mTurning == Turn.NO_TURN) {
            updateStraightCar(fps);
        } else {
            updateTurningCar(fps);
        }
    }

    private void updateStraightCar(long fps) {
        double speed = (double) SPEED / fps;
        float dx = (float) (speed * Math.cos(mCurrentAngle));
        float dy = (float) (speed * Math.sin(mCurrentAngle));

        offsetEverything(dx, dy);
    }

    private void offsetEverything(float dx, float dy) {
        mCenter.offset(dx, dy);
        mCollider.offset(dx, dy);
        mTires.offset(dx, dy);
    }

    private void updateTurningCar(long fps) {
        double arcTravelled = (double) SPEED / fps;
        double deltaAngle = arcTravelled / TURNING_RADIUS;   // formula: arc = r * Δφ
        if (mTurning == Turn.COUNTER_CLOCKWISE) {
            deltaAngle = -deltaAngle;
        }

        updateRotationPoint();
        rotateEverything(mRotationPoint, deltaAngle);
        mCurrentAngle += deltaAngle;

//            float driftAngle = deltaAngle;
//            if (mAngleTurnedSoFar + Math.abs(driftAngle) <= HALF_PI) {
//                rotateEverything(mTires.getFrontRightTire(), driftAngle);
//                mCurrentAngle += driftAngle;
//                mAngleTurnedSoFar += Math.abs(driftAngle);
//            }
    }

    private void rotateEverything(PointF rotationPoint, double deltaAngle) {
        MathEngine.rotatePoint(mCenter, rotationPoint, deltaAngle);
        mCollider.rotate(rotationPoint, deltaAngle);
        mTires.rotate(rotationPoint, deltaAngle, mCurrentAngle);
    }

    void draw(Canvas canvas, Paint paint) {
        if (mCurrentAngle != mPreviousAngle) {
            mCarBitmap = mGameResources.getBitmapCar(mCurrentAngle);
            mPreviousAngle = mCurrentAngle;
        }

        PointF whereToDraw = mCollider.getBitmapOrigin();
        canvas.drawBitmap(mCarBitmap, whereToDraw.x, whereToDraw.y, paint);

        if (GameEngine.DEBUGGING) {
            drawDebuggingPoints(canvas, paint);
        }

        mTires.draw(canvas, paint);
    }

    private void drawDebuggingPoints(Canvas canvas, Paint paint) {
        // center of car
        paint.setColor(Color.GREEN);
        canvas.drawCircle(mCenter.x, mCenter.y, 5, paint);

        // car's collider
        paint.setColor(Color.MAGENTA);
        mCollider.draw(canvas, paint);

        // rotation center
        paint.setColor(Color.GREEN);
        if (mTurning != Turn.NO_TURN) {
            canvas.drawCircle(mRotationPoint.x, mRotationPoint.y, 5, paint);
            paint.setStrokeWidth(5);
            if (mTurning == Turn.CLOCKWISE) {
                canvas.drawLine(mRotationPoint.x, mRotationPoint.y, mTires.getBackRightTire().x, mTires.getBackRightTire().y, paint);
                canvas.drawLine(mRotationPoint.x, mRotationPoint.y, mTires.getFrontRightTire().x, mTires.getFrontRightTire().y, paint);
            } else {
                canvas.drawLine(mRotationPoint.x, mRotationPoint.y, mTires.getBackLeftTire().x, mTires.getBackLeftTire().y, paint);
                canvas.drawLine(mRotationPoint.x, mRotationPoint.y, mTires.getFrontLeftTire().x, mTires.getFrontLeftTire().y, paint);
            }
        }
    }

    // has car exited through the walls?
    boolean crashes(Point screenSize) {
        boolean hitWall = mCollider.outOfBounds(screenSize);
        boolean hitTracks = false;
//        // has car crossed its tracks
//        for (int i = mSegmentsLocations.size() - 1; i > 0; i--) {
//            Point segmentLocation = mSegmentsLocations.get(i);
//            if (carLocation.equals(segmentLocation)) {
//                return true;
//            }
//        }

        return hitWall || hitTracks;
    }


    boolean runsOver(Fuel fuel) {
        PointF center = fuel.getColliderCenter();
        float radius = fuel.getColliderRadius();

        boolean colliderOverFuel = mCollider.touchesCircle(center, radius, mCurrentAngle);
        boolean tiresOverFuel = mTires.touchCircle(center, radius);

        return colliderOverFuel || tiresOverFuel;
    }

//    void addSegment() {
//        // Don't worry, this point will get its location when update() is called
//        mSegmentsLocations.add(new Point(-10, -10));
//    }

    private void updateRotationPoint() {
        double MAX_STEERING_ANGLE = Math.PI / 3;

        double frontTireAngle;
        PointF backTire;       // These are the inner wheels during turning
        PointF frontTire;

        if (mTurning == Turn.CLOCKWISE) {
            frontTireAngle = mCurrentAngle + MAX_STEERING_ANGLE;
            backTire = mTires.getBackRightTire();
            frontTire = mTires.getFrontRightTire();
        } else if (mTurning == Turn.COUNTER_CLOCKWISE) {
            frontTireAngle = mCurrentAngle - MAX_STEERING_ANGLE;
            backTire = mTires.getBackLeftTire();
            frontTire = mTires.getFrontLeftTire();
        } else {
            return;     // if no turn detected.
        }

        // NOTE: I reverse the slope signs due to the upside-down Y-axis of canvas!
        double tanCurrentAngle = Math.tan(mCurrentAngle);
        double tanFrontTireAngle = Math.tan(frontTireAngle);

        // These special cases will almost never happen, because I add many little deltaAngles
        // to get to the total angle...
        if (tanCurrentAngle == 0) {
            mRotationPoint.x = backTire.x;
            double y = frontTire.y - (mRotationPoint.x - frontTire.x) / tanFrontTireAngle;
            mRotationPoint.y = (float) y;
        } else if (Double.isInfinite(tanCurrentAngle)) {
            mRotationPoint.y = backTire.y;
            double x = frontTire.x - tanFrontTireAngle * (mRotationPoint.y - frontTire.y);
            mRotationPoint.x = (float) x;
        } else if (tanFrontTireAngle == 0) {
            mRotationPoint.x = frontTire.x;
            double y = backTire.y - (mRotationPoint.x - backTire.x) / tanCurrentAngle;
            mRotationPoint.y = (float) y;
        } else if (Double.isInfinite(tanFrontTireAngle)) {
            mRotationPoint.y = frontTire.y;
            double x = backTire.x - tanCurrentAngle * (mRotationPoint.y - backTire.y);
            mRotationPoint.x = (float) x;
        } else {
            solveGeneralEquationForRotationPoint(backTire, frontTire, tanCurrentAngle, tanFrontTireAngle);
        }
    }

    private void solveGeneralEquationForRotationPoint(PointF backTire, PointF frontTire,
                                                      double tanCurrentAngle, double tanFrontTireAngle) {
        double x = tanCurrentAngle * tanFrontTireAngle / (tanCurrentAngle - tanFrontTireAngle) *
                (frontTire.y - backTire.y + frontTire.x / tanFrontTireAngle - backTire.x / tanCurrentAngle);

        double y = frontTire.y - (x - frontTire.x) / tanFrontTireAngle;

        mRotationPoint.set((float) x, (float) y);
    }
}
