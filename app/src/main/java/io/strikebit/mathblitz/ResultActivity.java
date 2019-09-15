package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import io.strikebit.mathblitz.config.GameConfig;

public class ResultActivity extends AppCompatActivity {
    private int gameMode;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        difficulty = getIntent().getIntExtra("difficulty", 0);

        int score = getIntent().getIntExtra("score", 0);
        int highScore = getIntent().getIntExtra("highScore", 0);
        boolean alive = getIntent().getBooleanExtra("alive", true);

        TextView resultText = findViewById(R.id.text_result);
        TextView highScoreText = findViewById(R.id.text_high_score);

        highScoreText.setText(getString(R.string.high_score, highScore));

        if (alive) {
            resultText.setText(String.format(Locale.US, "Time's up! Final score: %d", score));
        } else {
            resultText.setText(String.format(Locale.US, "Game over. Final score: %d", score));
        }
    }

    public void onBackToMenuClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void playAgainClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", gameMode);
        // intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}
