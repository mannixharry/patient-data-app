package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import uk.ac.ucl.view.TableData;
import uk.ac.ucl.view.TableExporter;

@WebServlet({"/data"})
public class DataServlet extends HttpServlet {
  public DataServlet () {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try { 
      Model model = ModelFactory.getModel();
      //model.search("PREFIX", "Mr.", false);
      //model.search("BIRTHDATE","196.*", true);
      //model.sort("GENDER", true);
      //model.sort(model.getView().getColumnNames().get(0), true);
      
      TableData table = TableData.fromView(model.getView());
      TableExporter export = new TableExporter(table);
      export.toCSV("data/search_result.csv");

      request.setAttribute("table", table);
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/main.jsp");
      dispatch.forward(request, response);
    } catch (IOException e)
    {
      request.setAttribute("errorMessage", "Error loading data: " + e.getMessage());
      ServletContext context = this.getServletContext();
      RequestDispatcher dispatch = context.getRequestDispatcher("/error.jsp");
      dispatch.forward(request, response);
    }
  }
}