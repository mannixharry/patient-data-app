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

@WebServlet({ "/charts" })
public class ChartServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {

      request.setAttribute("pageMode", "charts");

      Model model = ModelFactory.getModel();
      Map<String, Map<String, Integer>> chartData = model.getChartData();

      String chartTitle = request.getParameter("title");
      if (chartTitle != null) {
        Map<String, Integer> valueCounts = chartData.get(chartTitle);

        if (valueCounts == null) {
          throw new IllegalArgumentException("Chart not found");
        }

        ChartRenderer chartRenderer = new ChartRenderer();
        JFreeChart chart;
        if (chartTitle.contains("Histogram")) {
          chart = chartRenderer.createBarChart(chartTitle, valueCounts);
        } else {
          chart = chartRenderer.createPieChart(chartTitle, valueCounts);
        }
        response.setContentType("image/png");

        try (OutputStream out = response.getOutputStream()) {
          ChartUtils.writeChartAsPNG(out, chart, 600, 400);
        }
      } else {
        request.setAttribute("chartTitles", chartData.keySet());
        forward(request, response, "/chartsPage.jsp");
      }
    } catch (Exception e) {
      forwardToError(request, response, "Chart display failed" + e.getMessage());
    }
  }
}
