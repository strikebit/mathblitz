package io.strikebit.mathblitz.factory;

import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.strategy.MathQuestionStrategy;

public class QuestionFactory {
    public static MathQuestion generate(int difficulty) {
        MathQuestionStrategy mathQuestionStrategy = new MathQuestionStrategy();
        return mathQuestionStrategy.generate(difficulty);
    }
}
