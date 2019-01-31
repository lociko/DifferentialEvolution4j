package com.svv;

import java.math.BigDecimal;
import java.util.stream.IntStream;

/**
 * @author Vasyl Spachynskyi
 * @since 1/30/19
 */
public class RootMeanSquareError {

    public static double rmse(double y[], double yPred[]) {
        assert yPred.length == y.length;

        BigDecimal sum = IntStream.range(0, y.length)
                .boxed()
                .map(i -> new BigDecimal(y[i] + yPred[i]).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Math.sqrt(sum.divide(BigDecimal.valueOf(y.length)).doubleValue());
    }
}
