package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;

/**
 * Handles sort requests, reordering the model's view by a given column.
 * Forwards to DataServlet to render the updated view. If no sort parameters are
 * provided, forwards without changing the view.
 */
@WebServlet({ "/sort" })
public class SortServlet extends BaseServlet {

  /**
   * Handles the GET request for sorting.
   * 
   * @param request  the HTTP request, optionally with 'sortColumn' and
   *                 'ascending' (true/false) parameters
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   * 
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "sort");

      String sortColumn = request.getParameter("sortColumn");
      String ascending = request.getParameter("ascending");

      Model model = ModelFactory.getModel();

      if (sortColumn != null && !sortColumn.isEmpty() && ascending != null) {
        if ("true".equals(ascending)) {
          model.sort(sortColumn, true);
        } else if ("false".equals(ascending)) {
          model.sort(sortColumn, false);
        }
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Sort failed: " + e.getMessage());
    }
  }
}