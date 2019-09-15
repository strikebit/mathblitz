package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class StatsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        new Thread(new Runnable() {
            public void run() {
                sharedPref = getSharedPreferences(getString(R.string.game_shared_preferences), Context.MODE_PRIVATE);

                int timeTrialHighScore = sharedPref.getInt(getString(R.string.high_score_time_trial_key), 0);
                int survivalHighScore = sharedPref.getInt(getString(R.string.high_score_survival_key), 0);
                int totalQuestionsAnswered = sharedPref.getInt(getString(R.string.total_questions_answered), 0);
                int totalQuestionsCorrect = sharedPref.getInt(getString(R.string.total_questions_correct), 0);
                int mostCorrectInRow = sharedPref.getInt(getString(R.string.most_correct_in_a_row), 0);
                float fastestCorrectAnswer = sharedPref.getFloat(getString(R.string.fastest_correct_answer), 60000.0f);

                TextView tvTimeTrialHighScore = findViewById(R.id.text_time_trial_high_score);
                tvTimeTrialHighScore.setText(String.format(Locale.US, "%d", timeTrialHighScore));

                TextView tvSurvivalHighScore = findViewById(R.id.text_survival_high_score);
                tvSurvivalHighScore.setText(String.format(Locale.US, "%d", survivalHighScore));

                TextView tvQuestionsAnswered = findViewById(R.id.text_questions_answered);
                tvQuestionsAnswered.setText(String.format(Locale.US, "%d", totalQuestionsAnswered));

                TextView tvQuestionsCorrect = findViewById(R.id.text_questions_correct);
                tvQuestionsCorrect.setText(String.format(Locale.US, "%d", totalQuestionsCorrect));

                TextView tvMostCorrectInRow = findViewById(R.id.text_most_correct_in_a_row);
                tvMostCorrectInRow.setText(String.format(Locale.US, "%d", mostCorrectInRow));

                TextView tvFastestCorrectAnswer = findViewById(R.id.text_fastest_answer);
                if (fastestCorrectAnswer < 60000.0f) {
                    tvFastestCorrectAnswer.setText(getString(R.string.fastest_time_in_seconds, (fastestCorrectAnswer / 1000)));
                } else {
                    tvFastestCorrectAnswer.setText("--");
                }

                TextView tvPercentCorrect = findViewById(R.id.text_percent_correct);
                if (totalQuestionsCorrect > 0) {
                    tvPercentCorrect.setText(
                            String.format(
                                    Locale.US,
                                    "%.2f%%",
                                    ((double) totalQuestionsCorrect / (double) totalQuestionsAnswered) * 100
                            )
                    );
                } else {
                    tvPercentCorrect.setText("--");
                }
            }
        }).start();
    }
}
