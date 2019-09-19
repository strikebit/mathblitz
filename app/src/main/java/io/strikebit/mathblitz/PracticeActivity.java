package io.strikebit.mathblitz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;

public class PracticeActivity extends AppCompatActivity {

    private boolean doAdd = true;
    private boolean doSubtract = true;
    private boolean doMultiply = true;
    private boolean doDivide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        if (null != this.getSupportActionBar()) {
            this.getSupportActionBar().setTitle(getString(R.string.practice));
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LinearLayout ll1 = findViewById(R.id.practice_layout_1);
        LinearLayout ll2 = findViewById(R.id.practice_layout_2);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            ll1.setOrientation(LinearLayout.HORIZONTAL);
            ll2.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            ll1.setOrientation(LinearLayout.VERTICAL);
            ll2.setOrientation(LinearLayout.VERTICAL);
        }
    }

    public void onStartPracticeGameVeryEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_VERY_EASY);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onStartPracticeGameEasyClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_EASY);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onStartPracticeGameAdeptClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_ADEPT);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onStartPracticeGameHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_HARD);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onStartPracticeGameVeryHardClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_VERY_HARD);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onStartPracticeGameGeniusClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameMode", GameConfig.GAME_MODE_PRACTICE);
        intent.putExtra("difficulty", GameConfig.DIFFICULTY_LEGENDARY);
        intent.putStringArrayListExtra("operators", buildOperators());
        startActivity(intent);
    }

    public void onAddClick(View view) {
        ToggleButton tb = findViewById(R.id.button_practice_add);
        this.doAdd = tb.isChecked();
    }

    public void onSubstractClick(View view) {
        ToggleButton tb = findViewById(R.id.button_practice_subtract);
        this.doSubtract = tb.isChecked();
    }

    public void onMultiplyClick(View view) {
        ToggleButton tb = findViewById(R.id.button_practice_multiply);
        this.doMultiply = tb.isChecked();
    }

    public void onDivideClick(View view) {
        ToggleButton tb = findViewById(R.id.button_practice_divide);
        this.doDivide = tb.isChecked();
    }

    private ArrayList<String> buildOperators() {
        ArrayList<String> operators = new ArrayList<>();
        if (doAdd) {
            operators.add(MathQuestionStrategy.OPERATOR_PLUS);
        }
        if (doSubtract) {
            operators.add(MathQuestionStrategy.OPERATOR_MINUS);
        }
        if (doMultiply) {
            operators.add(MathQuestionStrategy.OPERATOR_MULTIPLY);
        }
        if (doDivide) {
            operators.add(MathQuestionStrategy.OPERATOR_DIVIDE);
        }

        System.out.println(operators);

        return operators;
    }
}
