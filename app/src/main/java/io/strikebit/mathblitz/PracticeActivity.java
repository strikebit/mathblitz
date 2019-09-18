package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import io.strikebit.mathblitz.config.GameConfig;

public class PracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LinearLayout ll1 = findViewById(R.id.practice_layout_1);
        LinearLayout ll2 = findViewById(R.id.practice_layout_2);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            ll1.setOrientation(LinearLayout.HORIZONTAL);
            ll2.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll1.setOrientation(LinearLayout.VERTICAL);
            ll2.setOrientation(LinearLayout.VERTICAL);
        }
    }

    public void onStartPracticeGameVeryEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_VERY_EASY);
        startActivity(intent);
    }

    public void onStartPracticeGameEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_EASY);
        startActivity(intent);
    }

    public void onStartPracticeGameAdeptClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_ADEPT);
        startActivity(intent);
    }

    public void onStartPracticeGameHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_HARD);
        startActivity(intent);
    }

    public void onStartPracticeGameVeryHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_VERY_HARD);
        startActivity(intent);
    }

    public void onStartPracticeGameGeniusClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_LEGENDARY);
        startActivity(intent);
    }
}
