package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthProvider;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    Locale myLocale;
    Button changeHandButton;
    Button vibrationButton;
    Button back;
    LayoutParams params;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();


        TextView optionsText = findViewById(R.id.settingsText);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.settings_title_scale);

        optionsText.startAnimation(anim);
        changeHandButton = findViewById(R.id.hand_mode_button);
        vibrationButton = findViewById(R.id.vibration_switch_button);
        back = findViewById(R.id.back);
        params = (LayoutParams) back.getLayoutParams();

        updateVibrationText(Config.loadVibration(this));
        updateHandModeText(Config.loadHandMode(this));
        //updateBackPosition(Config.loadHandMode(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void firebaseAuthWithPlayGames(GoogleSignInAccount acct) {
        Log.d("Firebase", "firebaseAuthWithPlayGames:" + acct.getId());

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = PlayGamesAuthProvider.getCredential(acct.getServerAuthCode());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SettingsActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            System.out.println("USER NOT NULL");
            Toast.makeText(this, user.getDisplayName() + "  " + user.getEmail(), Toast.LENGTH_LONG).show();
            System.out.println("USER:   " + user.getDisplayName());

        } else {
            System.out.println("USER IS NULL");
        }
    }

    public void onSignInClick(View v) {
        startSignInIntent();
        //signInSilently();
//                GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                        .requestServerAuthCode(getString(R.string.default_web_client_id))
//               // .requestEmail()
//               // .requestIdToken("716570647073-2aontgi14bqm7vkklr1k3ni1s21sgsfp.apps.googleusercontent.com")
//                .build();
//
//        GoogleSignInClient client = GoogleSignIn.getClient(this, options);
//        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        Intent signIngent = client.getSignInIntent();
//        startActivityForResult(signIngent, 3500);
//        //printAuthToken(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                firebaseAuthWithPlayGames(signedInAccount);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "ERROR";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    private void startSignInIntent() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 100);
    }


    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            GoogleSignInAccount signedInAccount = account;
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        // The signed in account is stored in the task's result.
                                        GoogleSignInAccount signedInAccount = task.getResult();
                                    } else {
                                        // Player will need to sign-in explicitly using via UI.
                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                        // Interactive Sign-in.
                                    }
                                }
                            });
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 3500) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            System.out.println("Result: " + result.getStatus());
//            if (result.isSuccess()) {
//                System.out.println("SUCCEEEEES!!!!");
//                GoogleSignInAccount account = result.getSignInAccount();
//                System.out.println(account.getDisplayName());
//                System.out.println(account.getEmail());
//
//            }
////            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
////            try {
////                GoogleSignInAccount account = task.getResult(ApiException.class);
////                printAuthToken(account);
////            } catch (ApiException e) {
////                printAuthToken(null);
////            }
//        }
//    }

    public void onChangeHandModeClick(View v) {
        int mode = Config.loadHandMode(this);
        if (mode == Config.LEFT_HANDED_MODE) {
            mode = Config.RIGHT_HANDED_MODE;
        } else {
            mode = Config.LEFT_HANDED_MODE;
        }
        updateHandModeText(mode);
        //updateBackPosition(mode);
        Config.saveHandMode(this, mode);
    }

//    private void updateBackPosition(int mode) {
//        if (mode == Config.RIGHT_HANDED_MODE) {
//            params.leftMargin = LayoutParams.UNSET;
//            params.rightMargin = 35;
//            params.endToEnd = LayoutParams.PARENT_ID;
//            params.startToStart = LayoutParams.UNSET;
//            back.setLayoutParams(params);
//        } else {
//            params.leftMargin = 35;
//            params.startToStart = LayoutParams.PARENT_ID;
//            params.rightMargin = LayoutParams.UNSET;
//            params.endToEnd = LayoutParams.UNSET;
//            back.setLayoutParams(params);
//        }
//
//    }

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
