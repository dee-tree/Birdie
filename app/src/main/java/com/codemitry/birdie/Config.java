package com.codemitry.birdie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

final class Config {

    static final float BIRD_WIDTH = 0.185f;
//    static final float BIRD_HEIGHT = 0.06f;
    static final float COLUMN_WIDTH = 0.138f;
    static final float COLUMN_HOLE = 0.198f;
    static final float COLUMN_SPEED = 0.15f;
    static final float COLUMN_ACCELERATION = 0.0000045f;
    static final float PIKA_WIDTH = 0.24f;
    static final float PIKA_HEIGHT = 0.052f;
    static final float COLUMN_HEIGHT_SCATTER = 0.365f;
    static final float GROUND_HEIGHT = 0.1045f;
    static final float GRASS_HEIGHT = 0.055f;
    static final float BIRD_UP = 0.001f;
    static final float GROUND_Y = 0.83f;
    static final int TEXTURES = 2;


    static final int

            SCORE_INCREMENT = 1,
            COLUMN_DIST = 2,  // Distance in column_width between two the nearest columns

    CONTINUE_PLAY = 1,     // Code of command to resume the game
            FINISH_PLAY = 2,      // Code of command to finish the game
            RESTART = 3,         // Code of command to restart the game
            TIMES_WINGS_DOWN = 7,
            FIRST_OPEN = 0,
            NOT_FIRST_OPEN = 1,

            VIBRATION_ON = 1,
            VIBRATION_OFF = 0;
    private static final String
            DEATHS_PREF = "Deaths",
            COLUMN_DEATHS_PREF = "Column Deaths",
            GROUND_DEATHS_PREF = "Ground Deaths",
            VIBRATION_PREF = "Vibration",
            TOTAL_SCORE_PREF = "Total score",
            IS_FIRST_OPEN = "FIRST_OPEN",
            SECRET_MODE = "Secret dance mode",
            BEST_SCORE_PREF = "Best score",
            SELECTED_TEXTURE_PREF = "Selected texture",
            BOUGHT_TEXTURES = "Bought textures",
            MONEY_PREF = "Money";

    static final String
            LANG_PREF = "Language";



    static int loadBestScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(BEST_SCORE_PREF, 0);
    }

    static void saveBestScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BEST_SCORE_PREF, score);
        editor.apply();
    }

    static int loadMoney(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(MONEY_PREF, 0);
    }

    static void saveMoney(Context context, int money) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(MONEY_PREF, money);
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

    static boolean isSecretModeEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getBoolean(SECRET_MODE, false);
    }

    static void saveSecretModeEnabled(Context context, boolean mode) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SECRET_MODE, mode);
        editor.apply();
    }

    static int loadSelectedTexture(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getInt(SELECTED_TEXTURE_PREF, 0);
    }

    static void saveSelectedTexture(Context context, int texture) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SELECTED_TEXTURE_PREF, texture);
        editor.apply();
    }


    static boolean[] loadBoughtTextures(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        int i_bought = prefs.getInt(BOUGHT_TEXTURES, 1);

        // Работает с числом, в двоичном представлении биты которого с малых к старшим: 1 - куплено, 0 - не куплено
        // Узнать состояние первого фона: a & 1,  второго фона: a & 2, третьего: a & 4     (2^k)

        boolean[] a = new boolean[TEXTURES];
        for (int i = 0; i < TEXTURES; i++) {
            a[i] = (i_bought & 1) == 1;
            i_bought >>= 1;
        }
        return a;
    }


    static void saveBoughtTexture(Context context, int number, boolean state) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        int i_bought = prefs.getInt(BOUGHT_TEXTURES, 1);

        if (state) {
            i_bought |= (1 << number);
        } else {
            i_bought &= ~(1 << number);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BOUGHT_TEXTURES, i_bought);
        editor.apply();
    }


}
