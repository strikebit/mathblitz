package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.network.NetworkTool;
import io.strikebit.mathblitz.sound.SoundManager;

public class ResultActivity extends AppCompatActivity {

    private static final int RC_LEADERBOARD_UI = 9004;

    private int gameMode;
    private SoundManager soundManager = new SoundManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);

        int score = getIntent().getIntExtra("score", 0);
        int highScore = getIntent().getIntExtra("highScore", 0);
        // boolean alive = getIntent().getBooleanExtra("alive", true);
        System.out.println("Score: " + score);

        TextView resultText = findViewById(R.id.text_result);
        TextView highScoreText = findViewById(R.id.text_high_score);

        highScoreText.setText(getString(R.string.high_score, highScore));

        resultText.setText(String.valueOf(score));

        soundManager.playGameEndSound(getApplicationContext());

        // Launch leaderboard
        if (score > 0
                && NetworkTool.isNetworkAvailable(this)
                && null != GoogleSignIn.getLastSignedInAccount(this)) {
            String leaderBoardId;
            if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
                leaderBoardId = getString(R.string.leaderboard_time_trial_id);
            } else {
                leaderBoardId = getString(R.string.leaderboard_survival_id);
            }
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .getLeaderboardIntent(leaderBoardId)
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }

        System.out.println("shiz: " + score);
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
        startActivity(intent);
    }
}
