package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.utility.DirectoryScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handles file import requests, loading a CSV or JSON file from the '/data'
 * directory into the model's {@link DataFrame} object. Uses a multi-step form
 * design where 'path' and 'key' parameters are accumulated across several GET
 * requests.
 */
@WebServlet({ "/importFile" })
public class ImportFileServlet extends BaseServlet {

  /**
   * Handles the GET request for file imports. If 'path' is present, loads the
   * file and returns the available key columns. If 'key is present, set it as the
   * model's current primary key.
   * 
   *
   * @param request  the HTTP request, optionally with 'path' and 'key' parameters
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      request.setAttribute("pageMode", "import");

      Model model = ModelFactory.getModel();

      // Gets the full path to /data (where the .csv files are stored)
      String projectRoot = System.getProperty("user.dir"); // COMP0004-Coursework
      Path dataDirectory = Paths.get(projectRoot, "data");

      // User inputs (see import.jsp) are passed as parameters in the URL
      // Since the user enters this information using separate HTML forms, we store
      // each parameter, when they are input, in the URL, until the full request is
      // gathered. This is a common design pattern in the project
      String inputPath = request.getParameter("path");
      String inputKey = request.getParameter("key");
      // Set flags to indicate which parameters are present
      boolean hasPath = inputPath != null && !inputPath.isEmpty();
      boolean hasKey = inputKey != null && !inputKey.isEmpty();

      // Retrieve a list of file names in webapp/data and attach to request
      List<String> files = DirectoryScanner.getCsvAndJsonFiles(dataDirectory.toString());
      request.setAttribute("files", files);

      if (hasPath) {
        // Get the full path to the input .csv file and load it into the model
        Path fullPath = dataDirectory.resolve(inputPath);
        model.load(fullPath);

        // Java stream interrogates the model to get a list of column names with unique
        // column values. We attach this to the request so import.jsp can show the user
        // a list of possible keys to select from
        request.setAttribute("keyList",
            model.getDf().getColumnNames().stream().filter(key -> model.checkValidKey(key)).toList());
      }

      if (hasKey) {
        model.setKey(inputKey);
        request.setAttribute("key", inputKey);
      }

      // DataServlet also attaches TableData so it is unnecessary here
      forward(request, response, "/data");

    } catch (Exception e) {
      forwardToError(request, response, "Import failed: " + e.getMessage());
    }
  }
}