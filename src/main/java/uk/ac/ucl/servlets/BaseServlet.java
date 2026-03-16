package uk.ac.ucl.servlets;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BaseServlet is an abstract base class for all the servlets in the
 * application.
 * Provides shared utility methods for redirecting, forwarding and error
 * handling.
 */
public abstract class BaseServlet extends HttpServlet {
  /**
   * Forwards the request to a JSP view (or another servlet)
   * 
   * @param request  the HTTP request object
   * @param response the HTTP response object
   * @param path     the path to the JSP or servlet . For example, "/main.jsp"
   * @throws IOException      if an I/O error happens when forwarding
   * @throws ServletException if no dispatcher if found for the given path (ie. if
   *                          the path to the JSP or servlet is wrong)
   */
  protected void forward(HttpServletRequest request, HttpServletResponse response, String path)
      throws ServletException, IOException {
    RequestDispatcher dispatcher = request.getRequestDispatcher(path);
    if (dispatcher == null) {
      throw new ServletException("No dispatcher found for path: " + path);
    }
    dispatcher.forward(request, response);
  }

  /**
   * Redirects the client to a servlet path (relative to the context path).
   * Use this instead of {@link #forward} whenever the destination is a servlet
   * not a JSP (unless passing on request attributes)
   * 
   * @param request  the HTTP request object
   * @param response the HTTP response object
   * @param path     the context-relative path to the servlet. For example,
   *                 "/main"
   * @throws IOException if an I/O error happens during the redirect
   */
  protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
    response.sendRedirect(request.getContextPath() + path);
  }

  /**
   * Forwards to the error page, displaying the given message to the user.
   * 
   * @param request  the HTTP request object
   * @param response the HTTP response object
   * @param message  the error message to display
   * 
   */
  protected void forwardToError(HttpServletRequest request, HttpServletResponse response, String message)
      throws IOException, ServletException {
    request.setAttribute("errorMessage", message);
    request.getRequestDispatcher("/error.jsp").forward(request, response);
  }
}