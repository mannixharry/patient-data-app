package uk.ac.ucl.model;
import java.nio.file.Path;
import java.nio.file.Paths;
public class ModelFactory {

  private static Model model; 

  public static Model getModel()
  {
    if (model == null)
    {
      model = new Model();
      final Path pathToCsv = Paths.get("data/patients100.csv");
      model.loadCsv(pathToCsv);
    }
    return model;
  }
}