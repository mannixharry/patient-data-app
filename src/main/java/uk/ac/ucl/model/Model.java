package uk.ac.ucl.model;

import java.util.List;
import java.util.Map; 

public class Model {
  private final DataLoader dataLoader = new DataLoader(); 
  private DataFrame df; 

  public void loadCsv(String pathToCsv)
  {
    this.df = dataLoader.load(pathToCsv);
  }

  public DataFrame getDataFrame()
  {
    return df;
  }

  public List<String> getColumnNames()
  {
    return df.getColumnNames();
  }

  public List<Map<String, String>> getRows()
  {
    return df.getRows();
  }
}
