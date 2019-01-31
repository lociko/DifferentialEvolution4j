package com.svv;

import java.util.Comparator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * @author Vasyl Spachynskyi
 * @since 1/30/19
 */
public class DifferentialEvolution {

    private static double mutationFactor = 0.8;
    private double crossPoint = 0.7;

    public double[] de(int popsize, double[] boundaries, Function<double[], Double> function, int its) {
        double population[][] = init(popsize, boundaries, 6);

        List<PopulationVector> evaluated = eval(population, function);
        PopulationVector best = evaluated.stream().min(Comparator.comparing(PopulationVector::getValue)).get();

        for (int i = 0; i < its; i++) {
            for (int j = 0; j < popsize; j++) {
                int[] indx = randomSelect(popsize, j);
                double[] mutant = mutationVector(population, indx);
                double[] trialVector = recombination(population[j], mutant, boundaries);

                Double trialValue = function.apply(trialVector);
                if (trialValue < evaluated.get(j).value) {
                    evaluated.set(j, new PopulationVector(trialValue, trialVector));
                    population[j] = trialVector;

                    if (trialValue < best.value) {
                        System.out.println(trialValue);
                        best = new PopulationVector(trialValue, trialVector);
                    }
                }
            }
        }

        return best.pop;
    }

    private double[] recombination(double[] originalVector, double[] mutant, double[] boundaries) {
        double[] trialVector = new double[originalVector.length];
        PrimitiveIterator.OfDouble rand = new Random()
                .doubles(0, 1).iterator();

        for (int i = 0; i < originalVector.length; i++) {
            if (rand.next() < crossPoint) {
                trialVector[i] = /*boundaries[0] + */mutant[i]/* * (boundaries[1] - boundaries[0])*/;
            } else {
                trialVector[i] = originalVector[i];
            }
        }

        return trialVector;
    }

    private double[] mutationVector(double population[][], int[] indx) {
        int vectorsize = population[0].length;

        double[] a = population[indx[0]];
        double[] b = population[indx[1]];
        double[] c = population[indx[2]];

        double[] res = new double[vectorsize];
        for (int i = 0; i < vectorsize; i++) {
            res[i] = a[i] + mutationFactor * (b[i] - c[i]);
        }

        PrimitiveIterator.OfDouble rand = new Random().doubles(-3, 3).iterator();
        double[] normalized = DoubleStream.of(res)
                .map(d -> {
                    if (d < -3 || d > 3)  {
                        return rand.next();
                    } else {
                        return d;
                    }
                }).toArray();

        return normalized;
    }

    private List<PopulationVector> eval(double[][] population, Function<double[], Double> function) {
        return Stream.of(population)
                .map(pop -> new PopulationVector(function.apply(pop), pop))
                .collect(Collectors.toList());
    }

    private double[][] init(int popsize, double[] boundaries, int parameters) {
        return Stream.generate(() -> new Random()
                .doubles(boundaries[0], boundaries[1])
                .limit(parameters)
                .toArray())
                .limit(popsize)
                .toArray(double[][]::new);
    }

    private int[] randomSelect(int popSize, int currentIndex) {
        return new Random().ints(0, popSize)
                .filter(i -> i != currentIndex)
                .limit(3)
                .toArray();
    }

    class PopulationVector {

        final Double value;
        final double[] pop;

        public PopulationVector(Double value, double[] pop) {

            this.value = value;
            this.pop = pop;
        }

        public Double getValue() {
            return value;
        }

        public double[] getPop() {
            return pop;
        }
    }
}
