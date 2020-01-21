package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class LoseActivity extends AppCompatActivity {
    int result, score, bestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lose);

        this.score = getIntent().getIntExtra("score", 0);
        this.bestScore = Config.loadBestScore(this);

        if (score > bestScore) {
            Config.saveBestScore(this, String.valueOf(score));
            TextView loseText = findViewById(R.id.loseText);
            loseText.setText(R.string.best_lose);
        }

        TextView yourScoreText = findViewById(R.id.yourScore),
                bestScoreText = findViewById(R.id.bestScore);
        yourScoreText.append("  " + score);
        bestScoreText.append("  " + bestScore);

        Animation animYes = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_yes);
        Animation animNo = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_no);
        Button yes = findViewById(R.id.loseYes);
        Button no = findViewById(R.id.loseNo);
        yes.setAnimation(animYes);
        no.setAnimation(animNo);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    private void sendResult(int result) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
    }


    public void onYesClick(View view) {
        result = Config.RESTART;
        sendResult(result);
        finish();
    }


    public void onNoClick(View view) {
        result = Config.FINISH_PLAY;
        sendResult(result);
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
