package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class ResultActivity extends AppCompatActivity {
    private final static int interval = 1000;
    private final static int maxTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        new CountDownTimer(maxTime, interval) {
            public void onTick(long mUntilFinished) {
                // noop
            }

            public void onFinish() {
                playAgain();
            }
        }.start();
    }

    @Override
    public void onBackPressed() { }

    protected void playAgain() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
