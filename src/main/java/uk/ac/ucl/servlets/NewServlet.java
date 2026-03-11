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
import uk.ac.ucl.view.TableData;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

@WebServlet({"/new"})
public class NewServlet extends HttpServlet {
  public NewServlet () {
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
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/patient.jsp");

      Model model = ModelFactory.getModel();
      model.emptyView();
      
      TableData patientTable = TableData.fromView(model.getView());
      request.setAttribute("patientTable", patientTable);
      request.setAttribute("isNew", true);
      request.setAttribute("key", model.getKeyName());

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
      Model model = ModelFactory.getModel();

      if ("add".equals(action)) {
        List<String> names = model.getColumnNames();
        List<String> values = new ArrayList<>(names.size());

        for (String name : names) {
          values.add(request.getParameter(name));
        }
        
        String patientID = values.get(model.getKeyIndex());

        if (patientID == null || patientID.isEmpty()) {
          request.setAttribute("retryFlag", true);
          request.setAttribute("retryValues", values);
          doGet(request, response);
        } else {
          model.addRow(values);
          response.sendRedirect(request.getContextPath() + "/patient?" + model.getKeyName() + "=" + patientID);
        }
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