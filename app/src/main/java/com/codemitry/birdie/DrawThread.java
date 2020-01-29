package com.codemitry.birdie;

import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
    private boolean runned = false;
    private SurfaceHolder surfaceHolder;
    private GameSurfaceView gameSurfaceView;
    private int FPS = 60;
    private int FRAME_PERIOD = 1000 / FPS;

    DrawThread(SurfaceHolder surfaceHolder, GameSurfaceView gameSurfaceView) {
        super("DrawThread");
        this.surfaceHolder = surfaceHolder;
        this.gameSurfaceView = gameSurfaceView;

        Log.d("Thread", "DrawThread created");
    }

    void setRunned(boolean run) {
        runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "DrawThread started");
        Canvas canvas;
//        int fps = 0;
//        int frames = 0;
//
//        long time = 0;
//        int skipped = 0;
        while (runned) {
            canvas = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canvas = surfaceHolder.lockHardwareCanvas();
                } else canvas = surfaceHolder.lockCanvas();
//                } else canvas = surfaceHolder.lockHardwareCanvas();
                synchronized (surfaceHolder) {
                    long beginTime = System.currentTimeMillis();
                    int framesSkipped = 0;
                    // тут отрисовка окна
                    gameSurfaceView.draw(canvas);
                    // ---
                    // FPS visible
//                    ++fps;
//                    frames++;
//                    if (time >= 1000) {
//                        Log.d("FPS", String.valueOf(fps));  // FPS log
//                        Log.d("Thread", "Draw Frames summary: " + frames + " ; skipped: " + skipped);
                    //                        fps = 0;
//                        Log.d("DrawThread", "Frames skipped: " + skipped);
//                        time = 0;
//                        skipped = 0;
//                    }

                    long elapsedTime = System.currentTimeMillis() - beginTime;
                    int sleepTime = (int) (FRAME_PERIOD - elapsedTime);
                    if (sleepTime > 0) {
                        try {
                            //Log.d("Thread", "Sleep time: " + sleepTime);
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                        }
                    }
                    while (sleepTime < 0 && framesSkipped < Config.MAX_FRAME_SKIPS) {
                        //gameSurfaceView.draw(canvas);
                        sleepTime += FRAME_PERIOD;
                        ++framesSkipped;
//                        ++skipped;
                    }
//                    time += System.currentTimeMillis() - beginTime;
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        Log.d("Thread", "DrawThread finished");
    }


}
