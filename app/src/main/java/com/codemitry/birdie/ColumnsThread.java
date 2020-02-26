package com.codemitry.birdie;

import android.util.Log;

import java.util.ArrayList;

@Deprecated
public class ColumnsThread extends Thread {
    private boolean runned = false;
    private int ups = 75; // update per second
    private int inc = 2;
    private int framePeriod = 1000 / ups;
    private ArrayList<Column> columns;
    private Game game;

    ColumnsThread(Game game) {
        super("ColumnsThread");
        this.game = game;
        Log.d("Thread", "ColumnsThread created");
    }

    void setRunned(boolean run) {
        this.runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "ColumnsThread started");

        int delta;
        long ctime, time = System.currentTimeMillis(), counter = 0;
        while (runned) {
            ctime = System.currentTimeMillis();
            delta = (int) (ctime - time);

            // тут обновление колонн
//            game.updateColumns(delta);
//            game.updateGround(delta);
            // ---
//            frames++;
//            ++fps;
            if (time >= 1000) {
                time -= 1000;
                if (ups < 250 && game.isRun())
                    incUPS();
            }

            if (delta < framePeriod) {
                try {
//                    Log.d("ColumnThread", "Sleep time: " + sleepTime);
                    Thread.sleep(delta);
                } catch (InterruptedException e) {
                }
            }
            time = ctime;
        }
        Log.d("Thread", "ColumnsThread finished");
    }

    private void updateFramePeriod() {
        framePeriod = 1000 / ups;
    }

    private void incUPS() {
        this.ups += inc;
        updateFramePeriod();
    }


}
