package io.strikebit.mathblitz.model;

import java.util.List;

public class MathQuestion {
    private String question;
    private List<Double> answers;
    private Double correctAnswer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Double> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Double> answers) {
        this.answers = answers;
    }

    public Double getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Double correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
