package com.svv;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * @author Vasyl Spachynskyi
 * @since 1/30/19
 */
public class TestFunction {

    public static double[] polynomialCurveFunction(int limit, double[] params) {
        return DoubleStream.of(x(limit))
                .map(x -> polynomialCurve(x, DoubleStream.of(params).toArray()))
                .toArray();
    }

    public static double[] randomizedCos(int limit) {
        PrimitiveIterator.OfDouble r = new Random().doubles(-0.15, 0.15).iterator();

        return DoubleStream.of(cos(limit))
                .map(i -> i + r.next())
                .toArray();
    }

    public static double[] cos(int limit) {
        return DoubleStream.iterate(0, n -> n + 0.02)
                .map(Math::cos)
                .limit(limit)
                .toArray();
    }

    public static double[] x(int limit) {
        return DoubleStream.iterate(0, n -> n + 0.02).limit(limit).toArray();
    }

    private static double polynomialCurve(double x, double[] parameters) {
        return IntStream.range(0, parameters.length)
                .mapToDouble(i -> parameters[i] * Math.pow(x, i))
                .sum();
    }

}
