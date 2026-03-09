package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import uk.ac.ucl.view.TableData;

import java.io.IOException;

/*import java.util.List;
import java.util.Map;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;*/

@WebServlet({"/patient"})
public class PatientServlet extends HttpServlet {
  public PatientServlet () {
  }

  public void forwardToError(HttpServletRequest request, HttpServletResponse response, String message) 
  throws IOException, ServletException {
    request.setAttribute("errorMessage", message);
    ServletContext context = this.getServletContext();
    RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
    dispatch.forward(request, response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try { 
      String patientID = request.getParameter("id");
      if (patientID == null || patientID.isEmpty()) {
        throw new IllegalArgumentException("Missing patient ID");
      }
      String exactIdRegex = "^" + Pattern.quote(patientID) + "$";
      
      Model model = ModelFactory.getModel();
      model.clearView();
      model.search("ID", exactIdRegex, true);
      TableData patientTable = TableData.fromView(model.getView());
      request.setAttribute("patientTable", patientTable);
      
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/patient.jsp");
      dispatch.forward(request, response);
    } catch (IllegalArgumentException e)
    {
      forwardToError(request, response, "Error loading data: " + e.getMessage());
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      String action = request.getParameter("action");
      String patientID = request.getParameter("ID");
      if (patientID == null || patientID.isEmpty()) {
        throw new IllegalArgumentException("Missing patient id");
      }
      Model model = ModelFactory.getModel();

      if ("update".equals(action)) {
        List<String> names = model.getColumnNames();
        List<String> values = new ArrayList<>(names.size());

        for (String name : names) {
          values.add(request.getParameter(name));
        }

        int rowIndex = model.getRowIndexByValue("ID", patientID);
        model.setRow(rowIndex, values);
        response.sendRedirect(request.getContextPath() + "/patient?id=" + patientID);
      } else if ("delete".equals(action)) {
        int row = model.getRowIndexByValue("ID", patientID); 
        if (row < 0) {
          throw new IllegalArgumentException("Patient not found");
        }
        model.deleteRow(row);
        response.sendRedirect("/main");
      }
    } catch (IllegalArgumentException e) {
      forwardToError(request, response, "Error updating patient: " + e.getMessage());
    } catch (Exception e) { 
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}