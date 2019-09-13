package io.strikebit.mathblitz.util;

public class NumberUtil {
    public static boolean numberHasDecimal(Number number) {
        double d = number.doubleValue();
        int i = number.intValue();
        double diff = i - d;

        return diff < 0 || diff > 0;
    }
}
