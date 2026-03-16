package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles view configuration requests, allowing the user to restrict which
 * columns are displayed, and set the page size. Forwards to DataServlet to
 * render the updated view.
 */
@WebServlet({ "/view" })
public class SetViewServlet extends BaseServlet {

  /**
   * Handles the GET request for view configuration. If 'columns' parameters are
   * present, restricts the view to those columns. If 'pageSize' is present,
   * updates
   * the page size accordingly.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      Model model = ModelFactory.getModel();
      request.setAttribute("pageMode", "setView");
      request.setAttribute("keyList", model.getDf().getColumnNames());
      String[] viewColumnsArray = request.getParameterValues("columns");
      if (viewColumnsArray != null) {
        List<String> viewColumns = new ArrayList<>(List.of(viewColumnsArray));
        model.refreshView();
        model.restrictViewColumns(viewColumns);
      }

      String pageSizeParameter = request.getParameter("pageSize");
      if (pageSizeParameter != null && !pageSizeParameter.isEmpty()) {
        try {
          int newPageSize = Integer.parseInt(pageSizeParameter);
          request.setAttribute("pageSize", newPageSize);
          model.getPagedView().setPageSize(newPageSize);
          model.updatePaging();
        } catch (NumberFormatException e) {
          // Ignore invalid page parameter; keep the current page
        }
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Set View failed: " + e.getMessage());
    }
  }
}