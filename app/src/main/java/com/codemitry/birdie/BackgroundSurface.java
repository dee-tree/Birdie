package com.codemitry.birdie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BackgroundSurface extends SurfaceView implements SurfaceHolder.Callback {
    MyThread thread;
    private int screenWidth, screenHeight;

    public BackgroundSurface(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        screenWidth = ((MainActivity) context).width;
        screenHeight = ((MainActivity) context).height;

        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        Log.d("BackgroundSurface", "Surface created");

        // thread create and start
        thread = new MyThread(getHolder());
        setRunned(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // завершаем работу потока

        boolean retry = true;
        setRunned(false);

        while (retry) {
            try {
                if (thread.isAlive())
                    thread.join();
                retry = false;
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

        Log.d("BackgroundSurface", "Surface Destroyed");
    }

    public void recreateAndStartThread() {
        thread = new MyThread(getHolder());
        setRunned(true);
        thread.start();
    }

    public void setRunned(boolean run) {
        if (thread != null)
            thread.setRunned(run);
    }

    boolean runned() {
        if (thread != null)
            return thread.getRunned();
        else
            return false;
    }


    class MyThread extends Thread {
        Bitmap background;
        SurfaceHolder holder;
        Canvas canvas;

        int x = 0, direction = -1;
        double speed = 0.00000000005;
        int dx;
        //        Rect rect;
        private boolean runned;

        MyThread(SurfaceHolder holder) {
            this.holder = holder;
            background = BitmapFactory.decodeResource(getResources(), R.drawable.menu_background);
            System.out.println(screenHeight);
            background = Bitmap.createScaledBitmap(background, screenWidth * 3, screenHeight, true);

            speed *= screenWidth;
        }


        void move(int dt) {
            if ((x >= 0) || (x + background.getWidth() <= screenWidth))
                direction *= -1;

            dx = (int) Math.round(speed * dt);

            x += direction * dx;
        }

        @Override
        public void run() {
            System.out.println("Движение фона пошло!");
            int delta;
            long ctime, time = System.nanoTime();
            while (runned) {
                ctime = System.nanoTime();
                canvas = null;
                delta = (int) (ctime - time);
                try {
                    if (holder != null)
                        canvas = holder.lockCanvas();

                    synchronized (holder) {
                        if (canvas != null) {
                            canvas.drawBitmap(background, x, 0, null);
                            move(delta);
                        }
                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                time = ctime;
                if (delta <= 1000 / 45) {
                    try {
                        Thread.sleep(delta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Движение фона прекращено, поток остановлен");
        }

        void setRunned(boolean run) {
            runned = run;
        }

        boolean getRunned() {
            return runned;
        }
    }

}
