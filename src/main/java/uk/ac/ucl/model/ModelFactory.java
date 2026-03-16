package uk.ac.ucl.model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ModelFactory implements the singleton pattern to enable a single instance of
 * the Model class to be shared across in the Controller.
 */
public class ModelFactory {

  private static Model model;

  /**
   * Returns the singleton model.
   * @return the {@link Model} object
   */
  public static Model getModel() {
    // Instantiate the model once
    if (model == null) {
      model = new Model();
      final Path pathToDefaultFile = Paths.get("data/patients100.csv");
      model.load(pathToDefaultFile);
      model.setKey("ID");
      model.getPagedView().setCurrentPage(0);
    }
    return model;
  }
}