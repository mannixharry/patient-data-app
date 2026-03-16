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

/**
 * Handles file deletion requests for CSV/JSON data file in the /data directory.
 * Requires both filename and a confirmation parameter before deleting. Prevents
 * deletion of the currently loaded file.
 */
@WebServlet({ "/deleteFile" })
public class DeleteFileServlet extends BaseServlet {

  /**
   * Handles the GET request for file deletion.
   *
   * @param request  the HTTP request, optionally with 'path' and 'confirmation'
   *                 parameters
   * @param response the HTTP response
   * @throws IOException      if forwarding fails
   * @throws ServletException if the request dispatcher cannot forward
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      Model model = ModelFactory.getModel();
      request.setAttribute("pageMode", "delete");
      // Gets the full path to /data directory from the project root
      Path dataDirectory = Paths.get(System.getProperty("user.dir"), "data");
      String inputPath = request.getParameter("path");
      String inputConfirmation = request.getParameter("confirmation");
      boolean hasPath = inputPath != null && !inputPath.isEmpty();
      boolean confirmationPresent = inputConfirmation != null && !inputConfirmation.isEmpty();
      // Prevent deletion of the currently loaded file
      boolean fileOpenFlag = hasPath && inputPath.equals(model.getPath().getFileName().toString());
      request.setAttribute("fileOpenFlag", fileOpenFlag);
      if (hasPath && confirmationPresent && !fileOpenFlag) {
        Path fullPath = dataDirectory.resolve(inputPath);
        Files.deleteIfExists(fullPath);
      }
      List<String> files = DirectoryScanner.getCsvAndJsonFiles(dataDirectory.toString());
      request.setAttribute("files", files);
      forward(request, response, "/data");
    } catch (Exception e) {
      forwardToError(request, response, "Error deleting file: " + e.getMessage());
    }
  }
}
