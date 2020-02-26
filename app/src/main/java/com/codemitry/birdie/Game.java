package com.codemitry.birdie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;

import java.util.ArrayList;


public class Game extends AppCompatActivity {
    private GameSurfaceView surface;
    private SoundPool media;
    private int earnScore;
    private Bird bird;
    private Ground ground;
    private ArrayList<Column> columns;
    private int columnsCount;

    private TextView loseText;
    private TextView gameText;
    private TextView scoreText;
    private ConstraintLayout loseLayout, pauseLayout;

    private Bitmap background;

    private GoogleSignInAccount lastAccount;
    private AchievementsClient achievementsClient;

    private int score, bestScore;
    int width, height;
    private boolean runned = false;  // if game is running now
    //    private boolean started = false;  // if game started at least
    private boolean paused = false; // Need pause or not
    private boolean losed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        setContentView(R.layout.activity_game);

        surface = findViewById(R.id.surface);

        ground = new Ground(this, width, height);
        bird = new Bird(this, ground.getY());

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, width, height, false);

        columns = new ArrayList<>();
        columnsCount = (int) (width / (Config.COLUMN_WIDTH * width) / 2) + 2;
        for (int i = 0; i < columnsCount; i++) {
            columns.add(new Column(this, width, height, bird.getX(), Config.COLUMN_SPEED));
            columns.get(i).setX(width * 5 / 6 + i * Config.COLUMN_DIST * columns.get(i).getWidth());
        }

        media = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        earnScore = media.load(this, R.raw.scoresound, 1);

//        scoreSound = MediaPlayer.create(this, R.raw.scoresound);

        loseText = findViewById(R.id.loseText);
        gameText = findViewById(R.id.gameText);
        scoreText = findViewById(R.id.score);
        pauseLayout = findViewById(R.id.pauseLayout);
        loseLayout = findViewById(R.id.loseLayout);

        bestScore = Config.loadBestScore(this);

        resetScore();

        lastAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (MainActivity.isSignedIn(this)) {
            final GamesClient gamesClient = Games.getGamesClient(this, lastAccount);
            gamesClient.setViewForPopups(findViewById(android.R.id.content));
            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }
    }

    public synchronized void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawBitmap(background, 0, 0, null);
            ground.draw(canvas);

            for (int i = columnsCount - 1; i >= 0; i--) {
                columns.get(i).draw(canvas);
            }

            bird.draw(canvas);
        }
    }

    public void update(int dt) {
        if (runned) {
            bird.update(dt);
            ground.update(dt);
            updateColumns(dt);
        }
    }

    public void updateColumns(int dt) {
        for (int i = columns.size() - 1; i >= 0; i--) {
            if (columns.get(i).isOut()) {
                columns.get(i).setAlive(false);

                columns.add(new Column(this, width, height, bird.getX(), columns.get(columns.size() - 2).getSpeed()));
                columns.get(columns.size() - 1).setX(columns.get(columns.size() - 2).getX() + Config.COLUMN_DIST * columns.get(i).getWidth() + (int) (Math.random() * 0.2 * Config.COLUMN_DIST * columns.get(columns.size() - 1).getWidth()));

                //columns.remove(i);
            }

        }
        // TODO: Настроить проверку коллизий
        for (int i = columns.size() - 1; i >= 0; i--) {
            columns.get(i).update(dt);
            if (isCollised(columns.get(i))) {
                Config.saveColumnDeath(this, Config.loadColumnDeaths(this) + 1);
                onLose();
            }


            if (!columns.get(i).isAlive()) {
                columns.remove(i);

            }
//            System.out.println("i: " + i + "  dx: " + columns.get(i).dx + "  speed: " + columns.get(i).speed);
        }

    }

    boolean isCollised(Column column) {
        return column.isCollised(bird.mask);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!losed && runned) {
            pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (runned && !losed && surface.runned())
            pause();
        else if (losed)
            finish();
        else if (!runned && !surface.runned())
            onContinueClick(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int result;
        if (requestCode == 1) {
            assert data != null;
            result = data.getIntExtra("result", 0);
            switch (result) {
                case Config.FINISH_PLAY:
                    surface.surfaceDestroyed(surface.getHolder());
                    this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                case Config.RESTART:
                    this.finish();
                    startActivity(new Intent(this, Game.class));
                    break;
                case Config.CONTINUE_PLAY:
                    setPaused(true);
            }
        }

    }

    private void vibrate() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (v != null) {
                    v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }

    public void onLose() {
        this.runned = false;
        this.paused = false;
        this.losed = true;

        Config.saveTotalScore(this, Config.loadTotalScore(this) + score);
        Config.saveDeath(this, (Config.loadDeaths(this) + 1));

        if (Config.loadVibration(this) == 1)
            vibrate();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((TextView) loseLayout.getViewById(R.id.yourScore)).append("  " + score);
                ((TextView) loseLayout.getViewById(R.id.bestScore)).append("  " + bestScore);

                if (score > bestScore) {
                    Config.saveBestScore(Game.this, score);
                    loseText.setText(getResources().getString(R.string.best_lose));
                    loseText.setTextColor(getResources().getColor(R.color.green));

                    if (MainActivity.isSignedIn(Game.this)) {
                        Games.getEventsClient(Game.this, lastAccount).increment(getString(R.string.event_new_top_score), bestScore);
                        Games.getLeaderboardsClient(Game.this, lastAccount).submitScore(getString(R.string.leaderboard_birdie_rating_by_score), bestScore);
                    }
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                loseLayout.setVisibility(View.VISIBLE);

                if (MainActivity.isSignedIn(Game.this)) {
                    achievementsClient.increment(getString(R.string.achievement_try_again_and_again), 1);

                    if (score >= 1)
                        achievementsClient.unlock(getString(R.string.achievement_the_first_time));
                    if (score >= 15)
                        achievementsClient.unlock(getString(R.string.achievement_fifteen_steps));
                    if (score >= 25)
                        achievementsClient.unlock(getString(R.string.achievement_twenty_five));
                    if (score >= 50)
                        achievementsClient.unlock(getString(R.string.achievement_fifty));
                    if (score >= 100)
                        achievementsClient.unlock(getString(R.string.achievement_youre_birdie_king));
                }
            }
        });

    }

    void playScoreSound() {
        media.play(earnScore, 1, 1, 1, 0, 1);
    }


    public void pause() {
        if (!losed) {
            runned = false;
            paused = true;
            surface.setRunned(false);
            pauseLayoutShow();
        }
    }

    public boolean isRun() {
        return runned;
    }

    public void setRun(boolean run) {
        this.runned = run;
    }


    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isLosed() {
        return losed;
    }

    public void setLosed(boolean losed) {
        this.losed = losed;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!paused && !losed) {
            if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
                if (!runned) {
                    gameTextHide();
                    setRun(true);
                    setPaused(false);

                    if (surface != null)
                        surface.setRunned(true);

                    bird.resetWings();
                    bird.up();
                    return true;
                }
                bird.resetWings();
                bird.up();
            }
        } else if (gameText.getVisibility() == View.VISIBLE) {
            gameTextHide();
            start();
//            setRun(true);
//            if (surface != null)
//                surface.setRunned(true);
            paused = false;
        }
        return true;
    }


    private void pauseLayoutHide() {
        pauseLayout.setAlpha(1);
        pauseLayout.animate().alpha(0);
        pauseLayout.setVisibility(View.GONE);
    }

    private void pauseLayoutShow() {
        pauseLayout.setAlpha(0);
        pauseLayout.setVisibility(View.VISIBLE);
        pauseLayout.animate().alpha(1.0f);
    }

    private void gameTextShow() {
        gameText.setAlpha(0);
        gameText.setText(getString(R.string.press_to_continue));
        gameText.setVisibility(View.VISIBLE);
        gameText.animate().alpha(1);
    }

    private void gameTextHide() {
        gameText.animate().alpha(0);
        gameText.setVisibility(View.GONE);
    }

    public void onExitClick(View v) {
        pauseLayoutHide();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    void start() {
        if (!runned) {
            runned = true;
            if (surface.runned())
                surface.setRunned(false);

            surface.gameThread = new GameThread(this, surface);

            surface.setRunned(true);
            surface.gameThread.start();
        }
    }

    public void onContinueClick(View v) {
        pauseLayoutHide();
        gameTextShow();
    }

    public void onYesClick(View v) {
        recreate();
    }

    public void onNoClick(View v) {
        finish();
    }

    void resetScore() {
        this.score = 0;
    }

    void addScore() {
        this.score += Config.SCORE_INCREMENT;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreText.setText(String.valueOf(score));
            }
        });

    }

}

