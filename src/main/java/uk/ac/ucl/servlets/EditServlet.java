package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import java.io.IOException;

import uk.ac.ucl.view.TableData;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

@WebServlet({ "/patient" })
public class EditServlet extends BaseServlet {
  public EditServlet() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();

      String patientID = request.getParameter(model.getKeyName());
      if (patientID == null || patientID.isEmpty()) {
        throw new IllegalArgumentException("Missing patient key");
      }
      String exactIdRegex = "^" + Pattern.quote(patientID) + "$";

      model.refreshView();
      model.search(model.getKeyName(), exactIdRegex, true);
      TableData patientTable = TableData.fromView(model.getView());
      request.setAttribute("patientTable", patientTable);
      request.setAttribute("isNew", false);
      request.setAttribute("key", model.getKeyName());
      request.setAttribute("pageMode", "edit");
      forward(request, response, "patient.jsp");
    } catch (IllegalArgumentException e) {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      Model model = ModelFactory.getModel();
      String action = request.getParameter("action");
      String patientID = request.getParameter(model.getKeyName());
      if (patientID == null || patientID.isEmpty()) {
        throw new IllegalArgumentException("Missing patient ID");
      }

      if ("update".equals(action)) {
        List<String> names = model.getColumnNames();
        List<String> values = new ArrayList<>(names.size());

        for (String name : names) {
          values.add(request.getParameter(name));
        }

        int rowIndex = model.getRowIndexInDfByValue(model.getKeyName(), patientID);
        model.setRow(rowIndex, values);
        response.sendRedirect(request.getContextPath() + "/patient?" + model.getKeyName() + "=" + patientID);
      } else if ("delete".equals(action)) {
        int row = model.getRowIndexInDfByValue(model.getKeyName(), patientID);
        if (row < 0) {
          throw new IllegalArgumentException("Patient not found");
        }
        model.deleteRow(row);
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