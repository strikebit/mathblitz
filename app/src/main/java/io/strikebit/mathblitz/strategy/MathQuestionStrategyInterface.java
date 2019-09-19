package io.strikebit.mathblitz.strategy;

import java.util.ArrayList;

import io.strikebit.mathblitz.model.MathQuestion;

public interface MathQuestionStrategyInterface {
    MathQuestion generate(int difficulty);
    MathQuestion generate(int difficulty, ArrayList<String> o);
}
