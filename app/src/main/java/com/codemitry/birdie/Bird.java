package com.codemitry.birdie;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

class Bird {
    private Bitmap[] pictures;
    private int x, y;
    private int width = (int) (Config.BIRD_WIDTH * Config.scale);
    private int height = (int) (Config.BIRD_HEIGHT * Config.scale);
    private RectF mask;
    private int groundY = (int) (Config.screen_height - Config.GROUND_HEIGHT * Config.scale);
    private Matrix matrixDown;
    private int wingsDown;
    private int speed = 4;
    private GameSurfaceView surface;

    Bird(Resources resources, int[] id, GameSurfaceView surface) {
        this.surface = surface;
        pictures = new Bitmap[id.length];
        for (int i = 0; i < id.length; i++) {
            pictures[i] = BitmapFactory.decodeResource(resources, id[i]);
            pictures[i] = Bitmap.createScaledBitmap(pictures[i], width, height, true);
        }

        x = (int) (Config.SPAWN_X * Config.scale);
        y = (int) (Config.SPAWN_Y * Config.scale);

        mask = new RectF();

        matrixDown = new Matrix();
        matrixDown.postRotate(30);
    }


    boolean collised(Columns.Column[] column) {
        boolean collised = false;
        for (int i = 0; i < column.length && !collised; i++) {
            if (column[i].isCollised(mask))
                // uncollised mode:
                //;
                collised = true;
        }
        return collised;
    }

    void update() {
        if (!surface.game.isDeath()) {
            y += speed;
            if (y < 0) {
                y = 0;
            } else if (y + height >= groundY) {
                pictures[0] = Bitmap.createBitmap(pictures[0], 0, 0, pictures[0].getWidth(), pictures[0].getHeight(), matrixDown, true);
                surface.game.setDeath(true);
                y = groundY - height;
            }
            if (speed < 80) {
                speed += 3;
            }
            mask.set(x + 7, y + 20, x + (int) (Config.BIRD_WIDTH * Config.scale) - 10,
                    y + (int) (Config.BIRD_HEIGHT * Config.scale) - 15);
        }
    }

    void draw(Canvas canvas) {
        if (wingsDown-- > 0)
            canvas.drawBitmap(pictures[1], x, y, null);
        else
            canvas.drawBitmap(pictures[0], x, y, null);
    }

    void resetWings() {
        wingsDown = Config.TIMES_WINGS_DOWN;
    }

    void up() {
        speed = -Config.BIRD_UP;
    }

    int getX() {
        return x;
    }
}
