package io.strikebit.mathblitz.util;

import com.crashlytics.android.Crashlytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return 0;
        }

        try {
            BigDecimal bd = BigDecimal.valueOf(value); // Fatal error here
            bd = bd.setScale(places, RoundingMode.HALF_UP);

            return bd.doubleValue();
        } catch (Exception e) {
            Crashlytics.logException(e);
            return value;
        }
    }
}
