package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Locale myLocale;
    Button changeHandButton;
    Button vibrationButton;
    Button back;
    LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView optionsText = findViewById(R.id.settingsText);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.settings_title_scale);

        optionsText.startAnimation(anim);
        changeHandButton = findViewById(R.id.hand_mode_button);
        vibrationButton = findViewById(R.id.vibration_switch_button);
        back = findViewById(R.id.back);
        params = (LayoutParams) back.getLayoutParams();

        updateVibrationText(Config.loadVibration(this));
        updateHandModeText(Config.loadHandMode(this));
        updateBackPosition(Config.loadHandMode(this));
    }

    public void onChangeHandModeClick(View v) {
        int mode = Config.loadHandMode(this);
        if (mode == Config.LEFT_HANDED_MODE) {
            mode = Config.RIGHT_HANDED_MODE;
        } else {
            mode = Config.LEFT_HANDED_MODE;
        }
        updateHandModeText(mode);
        updateBackPosition(mode);
        Config.saveHandMode(this, mode);
    }

    private void updateBackPosition(int mode) {
        if (mode == Config.RIGHT_HANDED_MODE) {
            params.leftMargin = LayoutParams.UNSET;
            params.rightMargin = 35;
            params.endToEnd = LayoutParams.PARENT_ID;
            params.startToStart = LayoutParams.UNSET;
            back.setLayoutParams(params);
        } else {
            params.leftMargin = 35;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            back.setLayoutParams(params);
        }

    }

    private void updateHandModeText(int mode) {
        if (mode == Config.LEFT_HANDED_MODE)
            changeHandButton.setText(getResources().getString(R.string.enable_right_handed_mode));
        else
            changeHandButton.setText(getResources().getString(R.string.enable_left_handed_mode));
    }

    public void onBackClick(View v) {
        onBackPressed();
    }

    public void onChangeVibrationClick(View v) {
        int mode = Config.loadVibration(this);
        if (mode == Config.VIBRATION_ON)
            mode = Config.VIBRATION_OFF;
        else
            mode = Config.VIBRATION_ON;
        updateVibrationText(mode);
        Config.saveVibration(this, mode);
    }

    private void updateVibrationText(int mode) {
        if (mode == Config.VIBRATION_ON)
            vibrationButton.setText(R.string.disable_vibration);
        else
            vibrationButton.setText(R.string.enable_vibration);

    }

    public void onResetScoreClick(View v) {
        Intent resetScore = new Intent(this, ResetScoreActivity.class);
        startActivity(resetScore);
    }

    public void onChangeLanguageClick(View v) {
        // en -> ru -> en
        String lang = getLocale();
        switch (lang) {
            case "en":
                changeLang("ru");
                break;
            case "ru":
                changeLang("en");
                break;
        }
    }

    public String getLocale() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getString(Config.LANG_PREF, "en");
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase("")) return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Config.language = lang;
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    private void updateTexts() {
        ((TextView) findViewById(R.id.settingsText)).setText(R.string.settings);
        ((TextView) findViewById(R.id.reset_score_button)).setText(R.string.reset);
        ((Button) findViewById(R.id.language_button)).setText(R.string.menu_language);
        ((Button) findViewById(R.id.back)).setText(R.string.back);
        String text = changeHandButton.getText().toString();
        updateHandModeText(Config.loadHandMode(this));
        updateVibrationText(Config.loadVibration(this));
    }

    public void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.LANG_PREF, lang);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
