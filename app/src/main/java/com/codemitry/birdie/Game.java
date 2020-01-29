package com.codemitry.birdie;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Game extends AppCompatActivity {
    GameSurfaceView gameSurfaceView;

    private boolean run = false;  // if game is running now
    private boolean started = false;  // if game started at least
    private boolean paused = false; // Need pause or not
    private boolean death = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_game);
            gameSurfaceView = new GameSurfaceView(this);
            setContentView(gameSurfaceView);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        setContentView(gameSurfaceView);
//    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRun()) {
            setPaused(true);
            setRun(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRun())
            pause();
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
                    gameSurfaceView.surfaceDestroyed(gameSurfaceView.getHolder());
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

    public void onLose() {
        Intent intent = new Intent(this, LoseActivity.class);
        intent.putExtra("score", gameSurfaceView.getScore());
        startActivityForResult(intent, 1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    public void pause() {
        setRun(false);
        Intent pauseWindow = new Intent(this, Pause.class);
        startActivityForResult(pauseWindow, 1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isDeath() {
        return death;
    }

    public void setDeath(boolean death) {
        this.death = death;
    }
}
