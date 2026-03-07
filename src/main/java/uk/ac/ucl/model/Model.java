package uk.ac.ucl.model;

import java.util.List;

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
  public DataFrame sortDataFrame(List<String> ordering) {
    DataFrame sortedDf = new DataFrame(); 
    return sortedDf;
  }
}
