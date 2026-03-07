package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class DataFrameView implements Frame {
  /*
  A DataFrameView represents a filtered or partial view of a DataFrame
  Instead of storing its own data, it maintains a list of row indices that 
  reference rows in the source DataFrame. 
  This is useful for searching, and sorting since it provides read-only access
  to the selected rows.
  */
  private final DataFrame sourceDf;
  private final List<Integer> rowIndices;

  public DataFrameView(DataFrame sourceDf, List<Integer> rowIndices)
  {
    this.sourceDf = sourceDf;
    this.rowIndices = List.copyOf(rowIndices); 
  }

  public DataFrameView(DataFrameView sourceView, List<Integer> rowIndices)
  {
    this.sourceDf = sourceView.getSource();
    List<Integer> sourceIndices = sourceView.getRowIndices();
    List<Integer> mappedIndices = new ArrayList<>(rowIndices.size());
    for (int rowIndex : rowIndices) {
      mappedIndices.add(sourceIndices.get(rowIndex));
    }
    this.rowIndices = List.copyOf(mappedIndices);
  }

  public static DataFrameView fullView(DataFrame df) {

    if (df.getColumnNames().isEmpty()) {
      throw new IllegalArgumentException("Cannot create full view of empty DataFrame");
    }

    int rowCount = df.getRowCount(df.getColumnNames().get(0));
    List<Integer> allIndices = new ArrayList<>(rowCount);
    for (int i = 0; i < rowCount; i++) {
      allIndices.add(i);
    }
    return new DataFrameView(df, allIndices);
  }

  public DataFrame getSource() {
    return sourceDf; 
  }

  public List<Integer> getRowIndices() {
    return List.copyOf(rowIndices);
  }

  @Override
  public List<String> getColumnNames()
  {
    return sourceDf.getColumnNames();
  }

  @Override
  public List<String> getColumnValues(String columnName) 
  {
    List<String> sourceColumn = sourceDf.getColumnValues(columnName);
    List<String> viewColumn = new ArrayList<>(rowIndices.size());
    for (int rowIndex : rowIndices) {
      viewColumn.add(sourceColumn.get(rowIndex));
    }
    return viewColumn;
  }

  @Override
  public JSPTable toJSPTable() 
  {
    List<String> names = sourceDf.getColumnNames();
    List<List<String>> columns = new ArrayList<>(names.size());

    for (String columnName : names)
    {
      List<String> columnEntries = sourceDf.getColumnValues(columnName);
      List<String> column = new ArrayList<>(rowIndices.size());
      for (int rowIndex : rowIndices) {
        column.add(columnEntries.get(rowIndex));
      }
      columns.add(column);
    }
    return new JSPTable(names, columns);
  }
}
