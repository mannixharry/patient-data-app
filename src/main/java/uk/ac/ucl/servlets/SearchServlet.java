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

@WebServlet({"/search"})
public class SearchServlet extends HttpServlet {
  public SearchServlet () {
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
      request.setAttribute("pageMode", "search");

      String searchColumn = request.getParameter("searchColumn");
      String searchTerm = request.getParameter("searchTerm");

      Model model = ModelFactory.getModel();

      if (searchColumn != null && !searchColumn.isEmpty()) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
          model.search(searchColumn, searchTerm, false);
        }
      }

      TableData table = TableData.fromView(model.getView());
      request.setAttribute("table", table);
      
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/data");
      dispatch.forward(request, response);
    } catch (Exception e) {
      forwardToError(request, response, "Search failed" + e.getMessage());
    }
  }
}