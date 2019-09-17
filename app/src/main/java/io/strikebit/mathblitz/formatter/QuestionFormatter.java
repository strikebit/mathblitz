package io.strikebit.mathblitz.formatter;

public class QuestionFormatter {
    /**
     * Get human-friendly question string.
     *
     * @param question String
     * @return String
     */
    public static String humanizeQuestion(String question) {
        question = question.replace('*', '×');
        question = question.replace('/', '÷');
        // question = question.replace('^', '^'); Handle this like so: Html.fromHtml("X<sup>2</sup>")

        return question;
    }
}
