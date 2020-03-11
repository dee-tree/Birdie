package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Locale myLocale;
    Button vibrationButton;
    Button back;
    Button signInButton;
    LayoutParams params;
    ConstraintLayout resetStatLayout, settingsLayout;
    LinearLayout buttonsLayout;
    GoogleSignInClient googleSignInClient;

    private static final int RC_SIGN_IN = 4005;

    int secretMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_settings);

        TextView optionsText = findViewById(R.id.settingsText);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.settings_title_scale);

        optionsText.startAnimation(anim);
        vibrationButton = findViewById(R.id.vibration_switch_button);
        back = findViewById(R.id.back);
        params = (LayoutParams) back.getLayoutParams();

        resetStatLayout = findViewById(R.id.resetStatLayout);
        settingsLayout = findViewById(R.id.settingsLayout);
        buttonsLayout = findViewById(R.id.settingsButtonLayout);

        updateVibrationText(Config.loadVibration(this));

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        signInButton = findViewById(R.id.sign_in);

        updateUI();


        settingsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (secretMode < 20)
                    secretMode++;
                else {
                    Toast.makeText(SettingsActivity.this, "Enough!", Toast.LENGTH_SHORT).show();
                    Config.saveSecretModeEnabled(SettingsActivity.this, true);
                }
                return false;
            }
        });

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void onBackClick(View v) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (resetStatLayout.getVisibility() == View.VISIBLE)
            hideResetStat();
        else {
            super.onBackPressed();
//            finish();
        }
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
        showResetStat();
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
        getBaseContext().getResources().updateConfiguration(config, null);
        getResources().updateConfiguration(config, null);
        updateTexts();
    }

    private void updateTexts() {
        ((TextView) findViewById(R.id.settingsText)).setText(R.string.settings);
        ((TextView) findViewById(R.id.reset_stat_button)).setText(R.string.reset);
        ((TextView) findViewById(R.id.bestScoreReset_text)).setText(R.string.best_score);
        ((TextView) findViewById(R.id.sureToReset_text)).setText(R.string.sure_to_reset);
        ((Button) findViewById(R.id.language_button)).setText(R.string.menu_language);
        ((Button) findViewById(R.id.back)).setText(R.string.back);
        ((Button) findViewById(R.id.yesReset_button)).setText(R.string.yes);
        ((Button) findViewById(R.id.noReset_button)).setText(R.string.no);
        updateUI();
        updateVibrationText(Config.loadVibration(this));
    }

    public void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Config.LANG_PREF, lang);
        editor.apply();
    }

    public void onSignInClick(View v) {
        if (isSignedIn()) {
            signOut();
        } else {
            signIn();
        }

    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    private void signIn() {
        startSignInIntent();
    }

    private void startSignInIntent() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            showSignIn();
            updateUI();
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI();
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w("Sign-in", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

//    private void signInSilently() {
//        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
//            // Already signed in.
//            // The signed in account is stored in the 'account' variable.
//            GoogleSignInAccount signedInAccount = account;
//        } else {
//            // Haven't been signed-in before. Try the silent sign-in first.
//            final GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
//            signInClient
//                    .silentSignIn()
//                    .addOnCompleteListener(
//                            this,
//                            new OnCompleteListener<GoogleSignInAccount>() {
//                                @Override
//                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                                    if (task.isSuccessful()) {
//
//                                        Log.d("Sign-in", "Successfull sign-in");
//                                        // The signed in account is stored in the task's result.
//                                        GoogleSignInAccount signedInAccount = task.getResult();
//                                    } else {
//                                        Log.d("Sign-in", "Error sign-in");
//                                        // Player will need to sign-in explicitly using via UI.
//                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
//                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
//                                        // Interactive Sign-in.
//                                    }
//                                }
//                            });
//        }
//    }

    private void signOut() {
        final GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signInClient.revokeAccess();
                        signInClient.signOut();
                        updateUI();
//                        Toast.makeText(SettingsActivity.this, "You are signed-out", Toast.LENGTH_SHORT).show();
                        // at this point, the user is signed out.
                    }
                });
    }

    private void updateUI() {
        if (isSignedIn()) {
            signInButton.setText(getString(R.string.connected));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                signInButton.setBackground(getDrawable(R.drawable.google_button_signed));
            } else
                signInButton.setBackground(ContextCompat.getDrawable(this, R.drawable.google_button_signed));
        } else {
            signInButton.setText(getString(R.string.connect));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                signInButton.setBackground(getDrawable(R.drawable.google_button));
            } else
                signInButton.setBackground(ContextCompat.getDrawable(this, R.drawable.google_button));
        }
    }

    private void showSignIn() {
        GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
        gamesClient.setViewForPopups(findViewById(android.R.id.content));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }

    void showResetStat() {
        for (int i = 0; i < settingsLayout.getChildCount(); i++)
            settingsLayout.getChildAt(i).setEnabled(false);
        for (int i = 0; i < buttonsLayout.getChildCount(); i++)
            buttonsLayout.getChildAt(i).setEnabled(false);
        resetStatLayout.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.bestScoreReset_text)).setText((getString(R.string.best_score) + "  " + Config.loadBestScore(this)));
    }

    void hideResetStat() {
        resetStatLayout.setVisibility(View.GONE);
        for (int i = 0; i < settingsLayout.getChildCount(); i++)
            settingsLayout.getChildAt(i).setEnabled(true);
        for (int i = 0; i < buttonsLayout.getChildCount(); i++)
            buttonsLayout.getChildAt(i).setEnabled(true);
    }

    public void onYesClick(View v) {
        Config.saveBestScore(this, 0);
        Config.saveDeath(this, 0);
        Config.saveGroundDeath(this, 0);
        Config.saveColumnDeath(this, 0);
        Config.saveMoney(this, 0);
        Config.saveTotalScore(this, 0);

        hideResetStat();
    }

    public void onNoClick(View v) {
        hideResetStat();
    }
}
