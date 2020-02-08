package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

public class Pause extends AppCompatActivity {
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pause);
        TextView pause = findViewById(R.id.pause);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        pause.startAnimation(anim);

        Animation animCont = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_yes);
        Animation animExit = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_no);

        Button cont = findViewById(R.id.cont);
        Button exit = findViewById(R.id.exit);

        int handMode = Config.loadHandMode(this);
        if (handMode == Config.LEFT_HANDED_MODE) {
            LayoutParams params = (LayoutParams) cont.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = exit.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            cont.setLayoutParams(params);

            params = (LayoutParams) exit.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = cont.getId();
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            exit.setLayoutParams(params);
        } else {
            LayoutParams params = (LayoutParams) cont.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = exit.getId();
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;

            cont.setLayoutParams(params);

            params = (LayoutParams) exit.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = cont.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            exit.setLayoutParams(params);
        }




        cont.setAnimation(animCont);
        exit.setAnimation(animExit);

    }

    private void sendResult(int result) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onContinueClick(null);
    }


    public void onContinueClick(View v) {
        result = Config.CONTINUE_PLAY;
        sendResult(result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    public void onExitClick(View v) {
        result = Config.FINISH_PLAY;
        sendResult(result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

}
