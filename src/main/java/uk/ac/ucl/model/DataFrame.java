package uk.ac.ucl.model;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class DataFrame {
  /*
  - Stores a collection of columns and their names
  - columnMap maps column names to column data
   */

  private final Map<String, Column> columnMap;

  public DataFrame()
  {
    // LinkedHashMap tracks insertion order 
    this.columnMap = new LinkedHashMap<String, Column>();
  }

  public void addColumn(String name)
  {
    Objects.requireNonNull(name, "Column name cannot be null");

    if (columnMap.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate column name: " + name);
    }

    Column newColumn = new Column(name);
    columnMap.put(name, newColumn);
  }

  public List<String> getColumnNames()
  {
    // The returned List<String> is immutable
    return List.copyOf(columnMap.keySet());
  }

  private Column getColumn(String name)
  {
    Column column = columnMap.get(name);
    if (column == null) { 
      throw new IllegalArgumentException("No column named: " + name);
    }
    return column;
  }

  public int getRowCount(String name)
  {
    return getColumn(name).getSize();
  }

  public String getValue(String name, int row)
  {
    return getColumn(name).getRowValue(row);
  }

  public void putValue(String name, int row, String value)
  {
    getColumn(name).setRowValue(row, value);
  }

  public void addValue(String name, String value)
  {
    getColumn(name).addRowValue(value);
  }

    public Map<String, String> getRow(int rowIndex)
  {
    List<String> columnNames = getColumnNames();
    Column firstColumn = columnMap.get(columnNames.get(0));
    if (!firstColumn.hasRowValue(rowIndex)) {
      throw new IllegalArgumentException("No row with index: " + rowIndex + " in database");
    }
    Map<String, String> row = new LinkedHashMap<>();
    for (String name : columnNames)
    {
      String value = getValue(name, rowIndex);
      row.put(name, value);
    }
    return row;
  }

  public List<Map<String, String>> getRows()
  {
    List<Map<String, String>> rows = new ArrayList<Map<String, String>>();

    List<String> columnNames = getColumnNames();
    if (columnNames.isEmpty()) {
      return rows;
    }

    int rowCount = getRowCount(columnNames.get(0));

    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
    {
      rows.add(getRow(rowIndex));
    }
    return rows; 
  }
}
