package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
    private int totalQuestionsAnswered;
    private int questionsAnswered = 0;
    private int totalQuestionsCorrect;
    private float fastestCorrectAnswer;
    private float newFastestTime = 60000.0f;
    private int mostCorrectInRow;
    private int mostCorrectInRowSession = 0;
    private boolean highScoreBreached = false;
    private boolean gameStarted = false;
    private float currentQuestionTime;

    private SharedPreferences sharedPref;
    private CountDownTimer countDownTimer;
    private CountDownTimer questionTimer;
    private ProgressBar progressBar;
    private SoundManager soundManager = new SoundManager();
    // TODO Make # of lives dynamic
    private List<ImageView> lifeCollection = new ArrayList<>();
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ll = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress_bar);
        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        score = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", GameConfig.DIFFICULTY_EASY);

        new Thread(new Runnable() {
            public void run() {
                sharedPref = getSharedPreferences(getString(R.string.game_shared_preferences), Context.MODE_PRIVATE);
                String highScoreKey;
                if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
                    highScoreKey = getString(R.string.high_score_time_trial_key);
                } else {
                    highScoreKey = getString(R.string.high_score_survival_key);
                }
                highScore = sharedPref.getInt(highScoreKey, 0);
                totalQuestionsAnswered = sharedPref.getInt(getString(R.string.total_questions_answered), 0);
                totalQuestionsCorrect = sharedPref.getInt(getString(R.string.total_questions_correct), 0);
                mostCorrectInRow = sharedPref.getInt(getString(R.string.most_correct_in_a_row), 0);
                fastestCorrectAnswer = sharedPref.getFloat(getString(R.string.fastest_correct_answer), 60000.0f);
            }
        }).start();

        if (GameConfig.GAME_MODE_PRACTICE != gameMode) {
            createQuestionTimer();
        } else {
            setupPracticeMode();
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
                    timerText.setText(String.format(Locale.US, "%d", mUntilFinished / 1000));
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            ll.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll.setOrientation(LinearLayout.VERTICAL);
        }
    }

    protected void setupPracticeMode() {
        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_life1).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_life2).setVisibility(View.INVISIBLE);
        findViewById(R.id.image_life3).setVisibility(View.INVISIBLE);
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
                currentQuestionTime = questionTime - (float) mUntilFinished;
                double i = (double) mUntilFinished;
                progressBar.setProgress((int) Math.round(((i / (double) questionTime) * 100)));
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
        intent.putExtra("highScore", highScore);
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

    protected void clearPreviousAnswers() {
        if (null == ll) {
            return;
        }
        if (ll.getChildCount() > 0) {
            ll.removeAllViews();
        }
    }

    protected void createQuestion() {
        MathQuestion mathQuestion = QuestionFactory.generate(difficulty);

        TextView questionText = findViewById(R.id.math_question);
        questionText.setText(mathQuestion.getQuestion());

        clearPreviousAnswers();

        int orientation = this.getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            ll.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll.setOrientation(LinearLayout.VERTICAL);
        }

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
        answerButton.setTextSize(30);
        answerButton.setTextColor(getResources().getColor(R.color.colorWhite));
        answerButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        answerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ++questionsAnswered;
                boolean isCorrect = possibleAnswer.equals(correctAnswer);

                if (isCorrect) {
                    if (currentQuestionTime > 0
                            && currentQuestionTime < newFastestTime
                            && currentQuestionTime < fastestCorrectAnswer) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        newFastestTime = currentQuestionTime;
                        editor.putFloat(getString(R.string.fastest_correct_answer), newFastestTime);
                        editor.apply();
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.new_fastest_answer, newFastestTime / 1000),
                                Toast.LENGTH_SHORT
                        );
                        toast.show();
                    }
                }

                // TODO Show a neat effect
                if (null != questionTimer) {
                    questionTimer.cancel();
                }
                provideAnswer(isCorrect);
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.setMargins(3, 3, 3, 3);
        ll.addView(answerButton, lp);
    }

    protected void checkForDeath() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (0 == livesRemaining) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.total_questions_answered), totalQuestionsAnswered + questionsAnswered);
            editor.putInt(getString(R.string.total_questions_correct), totalQuestionsCorrect + score);
            editor.apply();
            showResult();
        }
    }

    protected void loseLife() {
        livesRemaining = livesRemaining > 0 ? livesRemaining - 1 : 0;

        checkCorrectAnswersInaRow();
        mostCorrectInRowSession = 0;

        ImageView iv = lifeCollection.get(livesRemaining);
        iv.setVisibility(View.INVISIBLE);

        checkForDeath();
    }

    protected void gainLife() {
        if (livesRemaining >= startingLives) {
            return;
        }
        ++livesRemaining;

        ImageView iv = lifeCollection.get(livesRemaining - 1);
        iv.setVisibility(View.VISIBLE);
    }

    protected void checkHighScore() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (score > highScore) {
            highScore = score;
            if (!highScoreBreached && highScore > 10) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.new_high_score), Toast.LENGTH_SHORT);
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
    }

    protected void provideAnswer(boolean isCorrect) {
        if (isCorrect) {
            increaseScore();
        } else {
            loseLife();
            soundManager.playIncorrectAnswerSound(GameActivity.this);
        }
        if (livesRemaining > 0 || GameConfig.GAME_MODE_PRACTICE == gameMode) {
            createQuestion();
        }
    }

    protected void advanceDifficulty() {
        int newDifficulty;

        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            newDifficulty = difficulty;
        } else {
            newDifficulty = LevelManager.getNextLevel(difficulty, score);
        }

        if (newDifficulty > difficulty) {
            difficulty = newDifficulty;
            questionTime += 1500;
            createQuestionTimer();
            gainLife();
            soundManager.playLevelUpSound(GameActivity.this);
        } else {
            soundManager.playCorrectAnswerSound(GameActivity.this);
        }
    }

    protected void checkCorrectAnswersInaRow() {
        if (mostCorrectInRowSession > mostCorrectInRow) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.most_correct_in_a_row), mostCorrectInRowSession);
            editor.apply();
        }
    }

    protected void increaseScore() {
        ++score;
        ++mostCorrectInRowSession;
        advanceDifficulty();
        checkHighScore();
        // Update score in UI
        TextView textView = findViewById(R.id.text_score);
        textView.setText(getString(R.string.score_default, score));
    }

    @Override
    public void onBackPressed() { }
}
