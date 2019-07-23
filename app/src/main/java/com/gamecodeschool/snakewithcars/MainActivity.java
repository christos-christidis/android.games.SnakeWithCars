package com.gamecodeschool.snakewithcars;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {
    private GameEngine mGameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VisibilityManager.hideSystemUI(this);

        final Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getRealSize(screenSize);

        mGameEngine = new GameEngine(this, screenSize);
        setContentView(mGameEngine);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameEngine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameEngine.pause();
    }
}
