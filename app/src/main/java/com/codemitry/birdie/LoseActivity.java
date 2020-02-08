package com.codemitry.birdie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayersClient;


public class LoseActivity extends AppCompatActivity {
    int result, score, bestScore;
    AchievementsClient achievementsClient;
    GoogleSignInClient googleSignInClient;
//    GamesClient gamesClient;
LeaderboardsClient leaderboardsClient;
    PlayersClient playersClient;
    GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onResume() {

        if (score > bestScore) {
            Config.saveBestScore(this, String.valueOf(score));
            bestScore = score;

            TextView loseText = findViewById(R.id.loseText);
            loseText.setTextColor(getResources().getColor(R.color.green));
            loseText.setText(R.string.best_lose);

            if (isSignedIn()) {
                Games.getEventsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .increment(getString(R.string.event_new_top_score), bestScore);
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .submitScore(getString(R.string.leaderboard_birdie_rating_by_score), bestScore);

            }
        }

        if (isSignedIn()) {
//            achievementsClient = Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this));
//            achievementsClient.load(true);
//            GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
//            gamesClient.setViewForPopups(findViewById(android.R.id.content));
//            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            achievementsClient.increment(getString(R.string.achievement_try_again_and_again), 1);

            switch (score) {
                case 1:
                    achievementsClient.unlock(getString(R.string.achievement_the_first_time));
                    break;
                case 15:
                    achievementsClient.unlock(getString(R.string.achievement_fifteen_steps));
                    break;
                case 25:
                    achievementsClient.unlock(getString(R.string.achievement_twenty_five));
                    break;
                case 50:
                    achievementsClient.unlock(getString(R.string.achievement_fifty));
                    break;
                case 100:
                    achievementsClient.unlock(getString(R.string.achievement_youre_birdie_king));
                    break;

            }


        }

        Button yes = findViewById(R.id.loseYes);
        Button no = findViewById(R.id.loseNo);

        int handMode = Config.loadHandMode(this);
        if (handMode == Config.LEFT_HANDED_MODE) {
            LayoutParams params = (LayoutParams) yes.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = no.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            yes.setLayoutParams(params);

            params = (LayoutParams) no.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = yes.getId();
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            no.setLayoutParams(params);
        } else {
            LayoutParams params = (LayoutParams) yes.getLayoutParams();
            params.leftMargin = 10;
            params.startToEnd = no.getId();
            params.rightMargin = 5;
            params.endToEnd = LayoutParams.PARENT_ID;
            params.startToStart = LayoutParams.UNSET;
            params.endToStart = LayoutParams.UNSET;

            yes.setLayoutParams(params);

            params = (LayoutParams) no.getLayoutParams();
            params.leftMargin = 5;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = 10;
            params.endToStart = yes.getId();
            params.startToEnd = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            no.setLayoutParams(params);
        }

        Animation animYes = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_yes);
        Animation animNo = AnimationUtils.loadAnimation(this, R.anim.button_lose_dialog_no);
        yes.setAnimation(animYes);
        no.setAnimation(animNo);

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //signInSilently();
        if (isSignedIn()) {
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
            achievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
            leaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
            playersClient = Games.getPlayersClient(this, googleSignInAccount);

            // This is for achievement unlock popup
            final GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
            gamesClient.setViewForPopups(findViewById(android.R.id.content));
            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }
        super.onCreate(savedInstanceState);

        this.score = getIntent().getIntExtra("score", 0);
        this.bestScore = Config.loadBestScore(this);


        setContentView(R.layout.activity_lose);

        TextView yourScoreText = findViewById(R.id.yourScore),
                bestScoreText = findViewById(R.id.bestScore);
        yourScoreText.append("  " + score);
        bestScoreText.append("  " + bestScore);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    private void sendResult(int result) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("result", result);
        setResult(Activity.RESULT_OK, returnIntent);
    }


    public void onYesClick(View view) {
        result = Config.RESTART;
        sendResult(result);
        finish();
    }


    public void onNoClick(View view) {
        result = Config.FINISH_PLAY;
        sendResult(result);
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            //updateUI(account);
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
//            Log.w("Sign-in", "signInResult:failed code=" + e.getStatusCode());
//        }
//    }
//
//    private void showAchievement() {
//        GamesClient gamesClient = Games.getGamesClient(LoseActivity.this, GoogleSignIn.getLastSignedInAccount(this));
//        gamesClient.setViewForPopups(findViewById(android.R.id.content));
//        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//        System.out.println("Google: achievement's popup enabled");
//    }

}

