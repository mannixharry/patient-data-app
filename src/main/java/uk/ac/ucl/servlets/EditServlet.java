package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import uk.ac.ucl.view.TableData;
import uk.ac.ucl.model.DataFrameView;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet({ "/edit" })
public class EditServlet extends BaseServlet {
  // EditServlet manages 'update' and 'delete' actions
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    // Uniquely identifies the 'patient' (or generally a row in the database) using
    // 'row' parameter - the row number in the current model VIEW (important)

    try {
      // Flag to tell JSPs to display 'edit' page
      request.setAttribute("pageMode", "edit");

      Model model = ModelFactory.getModel();


      request.setAttribute("page", model.getCurrentPage()+1);
      request.setAttribute("pageSize", model.getPageSize());
      request.setAttribute("pageTotal", model.getTotalPages());

      // Retrieve row parameter
      // Row is the row number in the current view identified by row
      int row = Integer.parseInt(request.getParameter("row"));

      // Get the row number in the main DataFrame uniquely identified by row
      DataFrameView single_view = model.getView().restrictToRow(row);
      TableData table = TableData.fromView(single_view);
      request.setAttribute("table", table);

      // Attach the row number to the request
      request.setAttribute("row", row);

      // Attach the key to the request
      request.setAttribute("key", model.getKeyName());

      // The same JSP handles 'edit' and 'new' commands requests - isNew is a flag to
      // the JSP to specify the type of request. In this case, we submit 'false'
      request.setAttribute("isNew", false);
      // Pass the request onto the 'edit' JSP
      forward(request, response, "edit.jsp");
    } catch (IllegalArgumentException e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      // The type of action is specified by the 'action' parameter
      Model model = ModelFactory.getModel();

      String action = request.getParameter("action");
      Integer row = Integer.parseInt(request.getParameter("row"));

      if ("update".equals(action)) {
        List<String> names = model.getColumnNames();
        List<String> values = new ArrayList<>(names.size());

        for (String name : names) {
          values.add(request.getParameter(name));
        }

        model.setRowThroughView(row, values);

        response.sendRedirect(request.getContextPath() + "/edit?row=" + row);
      } else if ("delete".equals(action)) {
        model.deleteRowThroughView(row);
        response.sendRedirect("/main");
      } else {
        throw new IllegalArgumentException("Unkown action" + action);
      }
    } catch (IllegalArgumentException e) {
      forwardToError(request, response, "Error updating patient: " + e.getMessage());
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}