package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

/**
 * Entry point servlet that refreshes the model view and forwards to
 * DataServlet. Has two optional parameters, 'noRefresh' and 'noColumnRefresh'
 * to preserve view state across web-site navigations.
 * - 'noRefresh': skips the model's {@link DataFrameView} refresh entirely, preserving the view state.
 * - 'noColumnRefresh': refreshes the view but preserves the current selection of viewed columns.
 */
@WebServlet({ "/main" })
public class MainServlet extends BaseServlet {

  /**
   * Handles the GET request for the main page. 
   * 
   * @param request the HTTP request, optionally with 'noRefresh' or 'noColumnRefresh' parameters
   * @param response the HTTP response
   * @throws IOException if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "main");

      Model model = ModelFactory.getModel();

      String noRefreshParameter = request.getParameter("noRefresh");
      String noColumnRefreshParameter = request.getParameter("noColumnRefresh");

      boolean noRefresh = "true".equals(noRefreshParameter);
      boolean noColumnRefresh = "true".equals(noColumnRefreshParameter);

      if (!noRefresh && !noColumnRefresh) {
        model.refreshView();
      } else if (!noRefresh) {
        model.refreshRowView();
      }

      redirect(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}