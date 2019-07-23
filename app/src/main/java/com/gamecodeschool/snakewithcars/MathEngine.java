package com.gamecodeschool.snakewithcars;

import android.graphics.PointF;

class MathEngine {

    // static so we don't create a new point every time we need a rotation!!
    private final static PointF pivot = new PointF();

    static void rotatePoint(PointF point, PointF rotationCenter, double deltaAngle) {
        // Don't use rotationCenter itself because it might change if we rotate point around itself!
        pivot.set(rotationCenter);

        double sinAngle = Math.sin(deltaAngle);
        double cosAngle = Math.cos(deltaAngle);

        // we subtract the pivot to translate point to origin (0,0)
        point.offset(-pivot.x, -pivot.y);

        double x = cosAngle * point.x - sinAngle * point.y;
        double y = sinAngle * point.x + cosAngle * point.y;

        point.set((float) x, (float) y);

        // translate back
        point.offset(pivot.x, pivot.y);
    }

    static void alignRectangle(PointF carCenter, double currentAngle, float halfWidth, float halfLength,
                               PointF frontLeft, PointF frontRight, PointF backLeft, PointF backRight) {
        frontLeft.set(carCenter.x + halfLength, carCenter.y - halfWidth);
        frontRight.set(carCenter.x + halfLength, carCenter.y + halfWidth);
        backLeft.set(carCenter.x - halfLength, carCenter.y - halfWidth);
        backRight.set(carCenter.x - halfLength, carCenter.y + halfWidth);

        rotatePoint(frontLeft, carCenter, currentAngle);
        rotatePoint(frontRight, carCenter, currentAngle);
        rotatePoint(backLeft, carCenter, currentAngle);
        rotatePoint(backRight, carCenter, currentAngle);
    }

    static boolean pointInCircle(PointF point, PointF center, float radius) {
        return Math.pow(point.x - center.x, 2) + Math.pow(point.y - center.y, 2) <= Math.pow(radius, 2);
    }

    static void setMiddlePoint(PointF point1, PointF point2, PointF result) {
        float mid_x = (point1.x + point2.x) / 2;
        float mid_y = (point1.y + point2.y) / 2;
        result.set(mid_x, mid_y);
    }

    static double distance(PointF point1, PointF point2) {
        double xDistanceSquared = Math.pow(point1.x - point2.x, 2);
        double yDistanceSquared = Math.pow(point1.y - point2.y, 2);
        return Math.sqrt(xDistanceSquared + yDistanceSquared);
    }
}

