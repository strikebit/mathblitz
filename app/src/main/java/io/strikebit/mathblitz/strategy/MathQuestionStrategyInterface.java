package io.strikebit.mathblitz.strategy;

import io.strikebit.mathblitz.model.MathQuestion;

public interface MathQuestionStrategyInterface {
    MathQuestion generate(int difficulty);
}
