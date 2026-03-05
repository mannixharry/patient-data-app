package uk.ac.ucl.model;

import java.util.List;
import java.util.ArrayList;

public class Column {
  /* 
  - Represents a column of data with a name and rows.
  - Checks the column name and row values are not null. 
  */
 
  private final String name;
  private final List<String> rows;

  public Column(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Column name cannot be null");
    }
    this.name = name;
    this.rows = new ArrayList<String>();
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return rows.size();
  }

  
  public boolean hasRowValue(int row)
  {
    // Need to consider making this private
   return row >= 0 || row < rows.size();
  }

  public String getRowValue(int row) {
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column " + name + ": Row index " + row + " out of bounds");
    }
    return rows.get(row);
  }

  public void setRowValue(int row, String value) {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column " + name + ": Row index " + row + " out of bounds");
    }
    rows.set(row, value);
  }

  public void addRowValue(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    rows.add(value);
  }
}