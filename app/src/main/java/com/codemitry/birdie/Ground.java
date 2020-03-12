package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

class Ground {
    private Bitmap ground, groundDefault;
    private Bitmap grass, grassDefault;
    private int width, height, x, yGround, yGrass;
    private double speed, dx, acceleration;

    private Paint bitmapPaint;

    Ground(Game game, int width, int height) {
        this.width = width;
        this.height = height;

        groundDefault = BitmapFactory.decodeResource(game.getResources(), R.drawable.ground1);
        groundDefault = Bitmap.createScaledBitmap(groundDefault, width, (int) (Config.GROUND_HEIGHT * height), true);

        grassDefault = BitmapFactory.decodeResource(game.getResources(), R.drawable.grass1);
        grassDefault = Bitmap.createScaledBitmap(grassDefault, width, (int) (Config.GRASS_HEIGHT * height), true);

        ground = Bitmap.createBitmap(width * 2, (int) (Config.GROUND_HEIGHT * height), Bitmap.Config.ARGB_8888);
        Canvas groundCanvas = new Canvas(ground);
        groundCanvas.drawBitmap(groundDefault, 0, 0, null);
        groundCanvas.drawBitmap(groundDefault, width, 0, null);
        ground = Bitmap.createBitmap(ground);

        yGround = height - ground.getHeight();

        grass = Bitmap.createBitmap(width * 2, (int) (Config.GRASS_HEIGHT * height), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grass);
        canvas.drawBitmap(grassDefault, 0, 0, null);
        canvas.drawBitmap(grassDefault, width, 0, null);
        grass = Bitmap.createBitmap(grass);

        yGrass = yGround - grass.getHeight();
        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        x = 0;
        dx = speed = 0.0000000002 * width;
//        acceleration = Config.COLUMN_ACCELERATION;
        acceleration = 0.000000000000000000007 * game.width;
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(ground, x, yGround, bitmapPaint);
        canvas.drawBitmap(grass, x, yGrass, bitmapPaint);
    }

    void update(double dt) {
        if (x <= -width)
            x = 0;
        speed += acceleration * dt;
        dx = Math.round(speed * dt);
        x -= dx;
//        System.out.println("Ground dx: " + dx);
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.yGrass;
    }

}
