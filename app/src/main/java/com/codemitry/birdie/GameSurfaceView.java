package com.codemitry.birdie;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    GameThread gameThread;
    Game game;


    public GameSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        this.game = (Game) context;
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        Log.d("Surface", "Surface created");

        gameThread = new GameThread(game, this);
        gameThread.setRunned(true);
        gameThread.start();


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // завершаем работу потока

        boolean retry = true;

        setRunned(false);

        while (retry) {
            try {
                if (gameThread.isAlive())
                    gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }


        Log.d("Surface", "Surface Destroyed");
    }

    public void setRunned(boolean run) {
        if (gameThread != null)
            gameThread.setRunned(run);
    }

    boolean runned() {
        return gameThread.getRunned();
    }


}
