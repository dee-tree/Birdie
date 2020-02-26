package com.codemitry.birdie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
// <color name="menu_button_not_pressed">#009688</color>
// Ctrl + Alt + L
// app name's color: #81C784


public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 4005;
    private static final int RC_SIGN_IN_SHOW_ACHIEVEMENTS = 4010;
    private static final int RC_SIGN_IN_SHOW_LEADERBOARD = 4015;

    Locale myLocale;
    //String lang = "en";
    private Button start, settings, statistics, exit;
    Animation anim;
    TextView head;
    MediaPlayer logoSound;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Config.screen_width = displayMetrics.widthPixels;
        Config.screen_height = displayMetrics.heightPixels;
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

        if (isSignedIn(this))
            showSignIn(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);


        // Is app opened first time then invite to sign in to google
        if (Config.isFirstOpen(this) == Config.FIRST_OPEN) {
            Config.saveFirstOpen(this, Config.NOT_FIRST_OPEN);
            signIn(RC_SIGN_IN);

        }

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

    private static final int RC_ACHIEVEMENT_UI = 9003;

    private void showAchievements() {
        if (isSignedIn(this)) {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .getAchievementsIntent()
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                        }
                    });
        }
    }

    private static final int RC_LEADERBOARD_UI = 9004;

    private void showLeaderboard() {
        if (isSignedIn(this)) {
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .getLeaderboardIntent(getString(R.string.leaderboard_birdie_rating_by_score))
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }
    }

    public void onAchievementsClick(View v) {
        if (isSignedIn(this)) {
            showAchievements();
        } else {
            signIn(RC_SIGN_IN_SHOW_ACHIEVEMENTS);
        }
    }

    public void onLeaderBoardClick(View v) {
        if (isSignedIn(this)) {
            showLeaderboard();
        } else {
            signIn(RC_SIGN_IN_SHOW_LEADERBOARD);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //showSignIn();
            // signed
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w("Sign-in", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task;
        switch (requestCode) {
            case RC_SIGN_IN:
                task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case RC_SIGN_IN_SHOW_ACHIEVEMENTS:
                task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                showAchievements();
                break;
            case RC_SIGN_IN_SHOW_LEADERBOARD:
                task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                showLeaderboard();
                break;
        }
    }


    static boolean isSignedIn(Context context) {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    private static void signInSilently(final Context context) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
//            GoogleSignInAccount signedInAccount = account;
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            final GoogleSignInClient signInClient = GoogleSignIn.getClient(context, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            (Activity) context,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Google", "Successfull sign-in to google games account");
                                        // The signed in account is stored in the task's result.
                                        GoogleSignInAccount signedInAccount = task.getResult();
                                    } else {
                                        Log.d("Google", "Not successfull sign-in to google games account");
                                        // Player will need to sign-in explicitly using via UI.
                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                        // Interactive Sign-in.
                                    }
                                }
                            });
        }
    }

    private void signIn(int code) {
        startSignInIntent(code);
    }

    private void startSignInIntent(int code) {
        startActivityForResult(googleSignInClient.getSignInIntent(), code);

    }

    private void showSignIn(Context context) {
        GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
        gamesClient.setViewForPopups(findViewById(android.R.id.content));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }


}