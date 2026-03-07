package uk.ac.ucl.model;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class DataFrame implements Frame {
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

  @Override
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

  @Override
  public List<String> getColumnValues(String columnName)
  {
    return getColumnInternal(columnName).getEntries(); // Use private method
  }

  public int getRowCount(String columnName)
  {
    return getColumnInternal(columnName).getSize();
  }

  public String getValue(String columnName, int row)
  {
    return getColumnInternal(columnName).getRowValue(row);
  }

  public void putValue(String columnName, int row, String value)
  {
    getColumnInternal(columnName).setRowValue(row, value);
  }

  public void addValue(String columnName, String value)
  {
    getColumnInternal(columnName).addRowValue(value);
  }

  public void addRowByValues(List<String> values)
  {
    List<String> columns = getColumnNames();
    int numberOfColumns = columns.size();
    if (values.size() != numberOfColumns) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    for (int index = 0; index < numberOfColumns; index++)
    {
      addValue(columns.get(index), values.get(index));
    }
  }

  @Override
  public JSPTable toJSPTable() 
  {
    List<String> names = getColumnNames();
    List<List<String>> columns = new ArrayList<>(names.size());
    for (String columnName : names)
    {
      List<String> columnEntries = getColumnValues(columnName);
      int rowCount = columnEntries.size();
      List<String> column = new ArrayList<>(rowCount);
      for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
        column.add(columnEntries.get(rowIndex));
      }
      columns.add(column);
    }
    return new JSPTable(names, columns);
  }
}