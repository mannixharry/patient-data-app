package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.DataFrame;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.TableData;

import java.util.List;

import java.io.IOException;

/**
 * Handles the creation of new rows (ie patient records).
 * GET loads the 'new' form.
 * POST validates and inserts the new row into the model's {@link DataFrame} and
 * redirects to the new row's edit page.
 */
@WebServlet({ "/new" })
public class NewServlet extends BaseServlet {

  /**
   * Loads the 'new' form.
   * 
   * @param request  the HTTP request, optionally with 'retryMessage' and
   *                 'retryValues' attributes set, which allow input data to be
   *                 saved when validation fails
   * @param response the HTTP response
   * @throws IOException if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();

      request.setAttribute("pageMode", "edit");
      request.setAttribute("isNew", true);
      request.setAttribute("key", model.getKeyName());

      TableData table = TableData.fromView(model.getFullView());
      request.setAttribute("table", table);
      forward(request, response, "/new.jsp");

    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error loading data: " + e.getMessage());
    }
  }

  /**
   * Validates and adds a new row of data (ie a patient record) to the model's
   * data.
   * If validation fails, re-displays the form with an error message.
   * If validation passes, redirects the client to the edit page for the newly
   * generated row.
   * 
   * @param request  The HTTP request. Must contain 'action' as well as a value
   *                 for each column name in the model's {@link DataFrame}
   * @param response the HTTP response
   * @throws IOException      if forwarding or redirecting fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();
      String action = request.getParameter("action");
      if ("new".equals(action)) {
        List<String> values = model.getDf().getColumnNames().stream().map(request::getParameter).toList();

        String ID = request.getParameter(model.getKeyName());
        if (model.getKeyName() != null) {
          if (ID == null || ID.isEmpty()) {
            request.setAttribute("retryMessage", "ID field cannot be empty");
            request.setAttribute("retryValues", values);
            doGet(request, response);
            return;
          } else if (model.getDf().getRowIndexByValue(model.getKeyName(), ID) != -1) {
            request.setAttribute("retryMessage", "ID already in use");
            request.setAttribute("retryValues", values);
            doGet(request, response);
            return;
          }
        }
        model.getDf().addRowByValues(values);
        int newRowIndex = model.getDf().getRowCount() - 1;
        model.refreshRowView();
        response.sendRedirect(request.getContextPath() + "/edit?row=" + newRowIndex);
      } else {
        throw new IllegalArgumentException("Unknown action: " + action);
      }
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error creating a new row: " + e.getMessage());
    }
  }
}