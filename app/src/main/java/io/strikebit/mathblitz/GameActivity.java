package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import io.strikebit.mathblitz.factory.QuestionFactory;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;

public class GameActivity extends AppCompatActivity {
    private final static int interval = 1000;
    private final static int maxTime = 30000;

    private int livesRemaining = 3;
    private int totalCorrect = 0;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private int gameMode = MathQuestionStrategy.DIFFICULTY_EASY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        createQuestion();

        final TextView timerText = findViewById(R.id.text_time_remaining);

        countDownTimer = new CountDownTimer(maxTime, interval) {
            public void onTick(long mUntilFinished) {
                timerText.setText(String.format(Locale.US, "%ds", mUntilFinished / 1000));
            }

            public void onFinish() {
                TextView questionText = findViewById(R.id.math_question);
                questionText.setVisibility(View.INVISIBLE);
                showResult();
            }
        };

        countDownTimer.start();
        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.correct);
    }

    protected void showResult() {
        cleanup();
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    protected void cleanup() {
        LinearLayout ll = findViewById(R.id.linear_layout);
        if (ll.getChildCount() > 0) {
            ll.removeAllViews();
        }
    }

    protected void createQuestion() {
        MathQuestion mathQuestion = QuestionFactory.generate(gameMode);

        TextView questionText = findViewById(R.id.math_question);
        questionText.setText(mathQuestion.getQuestion());

        cleanup();

        int count = 0;
        for (Number answer : mathQuestion.getAnswers()) {
            addAnswerButton(count, answer, mathQuestion.getCorrectAnswer());
            ++count;
        }
    }

    protected void addAnswerButton(int index, final Number possibleAnswer, final Number correctAnswer) {
        final Button answerButton = new Button(this);
        answerButton.setId(index);
        answerButton.setText(String.valueOf(possibleAnswer));
        answerButton.setAllCaps(false);
        answerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean isCorrect = possibleAnswer.equals(correctAnswer);
                // if (!isCorrect) {
                    // answerButton.setBackgroundColor(Color.RED);
                // }
                provideAnswer(isCorrect);
            }
        });
        LinearLayout ll = findViewById(R.id.linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(answerButton, lp);
    }

    protected void checkForDeath() {
        if (0 == livesRemaining) {
            cleanup();
            countDownTimer.cancel();
            TextView questionText = findViewById(R.id.math_question);
            questionText.setText(String.format(Locale.US, "Game over. You got %d correct", totalCorrect));
        }
    }

    protected void provideAnswer(boolean isCorrect) {
        if (isCorrect) {
            ++totalCorrect;
            updateTotalCorrect();
            handleAchievements();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else {
            --livesRemaining;
            checkForDeath();
        }
        if (livesRemaining > 0){
            createQuestion();
        }
    }

    protected void handleAchievements() {
        if (totalCorrect == 10) {
            // TODO refactor
            gameMode = MathQuestionStrategy.DIFFICULTY_ADEPT;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 10 correct!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected void updateTotalCorrect() {
        TextView textView = findViewById(R.id.text_score);
        textView.setText(String.format(Locale.US,"Correct: %d", totalCorrect));
    }

    @Override
    public void onBackPressed() { }
}
