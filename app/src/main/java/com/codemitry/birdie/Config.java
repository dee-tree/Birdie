package com.codemitry.birdie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

final class Config {

    static int screen_width = 1080,  // Default values
            screen_height = 1920;
    static double scale = 1;

    static final int GROUND_Y = 1600,          // Distance from top to ground in px
            GROUND_HEIGHT = 200, //200,
            GRASS_HEIGHT = 106,
            SPAWN_X = 400,
            SPAWN_Y = 700,            // in px
    //TIMER_INTERVAL = 20,   // in milliseconds

    BIRD_WIDTH = 200,
            BIRD_HEIGHT = 110,
            BIRD_UP = 34,
            SCORE_INCREMENT = 1,

    RIGHT_HANDED_MODE = 0,
            LEFT_HANDED_MODE = 1,


    COLUMN_WIDTH = 150,
            COLUMN_HOLE = 375,  // Hole between upper and lower columns
            COLUMN_HEIGHT_SCATTER = 700,
            COLUMN_SPEED = 2,
            PIKA_WIDTH = 260,
            PIKA_HEIGHT = 100,
            COLUMN_DIST = 4,  // Distance in column_width between two the nearest columns

    CONTINUE_PLAY = 1,     // Code of command to resume the game
            FINISH_PLAY = 2,      // Code of command to finish the game
            RESTART = 3,         // Code of command to restart the game
            TIMES_WINGS_DOWN = 4,

    MAX_FPS = 40,
            MAX_FRAME_SKIPS = 5,
            FRAME_PERIOD = 1000 / MAX_FPS,

    BASIC_SCREEN_WIDTH = 1080,
            BASIC_SCREEN_HEIGHT = 1920;
    static String language = "en";
    static final String BEST_SCORE_PREF = "BestScore",
            LANG_PREF = "Language",
            HAND_MODE_PREF = "Hand-Mode",
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
        return Integer.parseInt(prefs.getString(MONEY_PREF, "0"));
    }

    static void saveMoney(Context context, String score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MONEY_PREF, score);
        editor.apply();
    }

    static int loadHandMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(HAND_MODE_PREF, 0);
    }

    static void saveHandMode(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(HAND_MODE_PREF, mode);
        editor.apply();
    }



}
