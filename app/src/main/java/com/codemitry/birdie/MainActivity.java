package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
// <color name="menu_button_not_pressed">#009688</color>
// Ctrl + Alt + L
// app name's color: #81C784


public class MainActivity extends AppCompatActivity {
    Locale myLocale;
    //String lang = "en";
    private Button start, settings, statistics, exit;
    Animation anim;
    TextView head;
    MediaPlayer logoSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Config.screen_width = displayMetrics.widthPixels;
        Config.screen_height = displayMetrics.heightPixels;
        Config.scale = (double) Config.screen_width / Config.BASIC_SCREEN_WIDTH;
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        settings = findViewById(R.id.settings);
        statistics = findViewById(R.id.statistics);
        exit = findViewById(R.id.exit);

        loadLocale();
        anim = AnimationUtils.loadAnimation(this, R.anim.headanim);
        head = findViewById(R.id.head);
        head.startAnimation(anim);
        logoSound = MediaPlayer.create(this, R.raw.logosound);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }

    public void onStatisticsClick(View v) {
        Intent statIntent = new Intent(this, StatisticsActivity.class);
        startActivity(statIntent);
    }

    public void onSettingsClick(View v) {
        Intent optionsIntent = new Intent(this, SettingsActivity.class);
        startActivity(optionsIntent);
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

    public void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.LANG_PREF, lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(Config.LANG_PREF, "en");
        changeLang(language);
    }


    private void updateTexts() {
        start.setText(R.string.menu_start);
        settings.setText(R.string.settings);
        statistics.setText(R.string.statistics);
        exit.setText(R.string.menu_exit);
    }


    public void onExitClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else finish();
    }

    public void onStartClick(View v) {
        Config.language = myLocale.getLanguage();
        Intent gameWindow = new Intent(this, Game.class);
        startActivity(gameWindow);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onLogoClick(View v) {
        System.out.println(getString(R.string.reset));

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        ImageView logo = findViewById(R.id.logo);
        if (logoSound.isPlaying())
            logoSound.seekTo(0);
        logoSound.start();
        logo.setAnimation(anim);
    }

}