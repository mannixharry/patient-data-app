package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DataFrameView {
  /*
   * A DataFrameView represents a filtered or partial view of a DataFrame
   * Instead of storing its own data, it maintains a list of row indices that
   * reference rows in the source DataFrame.
   * This is useful for searching, and sorting since it provides read-only access
   * to the selected rows.
   */
  private final DataFrame source;
  private final List<Integer> rowIndices;
  private final List<Integer> columnIndices;

  // Needs attention: make sure doesnt fail on rowIndices out of range
  // NEEDS TO ENSURE VIEW IS UNIQUE!!!!!!!! (MAYBE IDK)
  // Columns use the same logic as Rows for simplicity (but we never really chain
  // columnViews together)
  public DataFrameView(DataFrame sourceDf, List<Integer> rowIndices, List<Integer> columnIndices) {
    this.source = sourceDf;
    int rowCount = sourceDf.getRowCount();
    int columnCount = sourceDf.getColumnNames().size();
    this.rowIndices = List.copyOf(defaultIndices(rowIndices, rowCount));
    this.columnIndices = List.copyOf(defaultIndices(columnIndices, columnCount));
  }

  public DataFrameView(DataFrameView sourceView, List<Integer> rowIndices, List<Integer> columnIndices) {
    this.source = sourceView.getSource();

    List<Integer> sourceRows = sourceView.getRowIndices();
    List<Integer> sourceColumns = sourceView.getColumnIndices();

    this.rowIndices = List.copyOf(mapIndices(sourceRows, defaultIndices(rowIndices, sourceRows.size())));
    this.columnIndices = List.copyOf(mapIndices(sourceColumns, defaultIndices(columnIndices, sourceColumns.size())));
  }

  private List<Integer> defaultIndices(List<Integer> indices, int size) {
    if (indices != null) {
      return indices;
    }
    List<Integer> all = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      all.add(i);
    }
    return all;
  }

  private List<Integer> mapIndices(List<Integer> source, List<Integer> toMap) {
    List<Integer> mapped = new ArrayList<>(toMap.size());
    for (int i : toMap) {
      mapped.add(source.get(i));
    }
    return mapped;
  }

  public static DataFrameView fullView(DataFrame df) {
    return new DataFrameView(df, null, null);
  }

  public static DataFrameView emptyView(DataFrame df) {
    List<Integer> emptyIndices = new ArrayList<>();
    return new DataFrameView(df, emptyIndices, null);
  }

  public DataFrame getSource() {
    return source;
  }

  public List<Integer> getRowIndices() {
    return List.copyOf(rowIndices);
  }

  public int getRowCount() {
    return rowIndices.size();
  }

  public List<Integer> getColumnIndices() {
    return List.copyOf(columnIndices);
  }

  public List<String> getColumnNames() {
    List<String> sourceNames = source.getColumnNames();
    List<String> viewNames = new ArrayList<>(columnIndices.size());
    for (int columnIndex : columnIndices) {
      viewNames.add(sourceNames.get(columnIndex));
    }
    return viewNames;
  }

  public List<String> getColumnValues(String columnName) {
    List<String> sourceColumn = source.getColumnValues(columnName);
    List<String> viewColumn = new ArrayList<>(rowIndices.size());
    for (int rowIndex : rowIndices) {
      viewColumn.add(sourceColumn.get(rowIndex));
    }
    return viewColumn;
  }

  public List<String> getColumnValuesByIndex(int columnIndex) {
    List<String> sourceColumn = source.getColumnValues(source.getColumnNames().get(columnIndices.get(columnIndex)));
    List<String> viewColumn = new ArrayList<>(rowIndices.size());
    for (int rowIndex : rowIndices) {
      viewColumn.add(sourceColumn.get(rowIndex));
    }
    return viewColumn;
  }

  public int getRowIndexByValue(String columnName, String value) {
    List<String> column = getColumnValues(columnName);
    for (int i = 0; i < column.size(); i++) {
      if (column.get(i).equals(value)) {
        return i;
      }
    }
    return -1; // not found
  }

  public int getRowIndexInDf(int rowIndex) {
    return rowIndices.get(rowIndex);
  }

  public int getColumnIndexInDf(int columnIndex) {
    return columnIndices.get(columnIndex);
  }

  public DataFrameView restrictRows(List<Integer> rowIndices) {
    return new DataFrameView(this, rowIndices, null);
  }

  public DataFrameView restrictToRowRange(int start, int end) {
    List<Integer> restrictedRows = IntStream.range(start, end).boxed().toList();
    return new DataFrameView(this, restrictedRows, null);
  }

  public DataFrameView restrictToRow(int rowIndex) {
    return new DataFrameView(this, List.of(rowIndex), null);
  }

  public DataFrameView restrictColumns(List<String> columnNames) {
    List<Integer> restrictedColumns = columnNames.stream().map(name -> getColumnNames().indexOf(name))
        .filter(index -> index != -1).toList();
    return new DataFrameView(this, null, restrictedColumns);
  }
}