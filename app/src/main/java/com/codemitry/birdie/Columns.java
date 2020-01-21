package com.codemitry.birdie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

class Columns {
    private Context context;
    private GameSurfaceView surface;
    private int[] imgs;
    private int columnCount;
    private int birdX;
    private Column[] column;

    Columns(Context context, int[] imgs, int birdX, GameSurfaceView surface) {
        this.context = context;
        this.birdX = birdX;
        this.surface = surface;
        this.imgs = imgs;

        columnCount = (int) (Config.screen_width / (Config.COLUMN_WIDTH * Config.scale) / 2);
    }


    void createColumns() {
        column = new Column[columnCount];
        for (int i = 0; i < column.length; i++) {
            column[i] = new Column(context, imgs, birdX, surface);
            column[i].setX(Config.screen_width * 5 / 6 + i * Config.COLUMN_DIST * column[i].columnWidth);
        }
        if (columnCount > 1)
            column[1].generateAltitude();
    }

    void updateColumns() {
        if (!surface.game.isDeath() && surface.game.isRun()) {
            for (int i = 0; i < column.length; i++) {
                column[i].update();
                if (column[i].isOut())
                    column[i].setX((i - 1 < 0 ? (column[column.length - 1].getX()) : column[i - 1].getX()) + Config.COLUMN_DIST * column[i].columnWidth);  // start position of column
            }
        }
    }

    void drawColumns(Canvas canvas) {
        for (Column col : column) {
            if (col.getX() < Config.screen_width)
                col.draw(canvas);
        }
    }

    Column[] getColumns() {
        return column;
    }


    class Column {


        private Bitmap columnTop, columnDown;
        private Bitmap pikaTop, pikaDown;
        private int columnX, columnTopHeight, columnDownY, columnDownHeight, columnWidth;
        private int pikaX, pikaTopY, pikaDownY, pikaHeight, pikaWidth;
        private int hole;
        private int birdX, birdWidth = (int) (Config.BIRD_WIDTH * Config.scale);
        private int speed = Config.COLUMN_SPEED;
        private int bottom;
        private boolean isScored;
        private int columnHeightDefault;
        private Bitmap defaultColumn;

        private GameSurfaceView surface;

        private Matrix matrix;

        Column(Context context, int[] resource, int birdX, GameSurfaceView surface) {
            this.birdX = birdX;
            this.surface = surface;

            bottom = (int) (Config.screen_height - Config.scale * (Config.GROUND_HEIGHT));

            //screenWidth = Config.screen_width;
            hole = (int) (Config.COLUMN_HOLE * Config.scale);

            pikaWidth = (int) (Config.PIKA_WIDTH * Config.scale);
            pikaHeight = (int) (Config.PIKA_HEIGHT * Config.scale);

            columnWidth = (int) (Config.COLUMN_WIDTH * Config.scale);
            columnDownHeight = columnTopHeight = columnHeightDefault = (bottom - hole) / 2 - pikaHeight;
            columnDownY = bottom - columnDownHeight;

            pikaTopY = columnTopHeight;
            pikaDownY = bottom - columnDownHeight - pikaHeight;

            defaultColumn = BitmapFactory.decodeResource(context.getResources(), resource[0]);
            defaultColumn = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnTopHeight, false);

            columnTop = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnTopHeight, false);
            pikaTop = BitmapFactory.decodeResource(context.getResources(), resource[1]);

            pikaTop = Bitmap.createScaledBitmap(pikaTop, pikaWidth, pikaHeight, true);

            matrix = new Matrix();
            matrix.postRotate(180);
            columnDown = Bitmap.createBitmap(columnTop, 0, 0, columnTop.getWidth(), columnTop.getHeight());
            pikaDown = Bitmap.createBitmap(pikaTop, 0, 0, pikaTop.getWidth(), pikaTop.getHeight(), matrix, false);

            isScored = false;


        }


        void setX(int x) {
            pikaX = x;
            columnX = pikaX + (pikaWidth - columnWidth) / 2;
        }


        boolean isOut() {
            return pikaX + pikaWidth < 0;
        }

        private void generateAltitude() {
            int scatter = (int) (Config.COLUMN_HEIGHT_SCATTER * Config.scale);
            int newColumnTopHeight;
            int currentRnd = (int) ((Math.random() * (1 + scatter)) - scatter / 2);
            if (columnTopHeight + currentRnd < pikaHeight || pikaDownY + 2 * pikaHeight + currentRnd > Config.GROUND_Y)
                newColumnTopHeight = columnTopHeight - currentRnd;
            else newColumnTopHeight = columnTopHeight + currentRnd;

            pikaTopY = columnTopHeight = newColumnTopHeight;
            columnDownHeight = bottom - columnTopHeight - hole - pikaHeight * 2;
            pikaDownY = bottom - columnDownHeight - pikaHeight;
            columnDownY = pikaDownY + pikaHeight;

//            new  AsyncTask<Object, Void, Void>() {
//                @Override
//                protected Void doInBackground(Object[] objects) {
            if (columnTopHeight < columnHeightDefault) {
                columnTop = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnHeightDefault, false);
                columnTop = Bitmap.createBitmap(columnTop, 0, 0, columnWidth, columnTopHeight);
            } else if (columnTopHeight > columnHeightDefault) {
                columnTop = Bitmap.createBitmap(mergeColumns(columnTopHeight));
                //columnTop = Bitmap.createScaledBitmap(columnTop, columnWidth, columnTopHeight, true);
            }

            if (columnDownHeight < columnHeightDefault) {
                columnDown = Bitmap.createScaledBitmap(defaultColumn, columnWidth, columnHeightDefault, false);
                columnDown = Bitmap.createBitmap(columnDown, 0, 0, columnWidth, columnDownHeight, matrix, false);
            } else if (columnDownHeight > columnHeightDefault) {
                columnDown = Bitmap.createBitmap(mergeColumns(columnDownHeight));
            }
//                    return null;
        }
//            }.execute(columnTop, columnDown);
//        }

        void draw(Canvas canvas) {
            canvas.drawBitmap(columnTop, columnX, 0, null);
            canvas.drawBitmap(pikaTop, pikaX, pikaTopY, null);
            canvas.drawBitmap(pikaDown, pikaX, pikaDownY, null);
            canvas.drawBitmap(columnDown, columnX, columnDownY, null);
        }

        void update() {

            if (!isScored && (birdX + birdWidth > columnX + columnWidth / 2)) {
                isScored = true;
                surface.addScore();

                if (surface.scoreSound.isPlaying())
                    surface.scoreSound.seekTo(0);
                surface.scoreSound.start();
            }
            columnX -= speed;
            pikaX -= speed;
            if (isOut()) {
                isScored = false;
                this.generateAltitude();
            }
        }

        int getX() {
            return pikaX;
        }

        boolean isCollised(RectF birdMask) {
            return ((columnX <= birdMask.right) && (columnX + columnWidth >= birdMask.left) &&
                    ((birdMask.top <= pikaTopY + pikaHeight) || (birdMask.bottom >= pikaDownY)));
        }

        private Bitmap mergeColumns(int height) {
            Bitmap result = Bitmap.createBitmap(columnWidth, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            //Paint paint = new Paint();
            for (int i = 0; height > 0; height -= defaultColumn.getHeight(), i++)
                canvas.drawBitmap(defaultColumn, 0, i * defaultColumn.getHeight(), null);
            return result;
        }

    }

}
