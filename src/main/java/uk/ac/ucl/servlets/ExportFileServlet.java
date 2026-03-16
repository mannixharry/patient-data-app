package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.view.TableData;
import uk.ac.ucl.view.TableExporter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles file export requests for the model's current view to CSV or JSON.
 * Provides two export locations:
 * - 'data': saves the file to the /data directory on the server.
 * - 'download': streams the file directly to the client's downloads folder.
 */
@WebServlet({ "/exportFile" })
public class ExportFileServlet extends BaseServlet {

  /**
   * Handles the get GET request for file exports.
   * Requires 'fileName', 'fileType' (csv/json), and 'location' (data/download)
   * parameters.
   * If any parameter is missing, forwards to the export form without exporting
   * anything.
   * 
   * @param request the HTTP request, optionally with 'fileNAme', 'fileType' and 'location' parameters set
   * @param response the HTTP response
   * @throws IOException if forwarding or writing to the download stream fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {

      Model model = ModelFactory.getModel();
      request.setAttribute("pageMode", "export");

      String fileName = request.getParameter("fileName");
      String fileType = request.getParameter("fileType");
      String exportLocation = request.getParameter("location");

      boolean hasFileName = fileName != null && !fileName.isEmpty();
      boolean hasFileType = fileType != null && !fileType.isEmpty();
      boolean hasExportLocation = exportLocation != null && !exportLocation.isEmpty();

      request.setAttribute("fileName", fileName);
      request.setAttribute("fileType", fileType);
      request.setAttribute("location", exportLocation);

      if (hasFileName && hasFileType && hasExportLocation) {
        TableData table = TableData.fromView(model.getView());
        TableExporter exporter = new TableExporter(table);

        if ("csv".equals(fileType)) {
          if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
          }
        } else if ("json".equals(fileType)) {
          if (!fileName.toLowerCase().endsWith(".json")) {
            fileName += ".json";
          }
        }
        if ("data".equals(exportLocation)) {
          // Gets the full path to /data directory from the project root
          Path dataDirectory = Paths.get(System.getProperty("user.dir"), "data");
          Path exportPath = dataDirectory.resolve(fileName);
          if ("csv".equals(fileType)) {
            exporter.toCSV(exportPath);
          } else if ("json".equals(fileType)) {
            exporter.toJSON(exportPath);
          }
          request.setAttribute("path", exportPath.toString());
        } else if ("download".equals(exportLocation)) {
          // Set the content type based on the file format
          response.setContentType("csv".equals(fileType) ? "text/csv" : "application/json");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
          Writer writer = response.getWriter();
          if ("csv".equals(fileType)) {
            exporter.writeCSV(writer);
          } else if ("json".equals(fileType)) {
            exporter.writeJSON(writer);
          }
          return; // Response already written as the download stream, so skip forwarding to
                  // DataServlet
        }
      }
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Export failed: " + e.getMessage());
    }
  }
}
