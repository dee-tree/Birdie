package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

class Ground {
    private Bitmap ground;
    private int width, height, x, yGround;
    private double speed, dx, acceleration;


    Ground(Game game, int width, int height, int resID) {
        this.width = width;
        this.height = height;

        Bitmap groundDefault = BitmapFactory.decodeResource(game.getResources(), resID);
        groundDefault = Bitmap.createScaledBitmap(groundDefault, width, height, true);

        ground = Bitmap.createBitmap(width * 2, height, Bitmap.Config.ARGB_8888);
        Canvas groundCanvas = new Canvas(ground);
        groundCanvas.drawBitmap(groundDefault, 0, 0, null);
        groundCanvas.drawBitmap(groundDefault, width, 0, null);
        ground = Bitmap.createBitmap(ground);

        yGround = game.height - ground.getHeight();

        x = 0;
        dx = speed = 0.0000000002 * game.width;
//        acceleration = Config.COLUMN_ACCELERATION;
        acceleration = 0.000000000000000000007 * game.width;
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(ground, x, yGround, null);
    }

    void update(double dt) {
        if (x <= -width)
            x = 0;
        speed += acceleration * dt;
        dx = Math.round(speed * dt);
        x -= dx;
    }

    int getY() {
        return this.yGround;
    }

}
