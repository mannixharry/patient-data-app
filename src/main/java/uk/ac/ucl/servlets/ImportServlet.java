package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.utility.CsvDirectoryScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@WebServlet({ "/import" })
public class ImportServlet extends BaseServlet {
  public ImportServlet() {
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      // Flag to tell JSPs to display (current) table + import input bar
      request.setAttribute("pageMode", "import");

      Model model = ModelFactory.getModel();

      // Gets the full path to /data (where the .csv files are stored)
      String projectRoot = System.getProperty("user.dir"); // COMP0004-Coursework
      Path dataDirectory = Paths.get(projectRoot, "data");

      // User inputs (see import.jsp) are passed as parameters in the URL
      // Since the user enters this information using seperate HTML forms, we store
      // each parameter, when they are input, in the URL, until the full request is
      // gathered. This is a common design pattern in the project
      String inputPath = request.getParameter("path");
      String inputKey = request.getParameter("key");
      // Set flags to indicate which parameters are present
      boolean hasPath = inputPath != null && !inputPath.isEmpty();
      boolean hasKey = inputKey != null && !inputKey.isEmpty();

      // Retrieve a list of csv file names in webapp/data and attach to request
      List<String> files = CsvDirectoryScanner.getCsvFiles(dataDirectory.toString());
      request.setAttribute("files", files);

      if (hasPath) {
        // Get the full path to the input .csv file and load it into the model
        Path fullPath = dataDirectory.resolve(inputPath);
        model.loadCsv(fullPath);

        // Java stream interrogates the model to get a list of column names with unique
        // column values. We attach this to the request so import.jsp can show the user
        // a list of possible keys to select from
        request.setAttribute("keyList",
            model.getColumnNames().stream().filter(key -> model.checkValidKey(key)).toList());
      }

      if (hasKey) {
        // Update model to include input key and attach it as a request parameter
        model.setKey(inputKey);
        request.setAttribute("key", inputKey);
      }

      // Dispatch request to DataServlet (so the new table is displayed)
      // DataServlet also attaches TableData so it is unnecessary here
      forward(request, response, "/data");

    } catch (Exception e) {
      forwardToError(request, response, "Import failed" + e.getMessage());
    }
  }
}