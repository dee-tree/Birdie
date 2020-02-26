package com.codemitry.birdie;

import android.util.Log;

@Deprecated
public class UpdateThread extends Thread {
    private boolean runned = false;
    private Game game;
    private int FPS = 50;
    private int FRAME_PERIOD = 1000 / FPS;

    UpdateThread(Game game) {
        super("UpdateThread");
        this.game = game;
        Log.d("Thread", "UpdateThread created");
    }

    void setRunned(boolean run) {
        this.runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "UpdateThread started");


        int delta;
        long ctime, time = System.currentTimeMillis(), counter = 0;

        while (runned) {
            ctime = System.currentTimeMillis();
            delta = (int) (ctime - time);
            // тут обновление окна
//            game.update(delta);


            // ---
//            if (delta < FRAME_PERIOD) {
//                try {
//                    Thread.sleep(FRAME_PERIOD - delta);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            time = ctime;
//            if (delta < FRAME_PERIOD) {
//                try {
//                    Thread.sleep(FRAME_PERIOD);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        Log.d("Thread", "UpdateThread finished");
    }


}
