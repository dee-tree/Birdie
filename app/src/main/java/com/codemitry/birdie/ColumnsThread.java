package com.codemitry.birdie;

import android.util.Log;

public class ColumnsThread extends Thread {
    private boolean runned = false;
    private int ups = 75; // update per second
    private int inc = 2;
    private int framePeriod = 1000 / ups;
    private Columns columns;
    private Game game;

    ColumnsThread(Columns columns, Game game) {
        super("ColumnsThread");
        this.columns = columns;
        this.game = game;
        Log.d("Thread", "ColumnsThread created");
    }

    void setRunned(boolean run) {
        this.runned = run;
    }


    @Override
    public void run() {
        Log.d("Thread", "ColumnsThread started");
//        int fps = 0;
//        int frames = 0;
        long time = 0;
        int skipped = 0;
        while (runned) {
            long beginTime = System.currentTimeMillis();
            int framesSkipped = 0;
            // тут обновление колонн
            columns.updateColumns();
            game.gameSurfaceView.moveGrass();
            // ---
//            frames++;
//            ++fps;
            if (time >= 1000) {
//                Log.d("ColumnFPS", String.valueOf(fps));  // FPS log
//                Log.d("ColumnThread", "Frames summary: " + frames + " ; skipped: " + skipped);
                //fps = 0;

//                Log.d("ColumnThread", "Frames skipped: " + skipped);
                time = 0;
                skipped = 0;
                if (ups < 250 && game.isRun())
                    incUPS();
            }

            long elapsedTime = System.currentTimeMillis() - beginTime;
            int sleepTime = (int) (framePeriod - elapsedTime);
            if (sleepTime > 0) {
                try {
//                    Log.d("ColumnThread", "Sleep time: " + sleepTime);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
//            while (sleepTime < 0 && framesSkipped < Config.MAX_FRAME_SKIPS) {
//                // Columns update
//                columns.updateColumns();
//                game.gameSurfaceView.moveGrass();
//                sleepTime += framePeriod;
//                ++framesSkipped;
//                ++skipped;
//            }
            time += System.currentTimeMillis() - beginTime;
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
