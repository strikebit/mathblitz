package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import io.strikebit.mathblitz.config.GameConfig;

public class ResultActivity extends AppCompatActivity {
    // private final static int interval = 1000;
    // private final static int maxTime = 5000;

    private int gameMode;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        score = getIntent().getIntExtra("score", 0);
        // int difficulty = getIntent().getIntExtra("difficulty", 0);
        boolean alive = getIntent().getBooleanExtra("alive", true);

        TextView resultText = findViewById(R.id.text_result);

        if (alive) {
            resultText.setText(createResultMessage());
        } else {
            resultText.setText(String.format(Locale.US, "Game over. Final score: %d", score));
            Button backToMenuButton = findViewById(R.id.button_back_to_menu);
            backToMenuButton.setVisibility(View.VISIBLE);
        }
    }

    public void onBackToMenuClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() { }

    /*
    protected void playAgain() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", gameMode);
        intent.putExtra("score", score);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
     */

    private String createResultMessage() {
        if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
            return String.format(
                    Locale.US,
                    "You got %d correct in %d seconds!",
                    score,
                    GameConfig.TIME_TRIAL_DURATION_SECONDS
            );
        }
        return String.format(Locale.US, "You got %d correct!", score);
    }
}
