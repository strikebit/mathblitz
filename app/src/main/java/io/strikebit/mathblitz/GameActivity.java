package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.level.LevelManager;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.sound.SoundManager;
import io.strikebit.mathblitz.util.NumberUtil;

public class GameActivity extends AppCompatActivity {
    private final static int gameCountdownInterval = 1000;
    private final static int gameTime = GameConfig.TIME_TRIAL_DURATION_SECONDS * 1000;
    private final static DecimalFormat df2 = new DecimalFormat("#.##");
    private final static int startingLives = 3;

    private int gameMode;
    private int questionTime = 5000; // 5 seconds
    private int livesRemaining = startingLives;
    private int score;
    private int highScore;
    private int difficulty;
    private boolean highScoreBreached = false;
    private boolean gameStarted = false;

    private SharedPreferences sharedPref;
    private CountDownTimer countDownTimer;
    private CountDownTimer questionTimer;
    private ProgressBar progressBar;
    private SoundManager soundManager = new SoundManager();
    // TODO Make # of lives dynamic
    private List<ImageView> lifeCollection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBar = findViewById(R.id.progress_bar);
        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        score = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", GameConfig.DIFFICULTY_EASY);

        new Thread(new Runnable() {
            public void run() {
                sharedPref = getPreferences(Context.MODE_PRIVATE);
                String highScoreKey;
                if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
                    highScoreKey = getString(R.string.high_score_time_trial_key);
                } else {
                    highScoreKey = getString(R.string.high_score_survival_key);
                }
                highScore = sharedPref.getInt(highScoreKey, 0);
            }
        }).start();

        if (GameConfig.GAME_MODE_PRACTICE != gameMode) {
            createQuestionTimer();
        }
        createQuestion();

        final TextView timerText = findViewById(R.id.text_time_remaining);
        timerText.setVisibility(View.INVISIBLE);

        ImageView life1 = findViewById(R.id.image_life1);
        ImageView life2 = findViewById(R.id.image_life2);
        ImageView life3 = findViewById(R.id.image_life3);
        lifeCollection.add(life1);
        lifeCollection.add(life2);
        lifeCollection.add(life3);

        if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
            timerText.setVisibility(View.VISIBLE);
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
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameStarted = true;
                if (null != countDownTimer) {
                    countDownTimer.start();
                }
                if (null != questionTimer) {
                    questionTimer.start();
                }
            }
        }, 3000);

    }

    public void onBackToMenuClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected void createQuestionTimer() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }
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
        if (null != countDownTimer) {
            countDownTimer.cancel();
        }
        if (null != questionTimer) {
            questionTimer.cancel();
        }
    }

    protected void showResult() {
        clearPreviousAnswers();
        killTimers();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("gameMode", gameMode);
        intent.putExtra("score", score);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("alive", livesRemaining > 0);
        intent.putExtra("highScore", highScore);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        killTimers();
        countDownTimer = null;
        questionTimer = null;
    }

    protected void clearPreviousAnswers() {
        LinearLayout ll = findViewById(R.id.linear_layout);
        if (ll.getChildCount() > 0) {
            ll.removeAllViews();
        }
    }

    protected void createQuestion() {
        MathQuestion mathQuestion = QuestionFactory.generate(difficulty);

        TextView questionText = findViewById(R.id.math_question);
        questionText.setText(mathQuestion.getQuestion());

        clearPreviousAnswers();

        int count = 0;
        for (Number answer : mathQuestion.getAnswers()) {
            addAnswerButton(count, answer, mathQuestion.getCorrectAnswer());
            ++count;
        }

        if (null != questionTimer && gameStarted) {
            questionTimer.start();
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
                // TODO Show a neat effect
                if (null != questionTimer) {
                    questionTimer.cancel();
                }
                provideAnswer(isCorrect);
            }
        });
        LinearLayout ll = findViewById(R.id.linear_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(answerButton, lp);
    }

    protected void checkForDeath() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (0 == livesRemaining) {
            System.out.println("I AM DEAD");
            showResult();
        }
    }

    protected void loseLife() {
        livesRemaining = livesRemaining > 0 ? livesRemaining - 1 : 0;

        System.out.println("lose life " + livesRemaining);

        ImageView iv = lifeCollection.get(livesRemaining);
        iv.setVisibility(View.INVISIBLE);
        checkForDeath();
    }

    protected void gainLife() {
        if (livesRemaining >= startingLives) {
            return;
        }
        ++livesRemaining;

        System.out.println("gain life " + livesRemaining);

        ImageView iv = lifeCollection.get(livesRemaining - 1);
        iv.setVisibility(View.VISIBLE);
    }

    protected void checkHighScore() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        System.out.println("Score: " + score + " High Score: " + highScore);
        if (score > highScore) {
            highScore = score;
            if (!highScoreBreached && highScore > 10) {
                Toast toast = Toast.makeText(getApplicationContext(), "New high score!", Toast.LENGTH_SHORT);
                toast.show();
            }
            highScoreBreached = true;
            updateHighScore();
        }
    }

    protected void updateHighScore() {
        SharedPreferences.Editor editor = sharedPref.edit();
        String highScoreKey;
        if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
            highScoreKey = getString(R.string.high_score_time_trial_key);
        } else {
            highScoreKey = getString(R.string.high_score_survival_key);
        }
        editor.putInt(highScoreKey, highScore);
        editor.apply();
        System.out.println("update high score to " + highScore);
    }

    protected void provideAnswer(boolean isCorrect) {
        if (isCorrect) {
            increaseScore();
        } else {
            loseLife();
        }
        if (livesRemaining > 0 || GameConfig.GAME_MODE_PRACTICE == gameMode) {
            createQuestion();
        }
    }

    protected void advanceDifficulty() {
        System.out.println("advance difficulty");
        int newDifficulty;

        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            System.out.println("practice mode");
            newDifficulty = difficulty;
        } else {
            newDifficulty = LevelManager.getNextLevel(difficulty, score);
        }

        if (newDifficulty > difficulty) {
            difficulty = newDifficulty;
            gainLife();
            soundManager.playLevelUpSound(GameActivity.this);
        } else {
            System.out.println("play sound");
            soundManager.playCorrectAnswerSound(GameActivity.this);
        }
    }

    protected void handleAchievements() {
        // TODO Implement achievements
    }

    protected void increaseScore() {
        ++score;
        advanceDifficulty();
        checkHighScore();
        // Update score in UI
        TextView textView = findViewById(R.id.text_score);
        textView.setText(String.format(Locale.US,"Score: %d", score));
    }

    @Override
    public void onBackPressed() { }
}
