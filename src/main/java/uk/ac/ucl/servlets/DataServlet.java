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
  public DataServlet() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Passes a JSP-friendly copy of the current model view to main.jsp for display
    try {
      Model model = ModelFactory.getModel();
      // Convert current view of Model to JSP-friendly (readonly) format
      TableData table = TableData.fromView(model.getView());
      // Set 'table' and 'key' attributes.
      request.setAttribute("table", table);
      request.setAttribute("key", model.getKeyName());
      
      // Forward to main.jsp for display
      forward(request, response, "/main.jsp");

    } catch (IOException e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    }
  }
}