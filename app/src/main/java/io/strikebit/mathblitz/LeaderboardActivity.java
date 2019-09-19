package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

public class LeaderboardActivity extends AppCompatActivity {

    private static final int RC_LEADERBOARD_UI = 9004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        if (null != this.getSupportActionBar()) {
            this.getSupportActionBar().setTitle(getString(R.string.leaderboard));
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.showTimeTrialLeaderboard();
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

    public void onTimeTrialClick(View view) {
        showTimeTrialLeaderboard();
    }

    public void onSurvivalClick(View view) {
        showSurvivalLeaderboard();
    }

    private void showTimeTrialLeaderboard() {
        if (null != GoogleSignIn.getLastSignedInAccount(this)) {
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .getLeaderboardIntent(getString(R.string.leaderboard_time_trial_id))
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }
    }

    private void showSurvivalLeaderboard() {
        if (null != GoogleSignIn.getLastSignedInAccount(this)) {
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .getLeaderboardIntent(getString(R.string.leaderboard_survival_id))
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, RC_LEADERBOARD_UI);
                        }
                    });
        }
    }
}
