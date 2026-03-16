package uk.ac.ucl.model;

import java.util.List;
import java.util.ArrayList;

public class Column {
  /**
   * Stores a single column of data under a given name.
   */

  private final String name;
  private final List<String> entries;

  /**
   * Constructs an empty column of data under an input name.
   * 
   * @param name the name of the column
   * @throws IllegalArgumentException if the input column name is null
   */
  public Column(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Column name cannot be null");
    }
    this.name = name;
    this.entries = new ArrayList<String>();
  }

  /**
   * Returns the column's name.
   * 
   * @return a {@link String} of the column's name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the number of rows stored in the column.
   * 
   * @return the number of rows
   */
  public int getSize() {
    return entries.size();
  }

  /**
   * Returns a copy of the list of values stored in the column.
   * 
   * @return a {@link List} of {@link String} values
   */
  public List<String> getEntries() {
    // Returns a copy to enforce data immutability
    return List.copyOf(entries);
  }

  /**
   * Returns the index of the first occurrence of a given value in the column.
   * 
   * @param value the {@link String} value to search for
   * @return the zero-based index of the first matching row, or -1 if the value is
   *         not found
   */
  public int find(String value) {
    for (int rowIndex = 0; rowIndex < getSize(); rowIndex++) {
      if (value.equals(entries.get(rowIndex))) {
        return rowIndex;
      }
    }
    return -1;
  }

  /**
   * Returns true if the column has a stored value at a given row index.
   * 
   * @param row the row index
   * @return true if there is data at the given index, false if there is not
   */
  public boolean hasRowValue(int row) {
    return row >= 0 && row < entries.size();
  }

  /**
   * Gets the value stored at a given row.
   * 
   * @param row the row index
   * @throws IndexOutOfBoundsException if the row index is out of bounds
   */
  public String getRowValue(int row) {
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column: " + name + "; Row index out of bounds: " + row);
    }
    return entries.get(row);
  }

  /**
   * Sets the value at a row index to a given value.
   * 
   * @param row   the row index
   * @param value the value to insert
   * @throws IllegalArgumentException  if the value to insert is null
   * @throws IndexOutOfBoundsException if the row index is out of bounds
   */
  public void setRowValue(int row, String value) {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    if (!hasRowValue(row)) {
      throw new IndexOutOfBoundsException("Column: " + name + "; Row index out of bounds: " + row);
    }
    entries.set(row, value);
  }

  /**
   * Adds a given value to the end of the column.
   * 
   * @param value the value to insert
   * @throws IllegalArgumentException if the column does not exist
   */
  public void addRowValue(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Row value cannot be null");
    }
    entries.add(value);
  }

  /**
   * Deletes a value at a given index in the column.
   * 
   * @param row the index of the row to delete
   * @throws IndexOutOfBoundsException if the row index is invalid (< 0 or >= row
   *                                   count)
   */
  public void deleteRowValue(int row) {
    entries.remove(row);
  }
}