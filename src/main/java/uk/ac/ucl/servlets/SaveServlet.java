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

@WebServlet({ "/saveFile" })
public class SaveServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Flag to tell JSPs to display table + save information
      request.setAttribute("pageMode", "save");

      String inputCofirmation = request.getParameter("confirmation");
      boolean hasConfirmation = inputCofirmation != null && !inputCofirmation.isEmpty();
      
      Model model = ModelFactory.getModel();
      TableData table = TableData.fromView(model.getFullView());
      TableExporter exporter = new TableExporter(table);
      // Overwrites the imported .csv file with current model data
      // Gets a full model view so that the entire database is saved

      Path path = model.getPathToCsv();
      String fileName = path.getFileName().toString().toLowerCase();
      request.setAttribute("path", path.toString());
      if (hasConfirmation) {
        if (fileName.endsWith(".csv")) {
          exporter.toCSV(path);
        } else if (fileName.endsWith(".json")) {
          exporter.toJSON(path);
        } else {
          throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
        // Dispatch request to the DataServlet
        // 'table' and 'key' request attributes will be set by the DataServlet
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}