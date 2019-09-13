package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.strikebit.mathblitz.factory.QuestionFactory;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;
import io.strikebit.mathblitz.util.NumberUtil;

public class GameActivity extends AppCompatActivity {
    private final static int gameCountdownInterval = 1000;
    private final static int gameTime = 61000; // 61 seconds
    private final static DecimalFormat df2 = new DecimalFormat("#.##");
    private final static int startingLives = 3;

    private int questionTime = 5000; // 5 seconds
    private int livesRemaining;
    private int totalCorrect;
    private int difficulty;

    private CountDownTimer countDownTimer;
    private CountDownTimer questionTimer;
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    // TODO Make # of lifes dynamic
    private List<ImageView> lifeCollection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBar = findViewById(R.id.progress_bar);
        mediaPlayer = MediaPlayer.create(GameActivity.this, R.raw.correct);
        totalCorrect = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", MathQuestionStrategy.DIFFICULTY_EASY);
        livesRemaining = startingLives;

        createQuestionTimer();
        createQuestion();

        final TextView timerText = findViewById(R.id.text_time_remaining);
        ImageView life1 = findViewById(R.id.image_life1);
        ImageView life2 = findViewById(R.id.image_life2);
        ImageView life3 = findViewById(R.id.image_life3);
        lifeCollection.add(life1);
        lifeCollection.add(life2);
        lifeCollection.add(life3);

        countDownTimer = new CountDownTimer(gameTime, gameCountdownInterval) {
            public void onTick(long mUntilFinished) {
                timerText.setText(String.format(Locale.US, "%ds", mUntilFinished / 1000));
            }

            public void onFinish() {
                TextView questionText = findViewById(R.id.math_question);
                questionText.setVisibility(View.INVISIBLE);
                showResult();
            }
        };

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                countDownTimer.start();
                questionTimer.start();
            }
        });
    }

    protected void createQuestionTimer() {
        questionTimer = new CountDownTimer(questionTime, 10) {
            public void onTick(long mUntilFinished) {
                double i = (double) mUntilFinished;
                progressBar.setProgress((int) Math.round(((i / questionTime) * 100)));
            }
            public void onFinish() {
                progressBar.setProgress(0);
                provideAnswer(false);
            }
        };
    }

    protected void killTimers() {
        countDownTimer.cancel();
        questionTimer.cancel();
    }

    protected void showResult() {
        cleanup();
        killTimers();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", totalCorrect);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("alive", livesRemaining > 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killTimers();
        countDownTimer = null;
        questionTimer = null;
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

        questionTimer.start();
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
                // TODO Show a neat effect
                questionTimer.cancel();
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
            showResult();
        }
    }

    protected void loseLife() {
        --livesRemaining;

        System.out.println("lose life " + livesRemaining);

        ImageView iv = lifeCollection.get(livesRemaining);
        iv.setVisibility(View.INVISIBLE);
        checkForDeath();
    }

    protected void gainLife() {
        if (livesRemaining == startingLives) {
            return;
        }
        ++livesRemaining;

        System.out.println("gain life " + livesRemaining);

        ImageView iv = lifeCollection.get(livesRemaining - 1);
        iv.setVisibility(View.VISIBLE);
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
            loseLife();
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
            questionTime += 1000;
            createQuestionTimer();
            gainLife();
            difficulty = MathQuestionStrategy.DIFFICULTY_ADEPT;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 10 correct!", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (totalCorrect == 20) {
            questionTime += 1000;
            createQuestionTimer();
            gainLife();
            difficulty = MathQuestionStrategy.DIFFICULTY_HARD;
            Toast toast = Toast.makeText(getApplicationContext(), "You got 20 correct!", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (totalCorrect == 30) {
            questionTime += 1000;
            createQuestionTimer();
            gainLife();
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
