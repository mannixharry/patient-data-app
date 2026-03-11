package uk.ac.ucl.model;

import java.util.List;

import java.util.ArrayList;

public class Column {
  /* 
  - Represents a column of data with a name and rows.
  - Maintains the invariant that column name and values are not null.
  */
 
  private final String name;
  private final List<String> entries;

  public Column(String name) 
  {
    if (name == null) {
      throw new IllegalArgumentException("Column name cannot be null");
    }
    this.name = name;
    this.entries = new ArrayList<String>();
  }

  public String getName() 
  {
    return name;
  }

  public int getSize() 
  {
    return entries.size();
  }

  public List<String> getEntries() 
  {
    return List.copyOf(entries); // Return a copy to enforce immutability
  }

  public int find(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Search value cannot be null");
    }
    for (int rowIndex=0; rowIndex < getSize(); rowIndex++) {
      if (value.equals(entries.get(rowIndex))) {
        return rowIndex;
      }
    }
    return -1; 
  }

  public boolean hasRowValue(int row) 
  {
   return row >= 0 && row < entries.size();
  }

  public String getRowValue(int row) 
  {
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column: " + name + "; Row index out of bounds: " + row);
    }
    return entries.get(row);
  }

  public void setRowValue(int row, String value) 
  {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column: " + name + "; Row index out of bounds: " + row);
    }
    entries.set(row, value);
  }

  public void addRowValue(String value) 
  {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    entries.add(value);
  }

  public void deleteRowValue(int row) 
  {
    entries.remove(row);
  }
}