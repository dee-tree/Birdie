package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

class Bird {
    private Game game;
    private Bitmap[] sprites;
    private int[] id = {R.drawable.bird1_fly, R.drawable.bird1_fall};
    private int x, y;
    private int width;
    private int height;
    Rect mask;
    private int ground;
    private Matrix rotate, position;
    private int wingsDown;
    private double acceleration;
    private double speed = 0;
    private double dy;
    private double angle;
    private boolean secret;
    private Paint bitmapPaint;

    Bird(Game game, int ground) {
        this.game = game;
        this.ground = ground;
        sprites = new Bitmap[id.length];
        width = (int) (0.185f * game.width);
        height = (int) (0.6 * width);

//        height = (int) (Config.BIRD_HEIGHT * Config.screen_height);
        for (int i = 0; i < id.length; i++) {
            sprites[i] = BitmapFactory.decodeResource(game.getResources(), id[i]);
            sprites[i] = Bitmap.createScaledBitmap(sprites[i], width, height, true);
        }
        x = game.width / 2 - width;
        y = game.height / 2 - height;
        dy = speed;
//        acceleration = 0.0045;    SAVE
        acceleration = 0.00000000000000000234375 * game.height;
//        up_coeff = Math.round(Config.screen_height * Config.BIRD_UP);
        angle = 0;

        secret = Config.isSecretModeEnabled(game);

        mask = new Rect();

        rotate = new Matrix();

        position = new Matrix();
        position.postTranslate(x, y);
        bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }


    boolean collised(ArrayList<Column> columns) {
        boolean collised = false;
        for (Column column : columns) {
            // for (int i = 0; i < column.length && !collised; i++) {
            if (column.isCollised(mask)) {
                collised = true;
            }
        }
        return collised;
    }

    void rotate(int angle) {

        rotate.reset();
        rotate.setRotate(angle, width / 2f, height / 2f);
        rotate.postTranslate(x, y);
        position.set(rotate);
    }

    void update(double dt) {
        speed += dt * acceleration;
        dy = Math.round(dt * speed);
        y += dy;

        // Плящущая птица
        if (secret) {
            rotate.setTranslate(x, y);
            position.set(rotate);
            if (wingsDown > 0)
                rotate((int) (angle -= 6));
            else
                rotate((int) (angle++));
        } else {

// Обычный поворот птицы при полете
            rotate.setTranslate(x, y);
            position.set(rotate);
            if (wingsDown > 0 && angle > -15)
                angle -= 4;
            else if (angle < 15)
                angle += 0.5;
            rotate((int) angle);

        }


        if (y < 0) {
            y = 0;
        } else if (y + height / 2 >= ground) {
            y = ground - height / 2;

            rotate.reset();
            rotate.setRotate(30, width / 2f, height / 2f);
            rotate.postTranslate(x, y);
            position.set(rotate);
            Config.saveGroundDeath(game, Config.loadGroundDeaths(game) + 1);
            game.onLose();

            return;
        }

        mask.left = x + width / 7;
        mask.top = y + height / 5;
        mask.right = x + width - width / 8;
        mask.bottom = y + height - height / 4;

    }

    void draw(Canvas canvas) {
        if (wingsDown-- > 0)
            canvas.drawBitmap(sprites[1], position, bitmapPaint);
        else
            canvas.drawBitmap(sprites[0], position, bitmapPaint);
//        canvas.drawRect(mask, new Paint());
    }

    void resetWings() {
        wingsDown = Config.TIMES_WINGS_DOWN;
    }

    void up() {
//        speed = - 1.25;    SAVE
        speed = -0.000000000651 * game.height;
    }

    int getWidth() {
        return width;
    }

    int getX() {
        return x;
    }
}
