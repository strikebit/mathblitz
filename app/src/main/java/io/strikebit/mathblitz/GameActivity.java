package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.formatter.QuestionFormatter;
import io.strikebit.mathblitz.level.LevelManager;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.network.NetworkTool;
import io.strikebit.mathblitz.sound.SoundManager;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;
import io.strikebit.mathblitz.strategy.MathQuestionStrategyInterface;
import io.strikebit.mathblitz.util.NumberUtil;

public class GameActivity extends AppCompatActivity {
    private final static int gameCountdownInterval = 1000;
    private final static int gameTime = GameConfig.TIME_TRIAL_DURATION_SECONDS * 1000;
    private final static DecimalFormat df2 = new DecimalFormat("#.##");
    private final static int startingLives = 3;

    private int gameMode;
    private int questionTime = 5000; // 5 seconds
    private int livesRemaining = 0;
    private int score;
    private int highScore;
    private int difficulty;
    private int totalQuestionsAnswered;
    private int questionsAnswered = 0;
    private int totalQuestionsCorrect;
    private float fastestCorrectAnswer;
    private float newFastestTime = 60000f;
    private int mostCorrectInRow;
    private int mostCorrectInRowSession = 0;
    private boolean highScoreBreached = false;
    private boolean gameStarted = false;
    private float currentQuestionTime;
    private boolean okToShowAd = false;

    private SharedPreferences sharedPref;
    private CountDownTimer countDownTimer;
    private CountDownTimer questionTimer;
    private ProgressBar progressBar;
    private SoundManager soundManager = new SoundManager();
    private LinearLayout ll;
    private InterstitialAd mInterstitialAd;
    private MathQuestionStrategyInterface mathQuestionStrategy;
    private ArrayList<String> operators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mathQuestionStrategy = new MathQuestionStrategy();

        ll = findViewById(R.id.linear_layout);
        progressBar = findViewById(R.id.progress_bar);

        gameMode = getIntent().getIntExtra("gameMode", GameConfig.GAME_MODE_TIME_TRIAL);
        score = getIntent().getIntExtra("score", 0);
        difficulty = getIntent().getIntExtra("difficulty", GameConfig.DIFFICULTY_EASY);
        operators = getIntent().getStringArrayListExtra("operators");

        if (null != this.getSupportActionBar()) {
            switch (gameMode) {
                case GameConfig.GAME_MODE_TIME_TRIAL:
                    this.getSupportActionBar().setTitle(getString(R.string.time_trial));
                    break;
                case GameConfig.GAME_MODE_SURVIVAL:
                    this.getSupportActionBar().setTitle(getString(R.string.survival));
                    break;
                default:
                    this.getSupportActionBar().setTitle(getString(R.string.practice));
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;
            }
        }

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

        if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
            countDownTimer = new CountDownTimer(gameTime, gameCountdownInterval) {
                public void onTick(long mUntilFinished) {
                    timerText.setText(String.format(Locale.US, "%d", mUntilFinished / 1000));
                }

                public void onFinish() {
                    TextView questionText = findViewById(R.id.math_question);
                    questionText.setVisibility(View.INVISIBLE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.total_questions_answered), totalQuestionsAnswered + questionsAnswered);
                    editor.putInt(getString(R.string.total_questions_correct), totalQuestionsCorrect + score);
                    editor.apply();
                    updateLeaderboard();

                    showResult();
                }
            };
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameStarted = true;
                gainLife(startingLives);
                ProgressBar startLoader = findViewById(R.id.start_loader);
                startLoader.setVisibility(View.INVISIBLE);
                ll.setVisibility(View.VISIBLE);
                if (GameConfig.GAME_MODE_PRACTICE != gameMode) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
                    timerText.setVisibility(View.VISIBLE);
                }
                TextView questionText = findViewById(R.id.math_question);
                questionText.setVisibility(View.VISIBLE);
                final Handler timerHandler = new Handler();
                timerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != countDownTimer) {
                            countDownTimer.start();
                        }
                        if (null != questionTimer) {
                            questionTimer.start();
                        }
                    }
                }, 1000);
            }
        }, 3000);

        // Ensure the player has played at least 30s before showing the interstitial ad when the game ends.
        final Handler adTimeoutHandler = new Handler();
        adTimeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                okToShowAd = true;
            }
        }, 30000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, PracticeActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        findViewById(R.id.life_layout).setVisibility(View.INVISIBLE);
    }

    protected void createQuestionTimer() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (null != questionTimer) {
            questionTimer.cancel();
            questionTimer = null;
        }

        questionTimer = new CountDownTimer(questionTime, 100) {
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

    protected void cleanScene() {
        findViewById(R.id.life_layout).setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.text_score).setVisibility(View.INVISIBLE);
        findViewById(R.id.text_time_remaining).setVisibility(View.INVISIBLE);
        findViewById(R.id.math_question).setVisibility(View.INVISIBLE);
    }

    protected void runAd() {
        if (!okToShowAd) {
            showResultView();
            return;
        }

        cleanScene();
        findViewById(R.id.start_loader).setVisibility(View.VISIBLE);

        MobileAds.initialize(this, getString(R.string.add_id_key));

        // TODO life ad unit key ca-app-pub-7297349899740519/6550225624
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Test ad
        // mInterstitialAd.setAdUnitId(getString(R.string.ad_interstitial)); // Production Ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                showResultView();
            }

            @Override
            public void onAdClosed() {
                showResultView();
            }
        });
    }

    protected void showResult() {
        clearPreviousAnswers();
        killTimers();
        runAd();
    }

    protected void showResultView() {
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

    @Override
    protected void onPause() {
        super.onPause();
        killTimers();
        finish();
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
        MathQuestion mathQuestion;
        if (null != operators && !operators.isEmpty()) {
            mathQuestion = mathQuestionStrategy.generate(difficulty, operators);
        } else {
            mathQuestion = mathQuestionStrategy.generate(difficulty);
        }

        TextView questionText = findViewById(R.id.math_question);
        questionText.setText(QuestionFormatter.humanizeQuestion(mathQuestion.getQuestion()));

        clearPreviousAnswers();

        int orientation = this.getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            ll.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll.setOrientation(LinearLayout.VERTICAL);
        }

        int count = 0;
        for (Double answer : mathQuestion.getAnswers()) {
            addAnswerButton(count, answer, mathQuestion.getCorrectAnswer());
            ++count;
        }

        if (null != questionTimer && gameStarted) {
            questionTimer.start();
        }
    }

    protected void addAnswerButton(int index, final Double possibleAnswer, final Double correctAnswer) {
        final Button answerButton = new Button(this);
        answerButton.setId(index);
        boolean isWhole = NumberUtil.isWholeNumber(possibleAnswer);
        if (isWhole) {
            answerButton.setText(String.valueOf(possibleAnswer.intValue()));
        } else {
            answerButton.setText(df2.format(possibleAnswer));
        }
        answerButton.setTextSize(30);
        answerButton.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        answerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        answerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ++questionsAnswered;
                boolean isCorrect = possibleAnswer.equals(correctAnswer);

                answerButton.setEnabled(false);

                if (isCorrect) {
                    answerButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                    if (currentQuestionTime > 0
                            && currentQuestionTime < newFastestTime
                            && currentQuestionTime < fastestCorrectAnswer
                            && GameConfig.GAME_MODE_PRACTICE != gameMode) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        newFastestTime = currentQuestionTime;
                        editor.putFloat(getString(R.string.fastest_correct_answer), newFastestTime);
                        editor.apply();
                    }
                    if (currentQuestionTime < GameConfig.ACHIEVEMENT_FAST_THINKER) {
                        handleAchievement(getString(R.string.achievement_fast_thinker));
                    }
                } else {
                    answerButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                }

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
            // TODO Refactor me
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.total_questions_answered), totalQuestionsAnswered + questionsAnswered);
            editor.putInt(getString(R.string.total_questions_correct), totalQuestionsCorrect + score);
            editor.apply();
            updateLeaderboard();
            int combinedScore = totalQuestionsCorrect + score;
            if (combinedScore >= GameConfig.ACHIEVMENT_HUNDRED_QUESTIONS_RIGHT && combinedScore < GameConfig.ACHIEVMENT_TWO_HUNDRED_QUESTIONS_RIGHT) {
                handleAchievement(getString(R.string.achievement_100_club));
            }
            if (combinedScore >= GameConfig.ACHIEVMENT_TWO_HUNDRED_QUESTIONS_RIGHT && combinedScore < GameConfig.ACHIEVMENT_FIVE_HUNDRED_QUESTIONS_RIGHT) {
                handleAchievement(getString(R.string.achievement_200_club));
            }
            if (combinedScore >= GameConfig.ACHIEVMENT_FIVE_HUNDRED_QUESTIONS_RIGHT) {
                handleAchievement(getString(R.string.achievement_500_club));
            }
            if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode && score >= GameConfig.ACHIEVEMENT_TIME_TRIAL_20_TOTAL) {
                handleAchievement(getString(R.string.achievement_time_trial_20_total));
            }
            if (GameConfig.GAME_MODE_SURVIVAL == gameMode && score >= GameConfig.ACHIEVEMENT_SURVIVOR) {
                handleAchievement(getString(R.string.achievement_survivor));
            }
            showResult();
        }
    }

    protected void updateLeaderboard() {
        if (score > 0
                && null != GoogleSignIn.getLastSignedInAccount(this)
                && NetworkTool.isNetworkAvailable(this)) {
            String leaderBoardId;
            if (GameConfig.GAME_MODE_TIME_TRIAL == gameMode) {
                leaderBoardId = getString(R.string.leaderboard_time_trial_id);
            } else {
                leaderBoardId = getString(R.string.leaderboard_survival_id);
            }

            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScore(leaderBoardId, score);
        }
    }

    protected void loseLife() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }
        livesRemaining = livesRemaining > 0 ? livesRemaining - 1 : 0;
        mostCorrectInRowSession = 0;

        LinearLayout ll = findViewById(R.id.life_layout);
        try {
            ll.removeViewAt(livesRemaining);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        checkForDeath();
    }

    protected void gainLife(int howMany) {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }
        livesRemaining = livesRemaining + howMany;

        LinearLayout ll = findViewById(R.id.life_layout);
        for (int i = 0; i < howMany; ++i) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.ic_heart);
            iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ll.addView(iv);
        }
    }

    protected void checkHighScore() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (score > highScore) {
            highScore = score;
            if (!highScoreBreached && highScore > 10) {
                Toast toast = Toast.makeText(this, getString(R.string.new_high_score), Toast.LENGTH_SHORT);
                toast.show();
            }
            highScoreBreached = true;
            updateHighScore();
        }
    }

    protected void updateHighScore() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

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
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createQuestion();
                }
            }, 100);
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
            // gainLife(1); May add this back later
            soundManager.playLevelUpSound(GameActivity.this);
        } else {
            soundManager.playCorrectAnswerSound(GameActivity.this);
        }
    }

    protected void handleAchievement(String achievementKey) {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode || !NetworkTool.isNetworkAvailable(this)) {
            return;
        }

        if (null != GoogleSignIn.getLastSignedInAccount(this)) {
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).unlock(achievementKey);
        }
    }

    protected void checkCorrectAnswersInaRow() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            return;
        }

        if (mostCorrectInRowSession == GameConfig.ACHIEVEMENT_10_ROW) {
            if (null != GoogleSignIn.getLastSignedInAccount(this)) {
                handleAchievement(getString(R.string.achievement_10_in_a_row));
            }
        }

        if (mostCorrectInRowSession == GameConfig.ACHIEVEMENT_20_ROW) {
            if (null != GoogleSignIn.getLastSignedInAccount(this)) {
                handleAchievement(getString(R.string.achievement_20_in_a_row));
            }
        }

        if (mostCorrectInRowSession == GameConfig.ACHIEVEMENT_30_ROW) {
            if (null != GoogleSignIn.getLastSignedInAccount(this)) {
                handleAchievement(getString(R.string.achievement_20_in_a_row));
            }
        }

        if (mostCorrectInRowSession > mostCorrectInRow) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.most_correct_in_a_row), mostCorrectInRowSession);
            editor.apply();
        }
    }

    protected void increaseScore() {
        ++score;
        ++mostCorrectInRowSession;
        checkCorrectAnswersInaRow();
        advanceDifficulty();
        checkHighScore();
        // Update score in UI
        TextView textView = findViewById(R.id.text_score);
        textView.setText(getString(R.string.score_default, score));
    }

    @Override
    public void onBackPressed() {
        if (GameConfig.GAME_MODE_PRACTICE == gameMode) {
            backToPracticeMenu();
        }
    }

    private void backToPracticeMenu() {
        Intent intent = new Intent(this, PracticeActivity.class);
        startActivity(intent);
    }
}
