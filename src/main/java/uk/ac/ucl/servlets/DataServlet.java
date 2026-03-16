package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import uk.ac.ucl.view.TableData;

/**
 * Populates request attributes with the current view of the model and forwards
 * to main.jsp. Also handles page navigation via the 'page' parameter.
 */
@WebServlet({ "/data" })
public class DataServlet extends BaseServlet {

  /**
   * Handles the GET request for data
   * @param request  the HTTP request, optionally with a 'page' query parameter
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Passes a JSP-friendly copy of the current model view to main.jsp for display

    if (request.getAttribute("pageMode") == null) {
      request.setAttribute("pageMode", "main");
    }

    try {
      Model model = ModelFactory.getModel();
      request.setAttribute("title", model.getPath());
      String pageParameter = request.getParameter("page");

      if (pageParameter != null && !pageParameter.isEmpty()) {
        try {
          // Model uses zero-based page indexing internally
          int page = Integer.parseInt(pageParameter);
          model.getPagedView().setCurrentPage(page - 1);
        } catch (NumberFormatException e) {
          // Ignore invalid page parameter; keep the current page
        }
      }

      TableData table = TableData.fromView(model.getPagedView().getCurrentPageView());
      request.setAttribute("table", table);
      request.setAttribute("key", model.getKeyName());
      // + 1 to convert back to one-based page indexing for the view
      request.setAttribute("page", model.getPagedView().getCurrentPage() + 1);
      request.setAttribute("pageSize", model.getPagedView().getPageSize());
      request.setAttribute("pageTotal", model.getPagedView().getTotalPages());
      request.setAttribute("rowTotal", model.getView().getRowCount());

      forward(request, response, "/main.jsp");

    } catch (Exception e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    }
  }
}