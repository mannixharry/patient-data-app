package uk.ac.ucl.model;

import java.util.List;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Stores a collection of Column objects.
 * The DataFrame acts as the single source of truth for all loaded data,
 * storing column names mapped to their corresponding {@link Column} objects.
 */
public class DataFrame {

  private final Map<String, Column> columnMap;

  /**
   * Constructs an empty DataFrame.
   */
  public DataFrame() {
    // LinkedHashMap preserves insertion order (implemented as a hash map and a
    // doubly linked list)
    this.columnMap = new LinkedHashMap<String, Column>();
  }

  // Column related operation:
  /**
   * Adds an empty {@link Column} to the DataFrame
   * 
   * @param name - the name of the column to add
   * @throws IllegalArgumentException if a column with the same name already
   *                                  exists
   */
  public void addColumn(String name) {
    if (columnMap.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate column name: " + name);
    }
    Column newColumn = new Column(name);
    columnMap.put(name, newColumn);
  }

  /**
   * Returns true if the dataframe has a column that matches an input name
   * 
   * @param columnName the column name to search for
   * @return true if the model has the column being searched for; false otherwise
   */
  public boolean hasColumn(String columnName) {
    return getColumnNames().contains(columnName);
  }

  /**
   * Returns the names of all columns in the DataFrame.
   * 
   * @return an immutable list containing the column names in insertion order
   */
  public List<String> getColumnNames() {
    return List.copyOf(columnMap.keySet());
  }

  /**
   * Returns all entries of a specified column as a List<String>.
   * 
   * @param columnName the name of the column whose data is retrieved
   * @return a {@link List} of {@link String} values in the specified column
   * @throws IllegalArgumentException if the specified column does not exist
   */
  public List<String> getColumnValues(String columnName) {
    return getColumnInternal(columnName).getEntries();
  }
  
  // Single-value operations:
  /**
   * Returns the value at a given row index in a specified column.
   * 
   * @param columnName the name of the column
   * @param row        the zero-based index in the specified column
   * @return the value at the specified row in the column
   * @throws IllegalArgumentException  if the column does not exist
   * @throws IndexOutOfBoundsException if the row index is out of bounds
   */
  public String getValue(String columnName, int row) {
    return getColumnInternal(columnName).getRowValue(row);
  }

  /**
   * Sets the value at a row index in a specified column to a given value.
   * 
   * @param columnName the name of the column
   * @param row        the zero-based index in the specified column
   * @param value      the value to insert
   * @throws IllegalArgumentException  if the column does not exist
   * @throws IndexOutOfBoundsException if the row index is out of bounds
   */
  public void setValue(String columnName, int row, String value) {
    getColumnInternal(columnName).setRowValue(row, value);
  }

  // Row related operations:

  // A row-getting method like 'getRowValues' is not implemented to maintain
  // column-oriented logic to better reflect the way data is stored

  /**
   * Returns the row index of the first occurence of a given value in the
   * specified column.
   * 
   * @param columnName the name of the column to search
   * @param value      the {@link String} value to search for
   * @return the zero-based index of the first matching row, or -1 if the value is
   *         not found
   * @throws IllegalArgumentException if the specified column does not exist
   */
  public int getRowIndexByValue(String columnName, String value) {
    if (hasColumn(columnName)) {
      return columnMap.get(columnName).find(value);
    } else {
      throw new IllegalArgumentException("The specified column does not exist");
    }
  }

  /**
   * Returns the number of rows in the DataFrame.
   * Assumes all columns have the same number of entries.
   * 
   * @return the number of rows in the DataFrame
   * @throws IllegalStateException if the DataFrame has no columns
   */
  public int getRowCount() {
    List<String> columns = getColumnNames();
    if (columns.isEmpty()) {
      throw new IllegalStateException("DataFrame has no columns -> cannot get row count");
    }
    // Assume columns have uniform size
    return getColumnInternal(columns.get(0)).getSize();
  }

  /**
   * Sets a row in the DataFrame to a new row of values (given as a list). Each
   * value in the list is inserted into the corresponding row in each column in
   * the order the columns were created.
   * 
   * @param values the {@link List} of {@link String} values to insert (one for
   *               each columns)
   * @param row    the zero-based index of the row to modify
   * @throws IllegalArgumentException  if the number of values does not match the
   *                                   number of columns
   * @throws IndexOutOfBoundsException if the row index is invalid (< 0 or >= row
   *                                   count)
   */
  public void setRowByValues(int row, List<String> values) {
    List<String> columns = getColumnNames();
    int columnCount = columns.size();
    if (values.size() != columnCount) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    // Set each value in its corresponding column
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      setValue(columns.get(columnIndex), row, values.get(columnIndex));
    }
  }

  /**
   * Adds a row of values (given as a list) to the DataFrame. Each value in the
   * list is inserted into the corresponding column in the order the columns were
   * created
   * 
   * @param values the {@link List} of {@link String} values to insert (one for
   *               each columns)
   * @throws IllegalArgumentException if the number of values does not match the
   *                                  number of columns
   */
  public void addRowByValues(List<String> values) {
    List<String> columns = getColumnNames();
    int columnCount = columns.size();
    if (values.size() != columnCount) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    // Add each value to its corresponding column
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      addValue(columns.get(columnIndex), values.get(columnIndex));
    }
  }

  /**
   * Deletes a row of values in the DataFrame.
   * 
   * @param row the zero-based index of the row to delete
   * @throws IndexOutOfBoundsException if the row index is invalid (< 0 or >= row
   *                                   count)
   */
  public void deleteRow(int row) {
    List<String> columns = getColumnNames();
    for (String name : columns) {
      columnMap.get(name).deleteRowValue(row);
    }
  }

  // Private methods

  /**
   * Returns the corresponding {@link Column} object for the input name.
   * 
   * @param columnName the name of the column to retrieve
   * @return the {@link Column} associated with the specified name
   * @throws IllegalArgumentException if no column with the specified name exists
   */
  private Column getColumnInternal(String columnName) {
    Column column = columnMap.get(columnName);
    if (column == null) {
      throw new IllegalArgumentException("No column named: " + columnName);
    }
    return column;
  }

  /**
   * Adds a given value to the end of a specified column. Kept private to ensure
   * rows can only be added via addRowByValues, ensuring the columns have uniform
   * size.
   * 
   * @param columnName the name of the column
   * @param value      the value to insert
   * @throws IllegalArgumentException if the column does not exist
   */
  private void addValue(String columnName, String value) {
    getColumnInternal(columnName).addRowValue(value);
  }
}