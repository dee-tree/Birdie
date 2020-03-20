package com.codemitry.birdie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

class Column {

    private Game game;
    private Bitmap columnTop, columnDown;
    private Bitmap pikaTop, pikaDown;
    private Bitmap defaultColumn;

    private Matrix matrix;

    private int screenWidth, screenHeight;
    private int columnX, columnTopHeight, columnDownY, columnDownHeight, columnWidth;
    private int pikaX, pikaTopY, pikaDownY, pikaHeight, pikaWidth;
    private int hole;
    private int birdX, birdWidth;
    private int bottom;
    private boolean isScored;
    private boolean alive;
    private int columnHeightDefault;
    double speed, dx, acceleration;

    private boolean hasCoin;
    private Coin coin;


    Column(Game game, int birdX, int birdWidth, double speed, int pikaID, int colID) {
        this.game = game;
        this.birdX = birdX;
        this.birdWidth = birdWidth;
        this.screenWidth = game.width;
        this.screenHeight = game.height;

        bottom = (int) (screenHeight - screenHeight * (Config.GROUND_HEIGHT));

        hole = (int) (Config.COLUMN_HOLE * screenHeight);

        pikaWidth = (int) (Config.PIKA_WIDTH * screenWidth);
        pikaHeight = (int) (Config.PIKA_HEIGHT * screenHeight);

        columnWidth = (int) (Config.COLUMN_WIDTH * screenWidth);
        columnDownHeight = columnTopHeight = columnHeightDefault = (bottom - hole) / 2 - pikaHeight;
        columnDownY = bottom - columnDownHeight;

        pikaTopY = columnTopHeight;
        pikaDownY = pikaTopY + pikaHeight + hole;

        defaultColumn = BitmapFactory.decodeResource(game.getResources(), colID);
        defaultColumn = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnTopHeight, false);

        columnTop = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnTopHeight, false);
        pikaTop = BitmapFactory.decodeResource(game.getResources(), pikaID);

        pikaTop = Bitmap.createScaledBitmap(pikaTop, pikaWidth, pikaHeight, true);

        matrix = new Matrix();
        matrix.postRotate(180);
        columnDown = Bitmap.createBitmap(columnTop, 0, 0, columnTop.getWidth(), columnTop.getHeight());
        pikaDown = Bitmap.createBitmap(pikaTop, 0, 0, pikaTop.getWidth(), pikaTop.getHeight(), matrix, false);

        isScored = false;
        alive = true;

        dx = this.speed = speed;
        acceleration = 0.000000000000000000007 * game.width;
//        acceleration = Config.COLUMN_ACCELERATION;

        hasCoin = Math.random() <= 0.4;
        if (hasCoin) {
            coin = new Coin(game, hole / 3);
        }

        generateAltitude();
    }


    boolean isAlive() {
        return alive;
    }

    void setAlive(boolean alive) {
        this.alive = alive;
    }

    boolean isOut() {
        return pikaX + pikaWidth < 0;
    }

    private void generateAltitude() {
        int scatter = (int) (Config.COLUMN_HEIGHT_SCATTER * screenHeight);
        int newColumnTopHeight;
        int currentRnd = (int) ((Math.random() * (1 + scatter)) - scatter / 2);
        if (columnTopHeight + currentRnd < pikaHeight || pikaDownY + 2 * pikaHeight + currentRnd > Config.GROUND_Y * screenHeight)
            newColumnTopHeight = columnTopHeight - currentRnd;
        else
            newColumnTopHeight = columnTopHeight + currentRnd;

        pikaTopY = columnTopHeight = newColumnTopHeight;
        columnDownHeight = bottom - columnTopHeight - hole - pikaHeight * 2;
        pikaDownY = pikaTopY + pikaHeight + hole;
        columnDownY = pikaDownY + pikaHeight;

        if (columnTopHeight < columnHeightDefault) {
            columnTop = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnHeightDefault, false);
            columnTop = Bitmap.createBitmap(columnTop, 0, 0, columnWidth, columnTopHeight);
        } else if (columnTopHeight > columnHeightDefault) {
            columnTop = Bitmap.createBitmap(mergeColumns(columnTopHeight));
        }

        if (columnDownHeight < columnHeightDefault) {
            columnDown = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnHeightDefault, false);
            columnDown = Bitmap.createBitmap(columnDown, 0, 0, columnWidth, columnDownHeight, matrix, false); // this error
        } else if (columnDownHeight > columnHeightDefault) {
            columnDown = Bitmap.createBitmap(mergeColumns(columnDownHeight));
        }

        if (hasCoin)
            coin.setY(pikaDownY - (hole + coin.getSize()) / 2);
    }

    void draw(Canvas canvas) {
        if (alive) {
            canvas.drawBitmap(columnTop, columnX, 0, null);
            canvas.drawBitmap(pikaTop, pikaX, pikaTopY, null);
            canvas.drawBitmap(pikaDown, pikaX, pikaDownY, null);
            canvas.drawBitmap(columnDown, columnX, columnDownY, null);
            if (hasCoin && !coin.isEarn())
                coin.draw(canvas);
//                canvas.drawBitmap(coin, coinX, coinY, null);
        }

    }

    private void dispose() {
        alive = false;
        this.coin = null;
        this.columnTop = null;
        this.columnDown = null;
        this.pikaDown = null;
        this.pikaTop = null;
        this.matrix = null;
        this.defaultColumn = null;
        this.game = null;
    }

    void update(double dt) {
        if (isOut()) {
            dispose();
            return;
        }
        if (alive) {

            speed += acceleration * dt;
            dx = Math.round(speed * dt);
            setX((int) (getX() - dx));

            if (hasCoin && !coin.isEarn())
                if (birdX + birdWidth >= columnX + columnWidth / 2) {
                    coin.earn();
                    game.addMoney(1);
                    game.playScoreSound();
                }

            if (!isScored && (birdX + birdWidth / 2 >= columnX + columnWidth)) {
                isScored = true;
                game.addScore();
            }

        }
    }


    int getX() {
        return pikaX;
    }

    void setX(int x) {
        pikaX = x;
        columnX = pikaX + (pikaWidth - columnWidth) / 2;
        if (hasCoin && !coin.isEarn())
            coin.setX(pikaX + (pikaWidth - coin.getSize()) / 2);
    }

    double getSpeed() {
        return speed;
    }

    boolean isCollised(Rect birdMask) {
        return ((columnX < birdMask.right) && (columnX + columnWidth > birdMask.left) &&
                ((birdMask.top < pikaTopY + pikaHeight) || (birdMask.bottom > pikaDownY)));
    }

    private Bitmap mergeColumns(int height) {
        Bitmap result = Bitmap.createBitmap(columnWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        for (int i = 0; height > 0; height -= defaultColumn.getHeight(), i++)
            canvas.drawBitmap(defaultColumn, 0, i * defaultColumn.getHeight(), null);
        return result;
    }

    int getWidth() {
        return pikaWidth;
    }
}