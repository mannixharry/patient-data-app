package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import uk.ac.ucl.view.TableData;

@WebServlet({ "/data" })
public class DataServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Passes a JSP-friendly copy of the current model view to main.jsp for display

    if (request.getAttribute("pageMode") == null) {
      request.setAttribute("pageMode", "main");
    }

    try {
      Model model = ModelFactory.getModel();
      request.setAttribute("title", model.getPathToCsv());
      String pageParameter = request.getParameter("page");

      if (pageParameter != null && !pageParameter.isEmpty()) {
        try {
          int page = Integer.parseInt(pageParameter);
          model.setCurrentPage(page-1);
        } catch (NumberFormatException e) {
          
        } // Ignore garbage input
      }

      // Convert current view of Model to JSP-friendly (readonly) format
      TableData table = TableData.fromView(model.getPagedView());
      // Set 'table' and 'key' attributes.

      request.setAttribute("table", table);
      request.setAttribute("key", model.getKeyName());

      request.setAttribute("page", model.getCurrentPage()+1);
      request.setAttribute("pageSize", model.getPageSize());
      request.setAttribute("pageTotal", model.getTotalPages());
      request.setAttribute("rowTotal", model.getView().getRowCount());
      // Forward to main.jsp for display
      forward(request, response, "/main.jsp");

    } catch (IOException e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    }
  }
}