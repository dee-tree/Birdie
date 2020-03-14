package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

class Background {
    private Bitmap background, backgroundDefault;
    private int x;
    private double speed, dx, acceleration;
    private int width, height;

    Background(Game game, int width, int height, int resID) {
        this.width = width;
        this.height = height;

        backgroundDefault = BitmapFactory.decodeResource(game.getResources(), resID);
        backgroundDefault = Bitmap.createScaledBitmap(backgroundDefault, width, height, true);

        background = Bitmap.createBitmap(width * 2, height, Bitmap.Config.ARGB_8888);
        Canvas groundCanvas = new Canvas(background);
        groundCanvas.drawBitmap(backgroundDefault, 0, 0, null);
        groundCanvas.drawBitmap(backgroundDefault, width, 0, null);
        background = Bitmap.createBitmap(background);
        x = 0;
        dx = speed = 0.00000000001 * game.width;
        acceleration = 0.000000000000000000007 * game.width;
    }

    void update(double dt) {
        if (x <= -width)
            x = 0;
        speed += acceleration * dt;
        dx = Math.round(speed * dt);
        x -= dx;
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(background, x, 0, null);
    }
}
