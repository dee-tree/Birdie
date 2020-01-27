package com.codemitry.birdie;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

class Bird {
    private Bitmap[] pictures;
    private int x, y;
    private int width;
    private int height;
    private Rect mask;
    private int groundY;
    private Matrix matrixDown;
    private int wingsDown;
    private int speed = 4;
    private GameSurfaceView surface;

    Bird(Resources resources, int[] id, GameSurfaceView surface) {
        this.surface = surface;
        pictures = new Bitmap[id.length];
        groundY = (int) (Config.screen_height - Config.GROUND_HEIGHT * Config.screen_height);
        width = (int) (Config.BIRD_WIDTH * Config.screen_width);
        height = (int) (Config.BIRD_HEIGHT * Config.screen_height);
        for (int i = 0; i < id.length; i++) {
            pictures[i] = BitmapFactory.decodeResource(resources, id[i]);
            pictures[i] = Bitmap.createScaledBitmap(pictures[i], width, height, true);
        }
        x = (int) (Config.screen_width * Config.SPAWN_X) - width;
        y = (int) (Config.screen_height * Config.SPAWN_Y) - height;
//        x = (int) (Config.SPAWN_X * Config.scale);
//        y = (int) (Config.SPAWN_Y * Config.scale);

        mask = new Rect();

        matrixDown = new Matrix();
        matrixDown.postRotate(30);
    }


    boolean collised(Columns.Column[] column) {
        boolean collised = false;
        for (int i = 0; i < column.length && !collised; i++) {
            if (column[i].isCollised(mask)) {
                collised = true;
            }
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
                Config.saveGroundDeath(surface.getContext(), Config.loadGroundDeaths(surface.getContext()) + 1);
                surface.onLose();
            }
            if (speed < 80) {
                speed += 3;
            }

//            y += speed;

            mask.left = x + 20;
            mask.top = y + 40;
            mask.right = x + width - 12;
            mask.bottom = y + height - 30;

//            mask.set(x + 7, y + 20, x + width - 10,
//                    y + height- 15);
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
