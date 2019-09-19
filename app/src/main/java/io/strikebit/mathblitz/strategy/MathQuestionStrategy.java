package io.strikebit.mathblitz.strategy;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.strikebit.mathblitz.config.GameConfig;
import io.strikebit.mathblitz.model.MathQuestion;
import io.strikebit.mathblitz.util.NumberUtil;

public class MathQuestionStrategy implements MathQuestionStrategyInterface {
    public final static String OPERATOR_PLUS = "+";
    public final static String OPERATOR_MINUS = "-";
    public final static String OPERATOR_MULTIPLY = "*";
    public final static String OPERATOR_DIVIDE = "/";
    // private final static String OPERATOR_EXPONENT = "^";
    private List<String> operators = new ArrayList<>(Arrays.asList(
            OPERATOR_PLUS, OPERATOR_MINUS, OPERATOR_MULTIPLY, OPERATOR_DIVIDE));

    private Expression expression = new Expression();
    private Random random = new Random();

    public MathQuestion generate(int difficulty, ArrayList<String> o) {
        if (null != o && !o.isEmpty()) {
            this.operators = o;
        }

        MathQuestion mathQuestion;
        switch (difficulty) {
            case GameConfig.DIFFICULTY_VERY_EASY:
                mathQuestion = generateVeryEasyQuestion();
                break;
            case GameConfig.DIFFICULTY_ADEPT:
                mathQuestion = generateAdeptQuestion();
                break;
            case GameConfig.DIFFICULTY_HARD:
                mathQuestion = generateHardQuestion();
                break;
            case GameConfig.DIFFICULTY_VERY_HARD:
                mathQuestion = generateVeryHardQuestion();
                break;
            case GameConfig.DIFFICULTY_LEGENDARY:
                mathQuestion = generateLegendaryQuestion();
                break;
            case GameConfig.DIFFICULTY_EASY:
            default:
                mathQuestion = generateEasyQuestion();
                break;
        }

        return mathQuestion;
    }

    public MathQuestion generate(int difficulty) {
        MathQuestion mathQuestion;
        switch (difficulty) {
            case GameConfig.DIFFICULTY_VERY_EASY:
                mathQuestion = generateVeryEasyQuestion();
                break;
            case GameConfig.DIFFICULTY_ADEPT:
                mathQuestion = generateAdeptQuestion();
                break;
            case GameConfig.DIFFICULTY_HARD:
                mathQuestion = generateHardQuestion();
                break;
            case GameConfig.DIFFICULTY_VERY_HARD:
                mathQuestion = generateVeryHardQuestion();
                break;
            case GameConfig.DIFFICULTY_LEGENDARY:
                mathQuestion = generateLegendaryQuestion();
                break;
            case GameConfig.DIFFICULTY_EASY:
            default:
                mathQuestion = generateEasyQuestion();
                break;
        }

        return mathQuestion;
    }

    private MathQuestion generateVeryEasyQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(8);
        if (0 == operand1) {
            ++operand1;
        }
        int operand2 = random.nextInt(operand1);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 3);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateEasyQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(13);
        int operand2 = random.nextInt(13);

        /*
        if (OPERATOR_EXPONENT.equals(operator)) {
            operand2 = random.nextInt(3);
        }
         */
        // Make division easy initially
        if (OPERATOR_DIVIDE.equals(operator)) {
            operand1 = 0 == operand1 ? ++operand1 : operand1;
            List<Integer> divisors = NumberUtil.getDivisors(operand1);
            operand2 = divisors.get(random.nextInt(divisors.size()));
        }

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 3);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateAdeptQuestion() {
        String operator = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(21);
        int operand2 = random.nextInt(12);
        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator)) {
            ++operand2;
        }
        /*
        if (OPERATOR_EXPONENT.equals(operator)) {
            operand2 = random.nextInt(3);
        }
         */

        String question = String.format(Locale.US, "%d %s %d", operand1, operator, operand2);

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 4);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateHardQuestion() {
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
        boolean useParenthesis = random.nextBoolean();
        String question;
        if (useParenthesis) {
            if (random.nextBoolean()) {
                question = String.format(Locale.US, "(%d %s %d) %s %d", operand1, operator1, operand2, operator2, operand3);
            } else {
                question = String.format(Locale.US, "%d %s (%d %s %d)", operand1, operator1, operand2, operator2, operand3);
            }
        } else {
            question = String.format(Locale.US, "%d %s %d %s %d", operand1, operator1, operand2, operator2, operand3);
        }


        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 3);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateVeryHardQuestion() {
        String operator1 = operators.get(random.nextInt(operators.size()));
        String operator2 = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(31);
        int operand2 = random.nextInt(31);
        int operand3 = random.nextInt(31);

        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator1)) {
            ++operand2;
        }
        if (0 == operand3 && OPERATOR_DIVIDE.equals(operator2)) {
            ++operand3;
        }
        /*
        if (OPERATOR_EXPONENT.equals(operator1)) {
            operand2 = random.nextInt(3);
        }
        if (OPERATOR_EXPONENT.equals(operator2)) {
            operand3 = random.nextInt(3);
        }
         */

        boolean useParenthesis = random.nextBoolean();
        String question;
        if (useParenthesis) {
            if (random.nextBoolean()) {
                question = String.format(Locale.US, "(%d %s %d) %s %d", operand1, operator1, operand2, operator2, operand3);
            } else {
                question = String.format(Locale.US, "%d %s (%d %s %d)", operand1, operator1, operand2, operator2, operand3);
            }
        } else {
            question = String.format(Locale.US, "%d %s %d %s %d", operand1, operator1, operand2, operator2, operand3);
        }

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 4);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion generateLegendaryQuestion() {
        String operator1 = operators.get(random.nextInt(operators.size()));
        String operator2 = operators.get(random.nextInt(operators.size()));
        int operand1 = random.nextInt(51);
        int operand2 = random.nextInt(51);
        int operand3 = random.nextInt(51);

        if (0 == operand2 && OPERATOR_DIVIDE.equals(operator1)) {
            ++operand2;
        }
        if (0 == operand3 && OPERATOR_DIVIDE.equals(operator2)) {
            ++operand3;
        }
        /*
        if (OPERATOR_EXPONENT.equals(operator1)) {
            operand2 = random.nextInt(3);
        }
        if (OPERATOR_EXPONENT.equals(operator2)) {
            operand3 = random.nextInt(3);
        }
         */

        boolean useParenthesis = random.nextBoolean();
        String question;
        if (useParenthesis) {
            if (random.nextBoolean()) {
                question = String.format(Locale.US, "(%d %s %d) %s %d", operand1, operator1, operand2, operator2, operand3);
            } else {
                question = String.format(Locale.US, "%d %s (%d %s %d)", operand1, operator1, operand2, operator2, operand3);
            }
        } else {
            question = String.format(Locale.US, "%d %s %d %s %d", operand1, operator1, operand2, operator2, operand3);
        }

        MathQuestion mathQuestion = buildMathQuestion(question);
        List<Double> answers = generateAnswers(mathQuestion, 4);
        mathQuestion.setAnswers(answers);

        return mathQuestion;
    }

    private MathQuestion buildMathQuestion(String question) {
        MathQuestion mathQuestion = new MathQuestion();
        mathQuestion.setQuestion(question);
        Double correctAnswer = calculateCorrectAnswer(question);
        mathQuestion.setCorrectAnswer(correctAnswer);

        return mathQuestion;
    }

    private List<Double> generateAnswers(MathQuestion mathQuestion, int howMany) {
        List<Double> answers = new ArrayList<>();
        answers.add(mathQuestion.getCorrectAnswer());
        while (answers.size() < howMany) {
            Double ia = calculateIncorrectAnswer(mathQuestion.getQuestion());
            if (!this.containsDouble(answers, ia)) {
                answers.add(ia);
            }
        }
        Collections.shuffle(answers);

        return answers;
    }

    private Double calculateCorrectAnswer(String expressionStr) {
        expression.setExpressionString(expressionStr);

        return expression.calculate();
    }

    private Double calculateIncorrectAnswer(String expressionStr) {
        Double correctAnswer = calculateCorrectAnswer(expressionStr);

        if (NumberUtil.isWholeNumber(correctAnswer)) {
            int intVal = random.nextBoolean()
                    ? random.nextInt(5) + correctAnswer.intValue()
                    : random.nextInt(5) - correctAnswer.intValue();

            return (double) intVal;
        }

        return random.nextBoolean()
                ? random.nextDouble() + correctAnswer
                : random.nextDouble() - correctAnswer;
    }

    private boolean containsDouble(List<Double> list, Double input) {
        double fixedInput = NumberUtil.round(input, 2);
        for (Double d : list) {
            double fixedD = NumberUtil.round(d, 2);
            if (fixedInput == fixedD) {
                return true;
            }
        }

        return false;
    }
}
