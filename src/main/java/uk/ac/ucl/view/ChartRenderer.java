package uk.ac.ucl.view;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.awt.Font;

public class ChartRenderer {

  Color[] colourPalette = {
      Color.decode("#bac4ce"), // blue-100
      Color.decode("#a5b3bf"), // blue-200
      Color.decode("#90a1b1"), // blue-300
      Color.decode("#7b8fa2"), // blue-400
      Color.decode("#677e92"), // blue-500
      Color.decode("#4d5e73"), // blue-600
      Color.decode("#304055") // blue-700
  };

  public JFreeChart createPieChart(String chartTitle, Map<String, Integer> valueCounts) {

    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    for (String value : valueCounts.keySet()) {
      dataset.setValue(value, valueCounts.get(value));
    }

    JFreeChart chart = ChartFactory.createPieChart(chartTitle, dataset);

    @SuppressWarnings("unchecked")
    PiePlot<String> plot = (PiePlot<String>) chart.getPlot();

    plot.setLabelPaint(colourPalette[6]);
    plot.setLabelBackgroundPaint(colourPalette[0]);
    chart.getTitle().setPaint(colourPalette[5]);

    int i = 0;
    for (Comparable<String> key : dataset.getKeys()) {
      plot.setSectionPaint(key, colourPalette[i % colourPalette.length]);
      i++;
    }

    return chart;
  }

  public JFreeChart createBarChart(String chartTitle, Map<String, Integer> valueCounts) {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (String value : valueCounts.keySet()) {
      dataset.addValue(valueCounts.get(value), "Count", value);
    }

    JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Category", "Count", dataset);

    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();

    renderer.setBarPainter(new BarRenderer().getBarPainter()); // resets painter
    renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
    renderer.setShadowVisible(false);
    chart.getTitle().setPaint(colourPalette[5]);

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90); // 90° vertical
    domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 8));

    renderer.setSeriesPaint(0, colourPalette[6]);

    return chart;
  }
}
