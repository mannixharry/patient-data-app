package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import java.io.IOException;

import uk.ac.ucl.view.TableData;
import uk.ac.ucl.model.DataFrame;
import uk.ac.ucl.model.DataFrameView;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

/**
 * Handles viewing and editing a single row, identified by the 'row' parameter
 * (the row index in the current model's {@link DataFrameView} view object - not
 * the underlying {@link DataFrame}).
 * GET: loads the edit form for the given row.
 * POST: applies an 'update' or 'delete' action to the specified row.
 */
@WebServlet({ "/edit" })
public class EditServlet extends BaseServlet {
  /**
   * Loads the edit form for a single row.
   * 
   * @param request  the HTTP request. Must contain a 'row' parameter specifying
   *                 the row index in the model's current view
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();
      request.setAttribute("pageMode", "edit");
      request.setAttribute("page", model.getPagedView().getCurrentPage() + 1);
      request.setAttribute("pageSize", model.getPagedView().getPageSize());
      request.setAttribute("pageTotal", model.getPagedView().getTotalPages());

      int row = Integer.parseInt(request.getParameter("row"));
      DataFrameView singleView = model.getFullView().restrictToRow(row);
      TableData table = TableData.fromView(singleView);

      request.setAttribute("table", table);
      request.setAttribute("row", row);
      request.setAttribute("key", model.getKeyName());
      // The same JSP handles 'edit' and 'new' commands requests - isNew is a flag for this
      request.setAttribute("isNew", false);
      forward(request, response, "edit.jsp");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error loading row: " + e.getMessage());
    }
  }

  /**
   * Applies an 'update' or 'delete' action to a single patient row. The action is
   * specified by the 'action' parameter, and the target row by the 'row'
   * parameter (index in the model's current view).
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();

      String action = request.getParameter("action");
      int row = Integer.parseInt(request.getParameter("row"));

      if ("update".equals(action)) {
        List<String> values = model.getFullView().getColumnNames().stream().map(request::getParameter).toList();
        model.setRowThroughView(row, values);
        response.sendRedirect(request.getContextPath() + "/edit?row=" + row);
      } else if ("delete".equals(action)) {
        model.deleteRowThroughView(row);
        response.sendRedirect(request.getContextPath() + "/main?noColumnRefresh=true");
      } else {
        throw new IllegalArgumentException("Unknown action: " + action);
      }
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error modifying row: " + e.getMessage());
    }
  }
}