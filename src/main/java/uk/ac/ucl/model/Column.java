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

  public boolean hasRowValue(int row) 
  {
   return row >= 0 && row < entries.size();
  }

  public String getRowValue(int row) 
  {
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column " + name + ": Row index " + row + " out of bounds");
    }
    return entries.get(row);
  }

  public void setRowValue(int row, String value) 
  {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column " + name + ": Row index " + row + " out of bounds");
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
}