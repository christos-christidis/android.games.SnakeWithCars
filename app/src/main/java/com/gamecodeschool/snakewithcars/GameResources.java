package com.gamecodeschool.snakewithcars;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.io.IOException;

class GameResources {

    private SoundPool mSoundPool;
    private int mEatID = -1;
    private int mCrashID = -1;

    private Bitmap mBitmapFuel;
    private Bitmap mBitmapCar;
//    private final Bitmap mBitmapTracks;

    GameResources(Context context, Point screenSize) {
        loadSounds(context);
        loadBitmaps(context, screenSize);
    }

    private void loadSounds(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(5).setAudioAttributes(audioAttributes).build();
        } else {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        // NOTE: I can either put sounds in assets/ in which case I load them like this OR I can put
        // them in res/raw and load them like in SubHunter game.
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("get_fuel.ogg");
            mEatID = mSoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSoundPool.load(descriptor, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBitmaps(Context context, Point screenSize) {
        final float FUEL_SCALE = 0.05f;
        final float CAR_SCALE = 0.12f;

        int fuelTargetSize = (int) (screenSize.x * FUEL_SCALE);

        Bitmap bitmapFuel = BitmapFactory.decodeResource(context.getResources(), R.drawable.fuel);
        mBitmapFuel = Bitmap.createScaledBitmap(bitmapFuel, fuelTargetSize, fuelTargetSize, false);

        // create car & tracks bitmaps
        Bitmap bitmapCarRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite7);

        float carAspectRatio = (float) bitmapCarRight.getWidth() / bitmapCarRight.getHeight();
        int carTargetWidth = (int) (screenSize.x * CAR_SCALE);
        int carTargetHeight = (int) (carTargetWidth / carAspectRatio);

        mBitmapCar = Bitmap.createScaledBitmap(bitmapCarRight, carTargetWidth, carTargetHeight, false);

//        Bitmap bitmapTracksRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.tracks1);
//        mBitmapTracksRight = Bitmap.createScaledBitmap(bitmapTracksRight,
//                Math.round(blockSize.x), Math.round(blockSize.y), false);
    }

    void playEatSound() {
        mSoundPool.play(mEatID, 1, 1, 0, 0, 1);
    }

    void playCrashSound() {
        mSoundPool.play(mCrashID, 1, 1, 0, 0, 1);
    }

    Bitmap getBitmapFuel() {
        return mBitmapFuel;
    }

    Bitmap getBitmapCar(double angle) {
        float degrees = (float) Math.toDegrees(angle);

        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        return Bitmap.createBitmap(mBitmapCar, 0, 0,
                mBitmapCar.getWidth(), mBitmapCar.getHeight(), matrix, true);
    }

    int getCarWidth() {
        return mBitmapCar.getHeight();
    }

    int getCarLength() {
        return mBitmapCar.getWidth();
    }

//    Bitmap getBitmapTracks(Car.Direction direction) {
//        return tracksBitmaps.get(direction);
//    }
}
