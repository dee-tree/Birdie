package com.codemitry.birdie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

public class ResetScoreActivity extends AppCompatActivity {
    Button yes, no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setFinishOnTouchOutside(false);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_reset_score);

        TextView text = findViewById(R.id.bestScoreReset_text);
        text.append("  " + Config.loadBestScore(this));


        yes = findViewById(R.id.yesReset_button);
        no = findViewById(R.id.noReset_button);

        int handMode = Config.loadHandMode(this);
        if (handMode == Config.LEFT_HANDED_MODE) {
            LayoutParams params = (LayoutParams) yes.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = no.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            yes.setLayoutParams(params);

            params = (LayoutParams) no.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = yes.getId();
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            no.setLayoutParams(params);
        } else {
            LayoutParams params = (LayoutParams) yes.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = no.getId();
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;

            yes.setLayoutParams(params);

            params = (LayoutParams) no.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = yes.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            no.setLayoutParams(params);
        }

    }

    public void onYesClick(View v) {
        Config.saveBestScore(this, String.valueOf(0));
        Config.saveDeath(this, 0);
        Config.saveGroundDeath(this, 0);
        Config.saveColumnDeath(this, 0);
        Config.saveMoney(this, 0);
        Config.saveTotalScore(this, 0);

        finish();
    }

    public void onNoClick(View v) {
        finish();
    }
}
