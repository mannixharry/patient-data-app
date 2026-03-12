package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.TableData;
import uk.ac.ucl.view.TableExporter;

@WebServlet({ "/save" })
public class SaveServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Flag to tell JSPs to display table + save information
      request.setAttribute("pageMode", "save");

      Model model = ModelFactory.getModel();
      // Overwrites the imported .csv file with current model data
      // Gets a full model view so that the entire database is saved
      TableData table = TableData.fromView(model.getFullView());
      TableExporter exporter = new TableExporter(table);
      Path path = model.getPathToCsv();
      exporter.toCSV(model.getPathToCsv());
      request.setAttribute("path", path);
    
      // Dispatch request to the DataServlet
      // 'table' and 'key' request attributes will be set by the DataServlet
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}