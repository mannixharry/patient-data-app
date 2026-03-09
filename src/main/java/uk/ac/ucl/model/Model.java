package uk.ac.ucl.model;

import java.util.List; 

public class Model {

  private final DataLoader dataLoader = new DataLoader();
  SearchEngine searchEngine = new SearchEngine();
  SortEngine sortEngine = new SortEngine();

  private DataFrame df; 
  private DataFrameView view; 

  public void loadCsv(String pathToCsv)
  {
    this.df = dataLoader.load(pathToCsv);
    this.view = DataFrameView.fullView(df);
  }

  public List<String> getColumnNames() {
    return df.getColumnNames(); 
  }

  public int getRowIndexByValue(String columnName, String value) {
    return df.getRowIndexByValue(columnName, value);
  }

  public void setRow(int row, List<String> values) {
    df.setRowByValues(row, values);
  }

  public void addRow(List<String> values) {
    df.addRowByValues(values);
  }

  public void deleteRow(int row) {
    df.deleteRow(row);
  }

  public DataFrameView getView() {
    return view;
  }

  public void clearView() { 
    view = DataFrameView.fullView(df);
  }

  public void search(String searchColumn, String searchTerm, boolean useRegex) {
    if (searchColumn.equals("ANY")) {
      view = searchEngine.searchAll(view, searchTerm, useRegex);
    } else {
      view = searchEngine.search(view, searchColumn, searchTerm, useRegex);
    }
  }

  public void sort(String sortColumn, boolean ascending) {
    view = sortEngine.sort(view, sortColumn, ascending);
  }
}
