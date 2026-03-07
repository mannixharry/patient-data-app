package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*import java.util.List;
import java.util.Map;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;*/

@WebServlet({"/patient"})
public class PatientServlet extends HttpServlet {
  public PatientServlet () {
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try { 
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/data.jsp");
      dispatch.forward(request, response);
    } catch (IOException e)
    {
      request.setAttribute("errorMessage", "Error loading data: " + e.getMessage());
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
      dispatch.forward(request, response);
    }
  }
}