package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;

/**
 * Handles search requests, filtering the model's current view to rows matching
 * the given search term. If no search parameters are provided, it does not
 * modify the view. Forwards to DataServlet to
 * render the updated view.
 */
@WebServlet({ "/search" })
public class SearchServlet extends BaseServlet {

  /**
   * Handles the GET request for search. 
   * 
   * @param request the HTTP request, optionally with 'searchColumn' and 'searchTerm' parameters
   * @param response the HTTP response
   * @throws IOException if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "search");

      String searchColumn = request.getParameter("searchColumn");
      String searchTerm = request.getParameter("searchTerm");

      Model model = ModelFactory.getModel();

      if (searchColumn != null && !searchColumn.isEmpty()) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
          model.search(searchColumn, searchTerm);
        }
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Search failed: " + e.getMessage());
    }
  }
}