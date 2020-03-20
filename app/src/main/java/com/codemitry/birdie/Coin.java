package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Coin {
    private int x, y, size;
    private Bitmap sprite;
    private boolean earn = false;

    Coin(Game game, int size) {
        sprite = BitmapFactory.decodeResource(game.getResources(), R.drawable.coin);
        this.size = size;
        sprite = Bitmap.createScaledBitmap(sprite, size, size, true);
    }

    void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, x, y, null);
    }

    void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    boolean isEarn() {
        return this.earn;
    }

    void earn() {
        this.earn = true;
        sprite = null;
    }

    int getSize() {
        return size;
    }
}
