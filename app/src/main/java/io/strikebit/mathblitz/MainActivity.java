package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.strikebit.mathblitz.config.GameConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            TextView buildInfo = findViewById(R.id.text_build_info);
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            buildInfo.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onStartTimeTrialGameClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        startActivity(intent);
    }

    public void onStartSurvivalGameClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_SURVIVAL);
        startActivity(intent);
    }

    public void onStartPracticeGameClick(View view) {
        Intent intent = new Intent(this, PracticeActivity.class);
        startActivity(intent);
    }

    public void viewStatsClick(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }
}
