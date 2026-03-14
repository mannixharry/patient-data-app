package uk.ac.ucl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class Model {

  private final DataLoader dataLoader = new DataLoader();
  SearchEngine searchEngine = new SearchEngine();
  SortEngine sortEngine = new SortEngine();
  ChartEngine chartEngine = new ChartEngine(25);

  private DataFrame df;
  private DataFrameView view;
  private PagedView pagedView;
  private Path pathToCsv;
  private String key;

  public void load(Path path) {
    this.df = dataLoader.load(path);
    this.view = DataFrameView.fullView(df);
    this.pagedView = new PagedView(view, 50);
    this.pathToCsv = path;
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
    if (key == null || key.isEmpty()) {
      return true;
    }
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

  public void setRowThroughView(int row, List<String> values) {
    df.setRowByValues(view.getRowIndexInDf(row), values);
  }

  public void deleteRowThroughView(int row) {
    df.deleteRow(view.getRowIndexInDf(row));
  }

  public int getLastRowIndex() {
    return df.getRowCount() - 1;
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

  public DataFrameView getFullView() {
    return DataFrameView.fullView(df);
  }

  public DataFrameView getEmptyView() {
    return DataFrameView.emptyView(df);
  }

  public void restrictViewRows(List<Integer> rowIndices) {
    view = view.restrictRows(rowIndices);
    pagedView.updateSourceView(view);
  }

  public void restrictViewToRowRange(int start, int end) {
    view = view.restrictToRowRange(start, end);
    pagedView.updateSourceView(view);
  }

  public void restrictViewColumns(List<String> columnNames) {
    view = view.restrictColumns(columnNames);
    pagedView.updateSourceView(view);
  }

  public void restrictViewToRow(int rowIndex) {
    view = view.restrictToRow(rowIndex);
    pagedView.updateSourceView(view);
  }

  public void refreshView() {
    view = DataFrameView.fullView(df);
    pagedView.updateSourceView(view);
  }

  public void emptyView() {
    view = DataFrameView.emptyView(df);
  }

  public void search(String searchColumn, String searchTerm, boolean useRegex) {
    if (searchColumn.equals("ANY")) {
      view = searchEngine.searchAll(view, searchTerm, useRegex);
      pagedView.updateSourceView(view);

    } else {
      view = searchEngine.search(view, searchColumn, searchTerm, useRegex);
      pagedView.updateSourceView(view);

    }
  }

  public void sort(String sortColumn, boolean ascending) {
    view = sortEngine.sort(view, sortColumn, ascending);
    pagedView.updateSourceView(view);
  }

  public Path getPathToCsv() {
    return pathToCsv;
  }

  public DataFrameView getPagedView() {
    return pagedView.getCurrentPageView();
  }

  public void setCurrentPage(int pageIndex) {
    pagedView.setCurrentPage(pageIndex);
  }

  public int getCurrentPage() {
    return pagedView.getCurrentPage();
  }

  public int getPageSize() {
    return pagedView.getPageSize();
  }

  public void setPageSize(int size) {
    pagedView.setPageSize(size);
  }

  public int getTotalPages() {
    return pagedView.getTotalPages();
  }

  public Map<String, Map<String, Integer>> getChartData() {
    Map<String, Map<String, Integer>> charts = chartEngine.getChartData(view);

    return charts;
  }
}
