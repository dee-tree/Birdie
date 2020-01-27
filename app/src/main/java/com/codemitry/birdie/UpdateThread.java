package com.codemitry.birdie;

import android.util.Log;

public class UpdateThread extends Thread {
    private boolean runned = false;
    private GameSurfaceView surface;

    UpdateThread(GameSurfaceView gameSurfaceView) {
        super("UpdateThread");
        surface = gameSurfaceView;
        Log.d("Thread", "UpdateThread created");
    }

    void setRunned(boolean run) {
        runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "UpdateThread started");
//        int fps = 0;
//        long time = 0;
//        int frames = 0, skipped = 0;
        while (runned) {
            long beginTime = System.currentTimeMillis();
            int framesSkipped = 0;
            // тут обновление окна
            surface.update();
            // ---
//            frames++;
//            ++fps;
//            if (time >= 1000) {
//              Log.d("UpdateFPS", String.valueOf(fps));  // FPS log
//              Log.d("UpdateThread", "Frames summary: " + frames + " ; skipped: " + skipped);
//              time = 0;
//              fps = 0;
//            }

            long elapsedTime = System.currentTimeMillis() - beginTime;
            int sleepTime = (int) (Config.FRAME_PERIOD - elapsedTime);
            if (sleepTime > 0) {
                try {
                    //Log.d("Thread", "Sleep time: " + sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
            while (sleepTime < 0 && framesSkipped < Config.MAX_FRAME_SKIPS) {
                //canvas.drawColor(Color.RED);
                surface.update();
                sleepTime += Config.FRAME_PERIOD;
                ++framesSkipped;
//                ++skipped;
            }
//            time += System.currentTimeMillis() - beginTime;
        }
        Log.d("Thread", "UpdateThread finished");
    }


}
