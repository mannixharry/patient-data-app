package uk.ac.ucl.view;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.awt.Font;

/**
 * Renders styled JFreeChart pie and bar charts. Each chart is constructed from
 * a map of category name to integer counts.
 */
public class ChartRenderer {

  private static final Color[] COLOUR_PALETTE = {
      Color.decode("#bac4ce"), // blue-100
      Color.decode("#a5b3bf"), // blue-200
      Color.decode("#90a1b1"), // blue-300
      Color.decode("#7b8fa2"), // blue-400
      Color.decode("#677e92"), // blue-500
      Color.decode("#4d5e73"), // blue-600
      Color.decode("#304055") // blue-700
  };

  /**
   * Creates a pie chart from the given category counts.
   * @param chartTitle the title displayed above the chart
   * @param valueCounts a map of category names to their integer counts
   * @return pie chart ready for rendering
   */
  public JFreeChart createPieChart(String chartTitle, Map<String, Integer> valueCounts) {

    DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
    for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
      dataset.setValue(entry.getKey(), entry.getValue());
    }

    JFreeChart chart = ChartFactory.createPieChart(chartTitle, dataset);

    @SuppressWarnings("unchecked")
    PiePlot<String> plot = (PiePlot<String>) chart.getPlot();

    plot.setLabelPaint(COLOUR_PALETTE[6]);
    plot.setLabelBackgroundPaint(COLOUR_PALETTE[0]);
    chart.getTitle().setPaint(COLOUR_PALETTE[5]);

    int i = 0;
    for (Comparable<String> key : dataset.getKeys()) {
      plot.setSectionPaint(key, COLOUR_PALETTE[i % COLOUR_PALETTE.length]);
      i++;
    }

    return chart;
  }
    /**
   * Creates a bar chart from the given category counts.
   * @param chartTitle the title displayed above the chart
   * @param valueCounts a map of category names to their integer counts
   * @return bar chart ready for rendering
   */
  public JFreeChart createBarChart(String chartTitle, Map<String, Integer> valueCounts) {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Map.Entry<String, Integer> entry : valueCounts.entrySet()) {
      dataset.addValue(entry.getValue(), "Count", entry.getKey());
    }

    JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Category", "Count", dataset);

    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();

    renderer.setBarPainter(new StandardBarPainter());
    renderer.setShadowVisible(false);
    chart.getTitle().setPaint(COLOUR_PALETTE[5]);

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90); // 90° vertical
    domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 8));

    renderer.setSeriesPaint(0, COLOUR_PALETTE[6]);

    return chart;
  }
}
