package com.codemitry.birdie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private ColumnsThread columnsThread;
    private UpdateThread updateThread;
    Game game;

    private Bird bird;
    private int[] birdImgs = {R.drawable.bird1_fly, R.drawable.bird1_fall};

    Columns columns;
    int[] columnImgs = {R.drawable.column1, R.drawable.pika1};

    Bitmap background, ground, grass;

    private int score;
    MediaPlayer scoreSound;

    Paint gameTextPaint, scorePaint;

    public GameSurfaceView(Game game) {
        super(game);
        this.game = game;

        getHolder().addCallback(this);
        scoreSound = MediaPlayer.create(super.getContext(), R.raw.scoresound);

        bird = new Bird(getResources(), birdImgs, this);

        columns = new Columns(this.getContext(), columnImgs, bird.getX(), this);
        columns.createColumns();

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);//, options);
        background = Bitmap.createScaledBitmap(background, Config.screen_width, Config.screen_height, false);

        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground1);
        ground = Bitmap.createScaledBitmap(ground, Config.screen_width, (int) (Config.GROUND_HEIGHT * Config.scale), true);

        grass = BitmapFactory.decodeResource(getResources(), R.drawable.grass1);
        grass = Bitmap.createScaledBitmap(grass, Config.screen_width, (int) (Config.GRASS_HEIGHT * Config.scale), true);

        gameTextPaint = new Paint();
        gameTextPaint.setColor(getResources().getColor(R.color.menu_button_not_pressed));
        gameTextPaint.setStyle(Paint.Style.STROKE);
        gameTextPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        gameTextPaint.setTextAlign(Paint.Align.CENTER);

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setShadowLayer(5, 1, 1, Color.GRAY);
        scorePaint.setTextSize((int) (140 * Config.scale));
        scorePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);

        resetScore();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        Log.d("Surface", "Surface created");

        drawThread = new DrawThread(getHolder(), this);
        drawThread.setRunned(true);
        drawThread.start();

        columnsThread = new ColumnsThread(columns, game);
        columnsThread.setRunned(true);
        columnsThread.start();

        updateThread = new UpdateThread(this);
        updateThread.setRunned(true);
        updateThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // завершаем работу потока
        drawThread.setRunned(false);
        columnsThread.setRunned(false);
        updateThread.setRunned(false);

        Log.d("Surface", "Surface Destroyed");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_DOWN) && !game.isDeath()) {
            if (!game.isRun() && game.isStarted()) {
                game.setRun(true);
                game.setPaused(false);
                return true;
            } else if (!game.isStarted()) {
                game.setStarted(true);
                game.setDeath(false);
                game.setRun(true);
                setThreadRun(true);
            }
            bird.resetWings();
            bird.up();
        }
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            super.draw(canvas);

            canvas.drawBitmap(background, 0, 0, null);

            columns.drawColumns(canvas);

            canvas.drawBitmap(ground, 0, getHeight() - ground.getHeight(), null);
            canvas.drawBitmap(grass, 0, getHeight() - ground.getHeight() - grass.getHeight(), null);

            bird.draw(canvas);

            if (!game.isDeath()) {
                if (game.isStarted())
                    drawScore(canvas);
                else {
                    gameTextPaint.setTextSize((int) (100 * Config.scale));
                    canvas.drawText(getContext().getString(R.string.press_to_start), getWidth() / 2, getHeight() / 3, gameTextPaint);
                }
                if (game.isPaused()) {
                    if (Config.language.equals("ru"))
                        gameTextPaint.setTextSize((int) (80 * Config.scale));
                    else
                        gameTextPaint.setTextSize((int) (100 * Config.scale));
                    canvas.drawText(getContext().getString(R.string.press_to_continue), getWidth() / 2, getHeight() / 3, gameTextPaint);
                }
            }
        }
    }

    public void update() {
        if (!game.isDeath() && game.isRun()) {
            bird.update();
            if (game.isDeath()) {
                onLose();

            } else if (bird.collised(columns.getColumns())) {
                onLose();
            }


        }
    }

    public void onLose() {
        game.setDeath(true);
        game.setRun(false);
        vibrate();
        game.onLose();
        this.surfaceDestroyed(getHolder());
    }

    private void vibrate() {
        try {
            Vibrator v = (Vibrator) game.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (v != null) {
                    v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    private void drawScore(Canvas canvas) {
        canvas.drawText(String.valueOf(this.score), getWidth() / 2, getHeight() / 6, scorePaint);
    }

    public void resetScore() {
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    void addScore() {
        this.score += Config.SCORE_INCREMENT;
    }


    public void setThreadRun(boolean run) {
        drawThread.setRunned(run);
        columnsThread.setRunned(run);
        updateThread.setRunned(run);
    }


}
