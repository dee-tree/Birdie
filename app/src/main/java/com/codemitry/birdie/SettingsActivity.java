package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

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
    Button changeHandButton;
    Button vibrationButton;
    Button back;
    Button signInButton;
    LayoutParams params;
    GoogleSignInClient googleSignInClient;
    GamesClient gamesClient;

    private static final int RC_SIGN_IN = 4005;


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

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        signInButton = findViewById(R.id.sign_in);

        updateUI();
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
        ((TextView) findViewById(R.id.reset_stat_button)).setText(R.string.reset);
        ((Button) findViewById(R.id.language_button)).setText(R.string.menu_language);
        ((Button) findViewById(R.id.back)).setText(R.string.back);
        updateUI();
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
            }
        } else {
            signInButton.setText(getString(R.string.connect));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                signInButton.setBackground(getDrawable(R.drawable.google_button));
            }
        }
    }

    private void showSignIn() {
        GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
        gamesClient.setViewForPopups(findViewById(android.R.id.content));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }
}
