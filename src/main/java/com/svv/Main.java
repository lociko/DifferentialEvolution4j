package com.svv;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import static com.svv.RootMeanSquareError.rmse;
import static com.svv.TestFunction.polynomialCurveFunction;
import static com.svv.TestFunction.x;

/**
 * @author Vasyl Spachynskyi
 * @since 1/15/19
 */
public class Main {

    public static void main(String[] args) {
        double[] cos = TestFunction.randomizedCos(500);

        Function<double[], Double> rmse = doubles ->
                rmse(cos, polynomialCurveFunction(500, doubles));

        double[][] pop = new double[10][];

        for (int i = 0; i < 10; i++) {
            double[] vector = new DifferentialEvolution(20, new double[]{-3, 3})
                    .de(rmse, 500);

            System.out.println(Arrays.toString(vector));

            pop[i] = vector;
        }

        double[] vector = new DifferentialEvolution(pop, new double[]{-3, 3})
                .de(rmse, 1000);

        System.out.println("Best:");
        System.out.println(Arrays.toString(vector));

        vector = DoubleStream.of(vector).map(d -> d * -1).toArray();

        XYChart chart = createChart();

        plotChart(chart, "Rand COS", x(500), cos);
        plotChart(chart, "COS", x(500), TestFunction.cos(500));
        plotChart(chart, "Polinom", x(500), polynomialCurveFunction(500, vector));

        new SwingWrapper<>(chart).displayChart();
    }

    private static void plotChart(XYChart chart, String name, double[] xData, double[] yData) {
        chart.addSeries(name, xData, yData);
    }

    private static XYChart createChart() {
        XYChart chart = new XYChartBuilder().width(800).height(600).build();

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
        chart.getStyler().setMarkerSize(5);
        return chart;
    }
}
