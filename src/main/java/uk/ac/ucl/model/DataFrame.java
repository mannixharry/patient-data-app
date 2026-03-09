package uk.ac.ucl.model;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class DataFrame {
  /*
  - Stores a collection of columns and their names.
  - columnMap maps column names to column data.
   */

  private final Map<String, Column> columnMap;

  public DataFrame()
  {
    // LinkedHashMap tracks insertion order 
    this.columnMap = new LinkedHashMap<String, Column>();
  }

  public void addColumn(String name)
  {
    if (columnMap.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate column name: " + name);
    }

    Column newColumn = new Column(name); // includes null check.
    columnMap.put(name, newColumn);
  }

  public List<String> getColumnNames()
  {
    // The returned List<String> is immutable
    return List.copyOf(columnMap.keySet());
  }

  private Column getColumnInternal(String columnName)
  {
    Column column = columnMap.get(columnName);
    if (column == null) { 
      throw new IllegalArgumentException("No column columnNamed: " + columnName);
    }
    return column;
  }

  public List<String> getColumnValues(String columnName)
  {
    return getColumnInternal(columnName).getEntries(); // Use private method
  }

  public int getRowIndexByValue(String columnName, String value) {
    return columnMap.get(columnName).find(value);
  }

  public int getRowCount()
  {
    List<String> columns = getColumnNames();
    if (columns.isEmpty()) {
      throw new IllegalStateException("DataFrame has no columns -> cannnot get row count");
    }
    return getColumnInternal(columns.get(0)).getSize(); // Columns have uniform size
  }

  public String getValue(String columnName, int row)
  {
    return getColumnInternal(columnName).getRowValue(row);
  }

  public void setValue(String columnName, int row, String value)
  {
    getColumnInternal(columnName).setRowValue(row, value);
  }

  private void addValue(String columnName, String value)
  {
    // This is kept private to ensure all columns have a uniform size
    getColumnInternal(columnName).addRowValue(value);
  }

  public void addRowByValues(List<String> values)
  {
    List<String> columns = getColumnNames();
    int columnCount = columns.size();
    if (values.size() != columnCount) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
    {
      addValue(columns.get(columnIndex), values.get(columnIndex));
    }
  }

  public void setRowByValues(int row, List<String> values) {
    List<String> columns = getColumnNames();
    int columnCount = columns.size();
    if (values.size() != columnCount) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
    {
      setValue(columns.get(columnIndex), row, values.get(columnIndex));
    }
  }

  public void deleteRow(int row) {
    List<String> columns = getColumnNames();
    for (String name : columns) {
      columnMap.get(name).deleteRowValue(row);
    }
  }
}