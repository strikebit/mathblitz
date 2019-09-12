package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import io.strikebit.mathblitz.factory.QuestionFactory;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;

public class GameActivity extends AppCompatActivity {
    private final static int interval = 1000;
    private final static int maxTime = 30000;

    private int totalCorrect = 0;
    private CountDownTimer countDownTimer;

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
                System.out.println("Time's up!");
                showResult();
            }
        };

        countDownTimer.start();
    }

    protected void showResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    protected void createQuestion() {
        MathQuestion mathQuestion = QuestionFactory.generate(MathQuestionStrategy.DIFFICULTY_EASY);

        TextView questionText = findViewById(R.id.math_question);
        questionText.setText(mathQuestion.getQuestion());

        LinearLayout ll = findViewById(R.id.linear_layout);
        if (ll.getChildCount() > 0) {
            ll.removeAllViews();
        }

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
                provideAnswer(possibleAnswer.equals(correctAnswer));
            }
        });
        LinearLayout ll = findViewById(R.id.linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(answerButton, lp);
    }

    protected void provideAnswer(boolean isCorrect) {
        System.out.println(isCorrect);
        if (isCorrect) {
            ++totalCorrect;
            updateTotalCorrect();
        }
        createQuestion();
    }

    protected void updateTotalCorrect() {
        TextView textView = findViewById(R.id.text_score);
        textView.setText(String.format(Locale.US,"Correct: %d", totalCorrect));
    }
}
