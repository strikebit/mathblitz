package io.strikebit.mathblitz.model;

import java.util.List;

public class MathQuestion {
    private String question;
    private List<Number> answers;
    private Number correctAnswer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Number> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Number> answers) {
        this.answers = answers;
    }

    public Number getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Number correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
