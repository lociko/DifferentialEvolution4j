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

    private final double mutationFactor = 0.8;
    private final double crossPoint = 0.7;

    private final int popsize;

    private double[] boundaries;
    private double[][] population;

    PrimitiveIterator.OfDouble crosspointRandomizer = new Random()
            .doubles(0, 1).iterator();

    PrimitiveIterator.OfDouble valueRandomizer;

    private DifferentialEvolution(double[] boundaries, int popSize) {
        this.boundaries = boundaries;
        this.popsize = popSize;

        valueRandomizer = new Random().doubles(boundaries[0], boundaries[1]).iterator();
    }

    public DifferentialEvolution(int popSize, double[] boundaries) {
        this(boundaries, popSize);
        this.population = init(popSize, 6);
    }

    public DifferentialEvolution(double[][] population, double[] boundaries) {
        this(boundaries, population.length);
        this.population = population;
    }

    public double[] de(Function<double[], Double> function, int its) {
        List<PopulationVector> evaluated = eval(population, function);

        PopulationVector best = evaluated.stream().min(Comparator.comparing(PopulationVector::getValue)).get();

        for (int i = 0; i < its; i++) {
            for (int j = 0; j < popsize; j++) {
                int[] randomIndx = randomSelect(popsize, j);

                double[] mutant = mutationVector(population, randomIndx);
                double[] trialVector = recombination(population[j], mutant, boundaries);

                Double trialValue = function.apply(trialVector);
                if (trialValue < evaluated.get(j).value) {
                    evaluated.set(j, new PopulationVector(trialValue, trialVector));
                    population[j] = trialVector;

                    if (trialValue < best.value) {
                        best = new PopulationVector(trialValue, trialVector);
                    }
                }
            }
        }

        return best.pop;
    }

    private double[] recombination(double[] originalVector, double[] mutant, double[] boundaries) {
        double[] trialVector = new double[originalVector.length];

        for (int i = 0; i < originalVector.length; i++) {
            if (crosspointRandomizer.next() < crossPoint) {
                trialVector[i] = mutant[i];
            } else {
                trialVector[i] = originalVector[i];
            }
        }

        return trialVector;
    }

    private double[] mutationVector(double[][] population, int[] indx) {
        int vectorSize = population[0].length;

        double[] a = population[indx[0]];
        double[] b = population[indx[1]];
        double[] c = population[indx[2]];

        double[] mutant = new double[vectorSize];

        for (int i = 0; i < vectorSize; i++) {
            mutant[i] = a[i] + mutationFactor * (b[i] - c[i]);
        }

        return normalization(mutant);
    }

    private double[] normalization(double[] mutant) {
        return DoubleStream.of(mutant)
                .map(d -> {
                    if (d < boundaries[0] || d > boundaries[1]) {
                        return valueRandomizer.next();
                    } else {
                        return d;
                    }
                }).toArray();
    }

    private List<PopulationVector> eval(double[][] population, Function<double[], Double> function) {
        return Stream.of(population)
                .map(pop -> new PopulationVector(function.apply(pop), pop))
                .collect(Collectors.toList());
    }

    private double[][] init(int popsize, int parameters) {
        return Stream.generate(() -> Stream.generate(() -> valueRandomizer.nextDouble()).limit(parameters).toArray())
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
    }
}
