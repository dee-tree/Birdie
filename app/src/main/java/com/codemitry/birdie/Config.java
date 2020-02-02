package com.codemitry.birdie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

final class Config {

    static int screen_width = 1080,  // Default values
            screen_height = 1920;
    static double scale = 1;

    static final float SPAWN_X = 0.5f;
    static final float SPAWN_Y = 0.5f;
    static final float BIRD_WIDTH = 0.185f;
    static final float BIRD_HEIGHT = 0.06f;
    static final float COLUMN_WIDTH = 0.138f;
    static final float COLUMN_HOLE = 0.198f;
    static final float PIKA_WIDTH = 0.24f;
    static final float PIKA_HEIGHT = 0.052f;
    static final float COLUMN_HEIGHT_SCATTER = 0.365f;
    static final float GROUND_HEIGHT = 0.1045f;
    static final float GRASS_HEIGHT = 0.055f;
    static final float BIRD_UP = 0.017f;
    static final float BIRD_DOWN = 0.0048f;
    static final float GROUND_Y = 0.83f;


    static final int //GROUND_Y = 1600,          // Distance from top to ground in px
//            GROUND_HEIGHT = 200,
//            GRASS_HEIGHT = 106,
//            SPAWN_X = 400,
//            SPAWN_Y = 700,            // in px
    //TIMER_INTERVAL = 20,   // in milliseconds

    //    BIRD_WIDTH = 200,
//            BIRD_HEIGHT = 110,
    //  BIRD_UP = 34,
            SCORE_INCREMENT = 1,

    RIGHT_HANDED_MODE = 0,
            LEFT_HANDED_MODE = 1,


    //    COLUMN_WIDTH = 150,
//            COLUMN_HOLE = 380,  // Hole between upper and lower columns
//            COLUMN_HEIGHT_SCATTER = 700,
            COLUMN_SPEED = 2,
    //            PIKA_WIDTH = 260,
//            PIKA_HEIGHT = 100,
            COLUMN_DIST = 4,  // Distance in column_width between two the nearest columns

    CONTINUE_PLAY = 1,     // Code of command to resume the game
            FINISH_PLAY = 2,      // Code of command to finish the game
            RESTART = 3,         // Code of command to restart the game
            TIMES_WINGS_DOWN = 6,
            FIRST_OPEN = 0,
            NOT_FIRST_OPEN = 1,

    MAX_FPS = 40,
            MAX_FRAME_SKIPS = 5,
            FRAME_PERIOD = 1000 / MAX_FPS,

    VIBRATION_ON = 1,
            VIBRATION_OFF = 0,

    BASIC_SCREEN_WIDTH = 1080,
            BASIC_SCREEN_HEIGHT = 1920;
    static String language = "en";
    static final String BEST_SCORE_PREF = "BestScore",
            LANG_PREF = "Language",
            HAND_MODE_PREF = "Hand-Mode",
            DEATHS_PREF = "Deaths",
            COLUMN_DEATHS_PREF = "Column Deaths",
            GROUND_DEATHS_PREF = "Ground Deaths",
            VIBRATION_PREF = "Vibration",
            TOTAL_SCORE_PREF = "Total score",
            IS_FIRST_OPEN = "FIRST_OPEN",
            MONEY_PREF = "Money";


    static int loadBestScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return Integer.parseInt(prefs.getString(BEST_SCORE_PREF, "0"));
    }

    static void saveBestScore(Context context, String score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(BEST_SCORE_PREF, score);
        editor.apply();
    }

    static int loadMoney(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(MONEY_PREF, 0);
    }

    static void saveMoney(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(MONEY_PREF, score);
        editor.apply();
    }

    static int loadHandMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(HAND_MODE_PREF, RIGHT_HANDED_MODE);
    }

    static void saveHandMode(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(HAND_MODE_PREF, mode);
        editor.apply();
    }

    static int loadDeaths(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(DEATHS_PREF, 0);
    }

    static void saveDeath(Context context, int deaths) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DEATHS_PREF, deaths);
        editor.apply();
    }

    static int loadGroundDeaths(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(GROUND_DEATHS_PREF, 0);
    }

    static void saveGroundDeath(Context context, int deaths) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GROUND_DEATHS_PREF, deaths);
        editor.apply();
    }

    static int loadColumnDeaths(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(COLUMN_DEATHS_PREF, 0);
    }

    static void saveColumnDeath(Context context, int deaths) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(COLUMN_DEATHS_PREF, deaths);
        editor.apply();
    }

    static int loadVibration(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(VIBRATION_PREF, VIBRATION_ON);
    }

    static void saveVibration(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(VIBRATION_PREF, mode);
        editor.apply();
    }

    static int loadTotalScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(TOTAL_SCORE_PREF, 0);
    }

    static void saveTotalScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TOTAL_SCORE_PREF, score);
        editor.apply();
    }

    static int isFirstOpen(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(IS_FIRST_OPEN, FIRST_OPEN);
    }

    static void saveFirstOpen(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(IS_FIRST_OPEN, score);
        editor.apply();
    }


}
