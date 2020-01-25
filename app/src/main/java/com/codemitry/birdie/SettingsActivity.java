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

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Locale myLocale;
    Button changeHandButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView optionsText = findViewById(R.id.settingsText);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.settings_title_scale);

        optionsText.startAnimation(anim);
        changeHandButton = findViewById(R.id.hand_mode_button);
        updateHandModeText(Config.loadHandMode(this));
    }

    public void onChangeHandModeClick(View v) {
        int mode = Config.loadHandMode(this);
        System.out.println("Mode before change: " + mode);
        if (mode == Config.LEFT_HANDED_MODE) {
            mode = Config.RIGHT_HANDED_MODE;
        } else {
            mode = Config.LEFT_HANDED_MODE;
        }
        updateHandModeText(mode);
        Config.saveHandMode(this, mode);
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
