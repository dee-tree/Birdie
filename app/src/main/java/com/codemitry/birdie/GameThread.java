package com.codemitry.birdie;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private boolean runned = false;
    private SurfaceHolder holder;
    private Game game;

    GameThread(Game game, GameSurfaceView surface) {
        super("GameThread");

        this.game = game;
        this.holder = surface.getHolder();

        Log.d("GameThread", "GameThread created");
    }

    void setRunned(boolean run) {
        runned = run;
    }

    boolean getRunned() {
        return runned;
    }

    @Override
    public void run() {
        Log.d("GameThread", "GameThread started");
        Canvas canvas;

        double delta;
        double ctime, time = System.nanoTime();
        long counter = 0;

        while (runned) {
            ctime = System.nanoTime();
            canvas = null;

            delta = (int) (ctime - time);
            try {
                canvas = holder.lockCanvas();
//                } else canvas = surfaceHolder.lockHardwareCanvas();
                synchronized (holder) {
                    // тут отрисовка окна
                    game.draw(canvas);
                    game.update(delta);
                    // ---
//                    System.out.println("dt: " + delta);
//                    if (delta < 1000f / 50) {
//                        try {
//                            Thread.sleep((long) (1000f / 50 - delta));
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            time = ctime;
        }
        Log.d("GameThread", "GameThread finished");
    }


}
