package com.codemitry.birdie;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResetScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_reset_score);

        TextView text = findViewById(R.id.bestScoreReset_text);
        text.append("  " + Config.loadBestScore(this));

    }

    public void onYesClick(View v) {
        Config.saveBestScore(this, String.valueOf(0));
        finish();
    }

    public void onNoClick(View v) {
        finish();
    }
}
