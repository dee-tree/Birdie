package com.codemitry.birdie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

public class StatisticsActivity extends AppCompatActivity {

    LayoutParams params;
    Button back;
    TextView record, deaths, columnDeaths, groundDeaths, totalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        back = findViewById(R.id.stat_back);
        params = (LayoutParams) back.getLayoutParams();
        int mode = Config.loadHandMode(this);
        if (mode == Config.LEFT_HANDED_MODE) {
            params.leftMargin = 35;
            params.startToStart = LayoutParams.PARENT_ID;
            params.rightMargin = LayoutParams.UNSET;
            params.endToEnd = LayoutParams.UNSET;
            back.setLayoutParams(params);
        } else {
            params.leftMargin = LayoutParams.UNSET;
            params.rightMargin = 35;
            params.endToEnd = LayoutParams.PARENT_ID;
            params.startToStart = LayoutParams.UNSET;
            back.setLayoutParams(params);
        }

        record = findViewById(R.id.stat_record);
        record.append(" " + Config.loadBestScore(this));

        deaths = findViewById(R.id.stat_deaths);
        deaths.append(" " + Config.loadDeaths(this));

        groundDeaths = findViewById(R.id.stat_ground_deaths);
        groundDeaths.append(" " + Config.loadGroundDeaths(this));

        columnDeaths = findViewById(R.id.stat_column_deaths);
        columnDeaths.append(" " + Config.loadColumnDeaths(this));

        totalScore = findViewById(R.id.stat_total_score);
        totalScore.append(" " + Config.loadTotalScore(this));

    }

    public void onBackClick(View v) {
        onBackPressed();
    }
}
