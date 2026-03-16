package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.ChartRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

/**
 * Handles chart display requests, using two modes:
 * - If no 'title' parameter is provided: forward to the charts dashboard page,
 * passing all chart titles (that reference generated chart PNGs).
 * - If a 'title' parameter is provided: render the named chart as a PNG image
 * (to be used as an image source by charts.jsp).
 */
@WebServlet({ "/charts" })
public class ChartServlet extends BaseServlet {

  /**
   * Handles GET requests for chart data.
   * @param request  the HTTP request, optionally with a 'title' parameter
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {

      // Retrieve the model and available chart data
      Model model = ModelFactory.getModel();
      Map<String, Map<String, Integer>> chartData = model.getChartData();

      String chartTitle = request.getParameter("title");
      if (chartTitle != null) {
        // Render the PNG image for the named chart
        Map<String, Integer> valueCounts = chartData.get(chartTitle);

        if (valueCounts == null) {
          throw new IllegalArgumentException("Chart not found");
        }

        ChartRenderer chartRenderer = new ChartRenderer();
        JFreeChart chart;

        if (model.getHistogramTitles().contains(chartTitle)) {
          chart = chartRenderer.createBarChart(chartTitle, valueCounts);
        } else {
          chart = chartRenderer.createPieChart(chartTitle, valueCounts);
        }

        // Stream the chart as a PNG 
        response.setContentType("image/png");
        try (OutputStream output = response.getOutputStream()) {
          ChartUtils.writeChartAsPNG(output, chart, 600, 400);
        }
      } else {
        // Forward chart titles to /chartsPage.jsp
        request.setAttribute("pageMode", "charts");
        request.setAttribute("chartTitles", chartData.keySet());
        forward(request, response, "/chartsPage.jsp");
      }
    } catch (Exception e) {
      forwardToError(request, response, "Chart display failed: " + e.getMessage());
    }
  }
}
