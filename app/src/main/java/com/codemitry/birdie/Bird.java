package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    private int up_coeff;
    private double acceleration;
    private double speed = 0;
    private double dy;

    Bird(Game game, int ground) {
        this.game = game;
        this.ground = ground;
        sprites = new Bitmap[id.length];
        width = (int) (Config.BIRD_WIDTH * Config.screen_width);
        height = (int) (Config.BIRD_HEIGHT * Config.screen_height);
        for (int i = 0; i < id.length; i++) {
            sprites[i] = BitmapFactory.decodeResource(game.getResources(), id[i]);
            sprites[i] = Bitmap.createScaledBitmap(sprites[i], width, height, true);
        }
        x = (int) (Config.screen_width * Config.SPAWN_X) - width;
        y = (int) (Config.screen_height * Config.SPAWN_Y) - height;
        dy = speed;
//        acceleration = 0.0045;    SAVE
        acceleration = 0.00000234375 * game.height;
//        up_coeff = Math.round(Config.screen_height * Config.BIRD_UP);

        mask = new Rect();

        rotate = new Matrix();

        position = new Matrix();
        position.postTranslate(x, y);
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

    void update(int dt) {
        speed += dt * acceleration;
        dy = Math.round(dt * speed);
        y += dy;
        rotate.setTranslate(x, y);
        position.set(rotate);

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
        mask.top = y + height / 6;
        mask.right = x + width - width / 8;
        mask.bottom = y + height - height / 4;

    }

    void draw(Canvas canvas) {
        if (wingsDown-- > 0)
            canvas.drawBitmap(sprites[1], position, null);
        else
            canvas.drawBitmap(sprites[0], position, null);
//        canvas.drawRect(mask, new Paint());
    }

    void resetWings() {
        wingsDown = Config.TIMES_WINGS_DOWN;
    }

    void up() {
//        speed = - 1.25;    SAVE
        speed = -0.000651 * game.height;
    }

    int getX() {
        return x;
    }
}
