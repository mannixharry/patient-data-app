package uk.ac.ucl.model;

import java.util.List;

public class Model {

  private final DataLoader dataLoader = new DataLoader();
  SearchEngine searchEngine = new SearchEngine();
  SortEngine sortEngine = new SortEngine();
  ViewEngine viewEngine = new ViewEngine();

  private DataFrame df; 
  private DataFrameView view; 
  private String key;

  public void loadCsv(String pathToCsv)
  {
    this.df = dataLoader.load(pathToCsv);
    this.view = DataFrameView.fullView(df);
  }

  public List<String> getColumnNames() {
    return df.getColumnNames(); 
  }

  public String getKeyName() {
    return key;
  }

  public void setKey(String key) {
    if (checkValidKey(key)) {
      this.key = key;
    } else {
      throw new IllegalArgumentException("Invalid key: " + key);
    }
  }

  public boolean checkValidKey(String key) {
    if (key == null || key.isEmpty()) {return true;}
    List<String> keyEntries = df.getColumnValues(key);
    return keyEntries.size() == keyEntries.stream().distinct().count();
  }
  
  public int getKeyIndex() {
    return df.getColumnNames().indexOf(getKeyName());
  }

  public int getRowIndexInDfByValue(String columnName, String value) {
    return df.getRowIndexByValue(columnName, value);
  }

  public int getRowIndexInViewByValue(String columnName, String value) {
    return view.getRowIndexByValue(columnName, value);
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

  public void restrictViewRows(List<Integer> rowIndices)
  {
    view = viewEngine.restrictRows(view, rowIndices);
  }

  public void restrictViewToRowRange(int start, int end)
  {
    view = viewEngine.restrictToRowRange(view, start, end);
  }

  public void restrictViewColumns(List<String> columnNames) 
  {
    view = viewEngine.restrictColumns(view, columnNames);
  }

  public void refreshView() { 
    view = DataFrameView.fullView(df);
  }

  public void emptyView() {
    view = DataFrameView.emptyView(df);
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
