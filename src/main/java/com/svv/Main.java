package com.svv;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static com.svv.RootMeanSquareError.rmse;
import static com.svv.TestFunction.polynomialCurveFunction;
import static com.svv.TestFunction.x;

/**
 * @author Vasyl Spachynskyi
 * @since 1/15/19
 */
public class Main {

    public static void main(String[] args) throws IOException {
        double[] cos = TestFunction.randomizedCos(500);

        Function<double[], Double> rmse = doubles ->
                rmse(cos, polynomialCurveFunction(500, doubles));

        double[] vector = new DifferentialEvolution()
                .de(15, new double[]{-3, 3}, rmse, 1000);

        System.out.println(Arrays.toString(vector));

        XYChart chart = createChart();

        plotChart(chart, "Rand COS", x(500), cos);
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
