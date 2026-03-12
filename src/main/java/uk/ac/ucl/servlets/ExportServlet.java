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

@WebServlet({ "/export" })
public class ExportServlet extends BaseServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "export");

      String fileName = request.getParameter("fileName");
      String fileType = request.getParameter("fileType");
      String exportLocation = request.getParameter("location");

      boolean hasFileName = fileName != null && !fileName.isEmpty();
      boolean hasFileType = fileType != null && !fileType.isEmpty();
      boolean hasExportLocation = exportLocation != null && !exportLocation.isEmpty();

      Model model = ModelFactory.getModel();

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
          String projectRoot = System.getProperty("user.dir"); // COMP0004-Coursework
          Path dataDirectory = Paths.get(projectRoot, "data");
          Path exportPath = dataDirectory.resolve(fileName); // This will break things
          if ("csv".equals(fileType)) {
            exporter.toCSV(exportPath);
          } else if ("json".equals(fileType)) {
            exporter.toJSON(exportPath);
          }
        } else if ("download".equals(exportLocation)) {
          response.setContentType("text/csv");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
          Writer writer = response.getWriter();
          if ("csv".equals(fileType)) {
            exporter.writeCSV(writer);
          } else if ("json".equals(fileType)) {
            exporter.writeJSON(writer);
          }
          return; // Avoid forwarding request to DataServlet
        }
      }

      request.setAttribute("fileName", fileName);
      request.setAttribute("fileType", fileType);
      request.setAttribute("location", exportLocation);

      // Dispatch request to the DataServlet
      // 'table' and 'key' request attributes will be set by the DataServlet

      forward(request, response, "/data");

    } catch (Exception e) {
      forwardToError(request, response, "Export failed" + e.getMessage());
    }
  }
}
