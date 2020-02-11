package com.codemitry.birdie;

import android.util.Log;

public class UpdateThread extends Thread {
    private boolean runned = false;
    private GameSurfaceView surface;
    private int FPS = 50;
    private int FRAME_PERIOD = 1000 / 50;

    UpdateThread(GameSurfaceView gameSurfaceView) {
        super("UpdateThread");
        surface = gameSurfaceView;
        Log.d("Thread", "UpdateThread created");
    }

    void setRunned(boolean run) {
        this.runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "UpdateThread started");
//        int fps = 0;
//        int frames = 0, skipped = 0;
//        int skipped = 0;
        int delta;
        long ctime, time = System.currentTimeMillis(), counter = 0;

        while (runned) {
            ctime = System.currentTimeMillis();
//            int framesSkipped = 0;

            delta = (int) (ctime - time);
            // тут обновление окна
            surface.update(delta);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            // ---
            if (delta < FRAME_PERIOD) {
                try {
                    Thread.sleep(delta);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            time = ctime;
        }
        Log.d("Thread", "UpdateThread finished");
    }


}
