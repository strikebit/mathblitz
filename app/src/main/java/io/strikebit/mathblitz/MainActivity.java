package io.strikebit.mathblitz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.network.NetworkTool;

public class MainActivity extends AppCompatActivity {

    private static final int RC_CODE = 9010;
    private static final int RC_CODE2 = 9011;

    private SharedPreferences sharedPref;
    private int timeTrialHighScore = 0;
    private int survivalHighScore = 0;

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
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        signInSilently();

        MobileAds.initialize(this);
        AdView mAdView = findViewById(R.id.ad_main_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        new Thread(new Runnable() {
            public void run() {
                sharedPref = getSharedPreferences(getString(R.string.game_shared_preferences), Context.MODE_PRIVATE);
                String timeTrialKey = getString(R.string.high_score_time_trial_key);
                String survivalKey = getString(R.string.high_score_survival_key);
                timeTrialHighScore = sharedPref.getInt(timeTrialKey, 0);
                survivalHighScore = sharedPref.getInt(survivalKey, 0);
            }
        }).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LinearLayout ll = findViewById(R.id.game_mode_layout);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            ll.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll.setOrientation(LinearLayout.VERTICAL);
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
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (NetworkTool.isNetworkAvailable(this)
                && !GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            startSignInIntent(RC_CODE2);
        } else {
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        }
    }

    public void leaderboardClick(View view) {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (NetworkTool.isNetworkAvailable(this)
                && !GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            startSignInIntent(RC_CODE);
        } else {
            Intent intent = new Intent(this, LeaderboardActivity.class);
            startActivity(intent);
        }
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            // upload leaderboard stats if not null
            updateLeaderboards(account);
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient
                    .silentSignIn()
                    .addOnCompleteListener(
                            this,
                            new OnCompleteListener<GoogleSignInAccount>() {
                                @Override
                                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                    if (task.isSuccessful()) {
                                        // The signed in account is stored in the task's result.
                                        GoogleSignInAccount signedInAccount = task.getResult();
                                        updateLeaderboards(account);
                                    } else {
                                        // Player will need to sign-in explicitly using via UI.
                                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                                        // Interactive Sign-in.
                                        // startSignInIntent();
                                    }
                                }
                            });
        }
    }

    private void updateLeaderboards(GoogleSignInAccount account) {
        if (timeTrialHighScore > 0) {
            Games.getLeaderboardsClient(this, account).submitScore(getString(R.string.leaderboard_time_trial_id), timeTrialHighScore);
        }
        if (survivalHighScore > 0) {
            Games.getLeaderboardsClient(this, account).submitScore(getString(R.string.leaderboard_survival_id), survivalHighScore);
        }
    }

    private void startSignInIntent(int rcCode) {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, rcCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Trace myTrace = FirebasePerformance.getInstance().newTrace("sign_in_intent");
        myTrace.start();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CODE || requestCode == RC_CODE2) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                myTrace.putAttribute("sign_in", "success");
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                updateLeaderboards(signedInAccount);
                if (requestCode == RC_CODE) {
                    Intent intent = new Intent(this, LeaderboardActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, StatsActivity.class);
                    startActivity(intent);
                }
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    myTrace.putAttribute("sign_in", "error: " + resultCode);
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
        myTrace.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
    }
}
