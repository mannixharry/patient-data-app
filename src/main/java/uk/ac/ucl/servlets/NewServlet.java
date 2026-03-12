package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.TableData;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

@WebServlet({ "/new" })
public class NewServlet extends BaseServlet {
  // NewServlet manages 'new' actions
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();

      request.setAttribute("pageMode", "edit");
      request.setAttribute("isNew", true);

      // Attach the key to the request
      request.setAttribute("key", model.getKeyName());

      TableData table = TableData.fromView(model.getView());
      request.setAttribute("table", table);
      forward(request, response, "/new.jsp");

    } catch (IllegalArgumentException e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      String action = request.getParameter("action");
      Model model = ModelFactory.getModel();

      if ("new".equals(action)) {

        List<String> names = model.getColumnNames();
        List<String> values = new ArrayList<>(names.size());

        for (String name : names) {
          values.add(request.getParameter(name));
        }

        String ID = request.getParameter(model.getKeyName());
        if (model.getKeyName() != null) {
          if (ID == null || ID.isEmpty()) {
            request.setAttribute("retryMessage", "ID field cannot be empty");
            request.setAttribute("retryValues", values);
            doGet(request, response);
            return; 
          } else if (model.getRowIndexInDfByValue(model.getKeyName(), ID) != -1) {
            request.setAttribute("retryMessage", "ID already in use");
            request.setAttribute("retryValues", values);
            doGet(request, response);
            return;
          }
        }
        model.addRow(values);
        model.refreshView();
        response.sendRedirect(request.getContextPath() + "/patient?row=" + model.getLastRowIndex());
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