package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private final static int interval = 1000;
    private final static int maxTime = 5000;

    private int score;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        score = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", 0);
        boolean alive = getIntent().getBooleanExtra("alive", true);

        TextView resultText = findViewById(R.id.text_result);

        if (alive) {
            resultText.setText(String.format(Locale.US, "You got %d correct!", score));
            new CountDownTimer(maxTime, interval) {
                public void onTick(long mUntilFinished) {
                    // noop
                }

                public void onFinish() {
                    playAgain();
                }
            }.start();
        } else {
            resultText.setText(String.format(Locale.US, "Game over. Final score: %d", score));
            Button backToMenuButton = findViewById(R.id.button_back_to_menu);
            backToMenuButton.setVisibility(View.VISIBLE);
        }
    }

    public void onStartGameClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() { }

    protected void playAgain() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}
