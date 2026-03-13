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

@WebServlet({ "/view" })
public class SetViewServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Flag to tell JSPs to display (current) table + import input bar
      request.setAttribute("pageMode", "setView");

      Model model = ModelFactory.getModel();

      request.setAttribute("keyList", model.getColumnNames());

      String[] viewColumnsArray = request.getParameterValues("columns");
      if (viewColumnsArray != null) {
        List<String> viewColumns = viewColumnsArray == null ? new ArrayList<>()
            : new ArrayList<>(List.of(viewColumnsArray));
        model.refreshView();
        if (viewColumns != null) {
          model.restrictViewColumns(viewColumns);
        }
      }

      String pageSizeParameter = request.getParameter("pageSize");
      if (pageSizeParameter != null && !pageSizeParameter.isEmpty()) {
        int newPageSize = Integer.parseInt(pageSizeParameter);
        model.setPageSize(newPageSize);
      }
      // Dispatch request to DataServlet (so the new table is displayed)
      // DataServlet also attaches TableData so it is unnecessary here
      forward(request, response, "/data");

    } catch (Exception e) {
      forwardToError(request, response, "Set View failed" + e.getMessage());
    }
  }
}