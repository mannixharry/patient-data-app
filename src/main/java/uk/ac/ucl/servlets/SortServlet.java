package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.TableData;

import java.io.IOException;

@WebServlet({"/sort"})
public class SortServlet extends HttpServlet {
  public SortServlet () {
  }

  public void forwardToError(HttpServletRequest request, HttpServletResponse response, String message) 
  throws IOException, ServletException {
    request.setAttribute("errorMessage", message);
    ServletContext context = this.getServletContext();
    RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
    dispatch.forward(request, response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "sort");

      String sortColumn = request.getParameter("sortColumn");
      String ascending = request.getParameter("ascending");

      Model model = ModelFactory.getModel();

      if (sortColumn!= null && !sortColumn.isEmpty()) {
        if (ascending != null) {
          if ("true".equals(ascending)) {
            model.sort(sortColumn, true);
          } else if ("false".equals(ascending)) {
            model.sort(sortColumn, false);
          }
        }
      }

      TableData table = TableData.fromView(model.getView());
      request.setAttribute("table", table);

      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/data");
      dispatch.forward(request, response);
    } catch (Exception e) {
      forwardToError(request, response, "Sort failed" + e.getMessage());
    }
  }
}