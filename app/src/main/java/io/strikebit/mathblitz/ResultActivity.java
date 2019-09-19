package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

        if (null != this.getSupportActionBar()) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (gameMode) {
                case GameConfig.GAME_MODE_TIME_TRIAL:
                    this.getSupportActionBar().setTitle(getString(R.string.time_trial));
                    break;
                case GameConfig.GAME_MODE_SURVIVAL:
                    this.getSupportActionBar().setTitle(getString(R.string.survival));
                    break;
                default:
                    this.getSupportActionBar().setTitle(getString(R.string.practice));
                    break;
            }
        }

        int score = getIntent().getIntExtra("score", 0);
        int highScore = getIntent().getIntExtra("highScore", 0);

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
