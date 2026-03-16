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

/**
 * Handles requests to save current data in the model to the file it was loaded
 * from (overwriting original data).
 */
@WebServlet({ "/saveFile" })
public class SaveServlet extends BaseServlet {

  /**
   * Handles GET requests for saving, requiring an input 'confirmation' parameter
   * to be set before exporting the model's data to the same file location it was
   * originally loaded from.
   * 
   * @param request  the HTTP request, optionally including the parameter
   *                 'confirmation' (a final check before over-writing data)
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      Model model = ModelFactory.getModel();

      request.setAttribute("pageMode", "save");

      String inputConfirmation = request.getParameter("confirmation");
      boolean confirmationPresent = inputConfirmation != null && !inputConfirmation.isEmpty();

      // Overwrites the imported CSV or JSON file with current model data
      // Gets a full model view so that the entire database is saved
      TableData table = TableData.fromView(model.getFullView());
      TableExporter exporter = new TableExporter(table);

      Path path = model.getPath();
      String fileName = path.getFileName().toString().toLowerCase();
      request.setAttribute("path", path.toString());
      if (confirmationPresent) {
        if (fileName.endsWith(".csv")) {
          exporter.toCSV(path);
        } else if (fileName.endsWith(".json")) {
          exporter.toJSON(path);
        } else {
          throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Unexpected error: " + e.getMessage());
    }
  }
}