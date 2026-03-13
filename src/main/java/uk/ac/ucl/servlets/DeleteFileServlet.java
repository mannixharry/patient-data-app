package uk.ac.ucl.servlets;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;
import uk.ac.ucl.utility.DirectoryScanner;

@WebServlet({ "/deleteFile" })
public class DeleteFileServlet extends BaseServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {

      Model model = ModelFactory.getModel();

      // Flag to tell JSPs to display (current) table + import input bar
      request.setAttribute("pageMode", "delete");

      // Gets the full path to /data (where the .csv files are stored)
      String projectRoot = System.getProperty("user.dir"); // COMP0004-Coursework
      Path dataDirectory = Paths.get(projectRoot, "data");

      String inputPath = request.getParameter("path");
      String inputCofirmation = request.getParameter("confirmation");

      // Set flags to indicate which parameters are present
      boolean hasPath = inputPath != null && !inputPath.isEmpty();
      boolean hasConfirmation = inputCofirmation != null && !inputCofirmation.isEmpty();

      boolean fileOpenFlag = false;
      if(hasPath) {
        fileOpenFlag = inputPath.equals(model.getPathToCsv().getFileName().toString());
      }
      request.setAttribute("fileOpenFlag", fileOpenFlag);

      if (hasPath && hasConfirmation && !fileOpenFlag) {
        Path fullPath = dataDirectory.resolve(inputPath);
        boolean deleted = Files.deleteIfExists(fullPath);
        if (deleted) {
            request.setAttribute("infoMessage", "File deleted successfully: " + inputPath);
        } else {
            request.setAttribute("infoMessage", "File not found: " + inputPath);
        }
      }
      
      // Retrieve a list of csv file names in webapp/data and attach to request
      List<String> files = DirectoryScanner.getCsvAndJsonFiles(dataDirectory.toString());
      request.setAttribute("files", files);

      forward(request, response, "/data");

    } catch (Exception e) {
      forwardToError(request, response, "Deletion failed: " + e.getMessage());
    }
  }
}
