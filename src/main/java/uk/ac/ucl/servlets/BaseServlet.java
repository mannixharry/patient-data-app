package uk.ac.ucl.servlets;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
  protected void forward(HttpServletRequest request, HttpServletResponse response, String path)
      throws ServletException, IOException {
    request.getRequestDispatcher(path).forward(request, response);
  }

  protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
    response.sendRedirect(request.getContextPath() + path);
  }

  protected void forwardToError(HttpServletRequest request, HttpServletResponse response, String message)
      throws IOException, ServletException {
    // Dispatch error details to /error.jsp
    request.setAttribute("errorMessage", message);
    ServletContext context = this.getServletContext();
    RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
    dispatch.forward(request, response);
  }
}