package io.strikebit.mathblitz.strategy;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.strikebit.mathblitz.model.MathQuestion;

public class MathQuestionStrategy implements MathQuestionStrategyInterface {
    private final static String OPERATOR_PLUS = "+";
    private final static String OPERATOR_MINUS = "-";
    private final static String OPERATOR_MULTIPLY = "*";
    private final static String OPERATOR_DIVIDE = "/";
    private final static List<String> operators = Collections.unmodifiableList(Arrays.asList(
            OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_MULTIPLY, OPERATOR_DIVIDE));

    public final static int DIFFICULTY_EASY = 0;
    public final static int DIFFICULTY_ADEPT = 1;
    public final static int DIFFICULTY_HARD = 2;
    public final static int DIFFICULTY_LEGENDARY = 3;

    public MathQuestion generate(int difficulty) {
        MathQuestion mathQuestion;
        switch (difficulty) {
            case DIFFICULTY_EASY:
                mathQuestion = generateEasyQuestion();
                break;
            case DIFFICULTY_ADEPT:
                // TODO
                mathQuestion = generateEasyQuestion();
                break;
            case DIFFICULTY_HARD:
                // TODO
                mathQuestion = generateEasyQuestion();
                break;
            case DIFFICULTY_LEGENDARY:
                // TODO
                mathQuestion = generateEasyQuestion();
                break;
            default:
                mathQuestion = generateEasyQuestion();
                break;
        }

        return mathQuestion;
    }

    private MathQuestion generateEasyQuestion() {
        Random random = new Random();
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(11);
        int operand2 = random.nextInt(11);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }

        String question = operand1 + " " + operator + " " + operand2;

        MathQuestion mathQuestion = new MathQuestion();
        mathQuestion.setQuestion(question);
        Number correctAnswer = calculateCorrectAnswer(question);
        mathQuestion.setCorrectAnswer(correctAnswer);

        List<Number> answers = new ArrayList<>();
        answers.add(correctAnswer);
        answers.add(3); // TODO FIXME

        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private Number calculateCorrectAnswer(String expressionStr) {
        Expression expression = new Expression(expressionStr);

        double answer = expression.calculate();
        int intVal = (int) answer;
        double diff = intVal - answer;
        System.out.println(answer);
        System.out.println(intVal);
        System.out.println(diff);

        if (diff < 0) {
            return answer;
        }

        return intVal;
    }
}
