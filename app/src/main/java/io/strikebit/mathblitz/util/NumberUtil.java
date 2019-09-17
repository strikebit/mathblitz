package io.strikebit.mathblitz.util;

import java.util.ArrayList;
import java.util.List;

public class NumberUtil {
    public static boolean isWholeNumber(Double d) {
        return 0 == d % 1;
    }

    public static boolean isPrime(int number) {
        boolean flag = false;
        for (int i = 2; i <= number / 2; ++i) {
            if (0 == number % i) {
                flag = true;
                break;
            }
        }

        return !flag;
    }

    public static List<Integer> getDivisors(int number) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= number; ++i) {
            if (0 == number % i) {
                list.add(i);
            }
        }

        return list;
    }
}
