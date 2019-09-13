package io.strikebit.mathblitz.strategy;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
    private Random random = new Random();

    public MathQuestion generate(int difficulty) {
        MathQuestion mathQuestion;
        switch (difficulty) {
            case DIFFICULTY_ADEPT:
                mathQuestion = generateHardQuestion();
                break;
            case DIFFICULTY_HARD:
                mathQuestion = generateAdeptQuestion();
                break;
            case DIFFICULTY_LEGENDARY:
                mathQuestion = generateLegendaryQuestion();
                break;
            case DIFFICULTY_EASY:
            default:
                mathQuestion = generateEasyQuestion();
                break;
        }

        return mathQuestion;
    }

    private MathQuestion generateEasyQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(11);
        int operand2 = random.nextInt(11);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Number> answers = generateAnswers(mathQuestion, 3);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateAdeptQuestion() {
        String operator1 = operators.get(random.nextInt(operators.size()));
        String operator2 = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(11);
        int operand2 = random.nextInt(11);
        int operand3 = random.nextInt(11);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator1)) {
            ++operand2;
        }
        if (0 == operand3 && OPERATOR_DIVIDE.equals(operator2)) {
            ++operand3;
        }

        String question = String.format(Locale.US, "%d %s %d %s %d", operand1, operator1, operand2, operator2, operand3);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Number> answers = generateAnswers(mathQuestion, 3);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateHardQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(13);
        int operand2 = random.nextInt(13);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Number> answers = generateAnswers(mathQuestion, 4);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateLegendaryQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(51);
        int operand2 = random.nextInt(51);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Number> answers = generateAnswers(mathQuestion, 4);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion buildMathQuestion(String question) {
        MathQuestion mathQuestion = new MathQuestion();
        mathQuestion.setQuestion(question);
        Number correctAnswer = calculateCorrectAnswer(question);
        mathQuestion.setCorrectAnswer(correctAnswer);

        return mathQuestion;
    }

    private List<Number> generateAnswers(MathQuestion mathQuestion, int howMany) {
        List<Number> answers = new ArrayList<>();
        answers.add(mathQuestion.getCorrectAnswer());
        while (answers.size() < howMany) {
            Number ia = calculateIncorrectAnswer(mathQuestion.getQuestion());
            if (!answers.contains(ia)) {
                answers.add(ia);
            }
        }
        Collections.shuffle(answers);

        return answers;
    }

    private Number calculateCorrectAnswer(String expressionStr) {
        Expression expression = new Expression(expressionStr);
        double answer = expression.calculate();
        int intVal = (int) answer;
        double diff = intVal - answer;

        if (diff < 0) {
            return answer;
        }

        return intVal;
    }

    private Number calculateIncorrectAnswer(String expressionStr) {
        // TODO Handle doubles
        int correctAnswer = calculateCorrectAnswer(expressionStr).intValue();
        return random.nextBoolean()
                ? random.nextInt(5) + correctAnswer
                : random.nextInt(5) - correctAnswer;
    }
}
