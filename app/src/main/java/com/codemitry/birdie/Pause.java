package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Pause extends AppCompatActivity {
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setFinishOnTouchOutside(false);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_pause);
        TextView pause = findViewById(R.id.pause);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        pause.startAnimation(anim);

        Animation animCont = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_yes);
        Animation animExit = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_no);

        Button cont = findViewById(R.id.cont);
        Button exit = findViewById(R.id.exit);


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
