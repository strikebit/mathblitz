package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.strikebit.mathblitz.config.GameConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onStartPracticeGameVeryEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_VERY_EASY);
        startActivity(intent);
    }

    public void viewStatsClick(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }
}
