package com.gamecodeschool.snakewithcars;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

class LogHelper {

    static void log(String str) {
        Log.i("WTF", str);
    }

    static void log(String name, boolean value) {
        Log.i("WTF", String.format("%s = %b", name, value));
    }

    static void log(String name, long value) {
        Log.i("WTF", String.format("%s = %d", name, value));
    }

    static void log(String name, double value) {
        Log.i("WTF", String.format("%s = %.2f", name, value));
    }

    static void log(String name, PointF point) {
        Log.i("WTF", String.format("%s = (%.2f, %.2f)", name, point.x, point.y));
    }

    static void log(String name, Point point) {
        Log.i("WTF", String.format("%s = (%d, %d)", name, point.x, point.y));
    }
}
