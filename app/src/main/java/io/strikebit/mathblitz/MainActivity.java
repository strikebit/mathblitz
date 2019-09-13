package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;

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
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_VERY_EASY);
        startActivity(intent);
    }

    public void onStartPracticeGameEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_EASY);
        startActivity(intent);
    }

    public void onStartPracticeGameAdeptClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_ADEPT);
        startActivity(intent);
    }

    public void onStartPracticeGameHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_HARD);
        startActivity(intent);
    }

    public void onStartPracticeGameVeryHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_VERY_HARD);
        startActivity(intent);
    }

    public void onStartPracticeGameLegendaryClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", MathQuestionStrategy.DIFFICULTY_LEGENDARY);
        startActivity(intent);
    }
}
