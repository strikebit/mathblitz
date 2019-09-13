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

import java.text.DecimalFormat;
import java.util.Locale;

import io.strikebit.mathblitz.factory.QuestionFactory;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;
import io.strikebit.mathblitz.util.NumberUtil;

public class GameActivity extends AppCompatActivity {
    private final static int interval = 1000;
    private final static int maxTime = 31000;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    private int livesRemaining;
    private int totalCorrect;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        totalCorrect = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", MathQuestionStrategy.DIFFICULTY_EASY);
        livesRemaining = 3;

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

        // countDownTimer.start();
        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.correct);
    }

    protected void showResult() {
        cleanup();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", totalCorrect);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("alive", livesRemaining > 0);
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
        MathQuestion mathQuestion = QuestionFactory.generate(difficulty);

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
        boolean isFloat = NumberUtil.numberHasDecimal(possibleAnswer);
        if (isFloat) {
            answerButton.setText(df2.format(possibleAnswer));
        } else {
            answerButton.setText(String.valueOf(possibleAnswer));
        }
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
            System.out.println("I AM DEAD");
            System.out.println(livesRemaining);
            countDownTimer.cancel();
            showResult();
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
            System.out.println("WRONG");
            System.out.println(livesRemaining);
            --livesRemaining;
            checkForDeath();
        }
        if (livesRemaining > 0){
            createQuestion();
        }
        System.out.println("total correct: ");
        System.out.println(totalCorrect);
    }

    protected void handleAchievements() {
        // TODO refactor
        if (totalCorrect == 10) {
            difficulty = MathQuestionStrategy.DIFFICULTY_ADEPT;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 10 correct!", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (totalCorrect == 20) {
            difficulty = MathQuestionStrategy.DIFFICULTY_HARD;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 20 correct!", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (totalCorrect == 20) {
            difficulty = MathQuestionStrategy.DIFFICULTY_LEGENDARY;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 30 correct!", Toast.LENGTH_SHORT);
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
