package uk.ac.ucl.model;

import java.util.List;
import java.util.ArrayList;

import java.util.stream.IntStream;

/**
 * A DataFrameView represents a filtered or partial view of a {@link DataFrame}
 * Instead of storing its own data, it maintains a list of row indices that
 * reference rows (and columns) in the source {@link DataFrame}.
 * This is probably one of the most important classes in the project.
 * It is useful for searching, sorting, paging and changing displayed columns.
 * It provides read-only access to the selected rows and columns - to mitigate
 * the risk of accidentally modifying data
 */
public class DataFrameView {

  // The DataFrame that the DataFrameView refers to
  private final DataFrame source;

  // References to rows and columns in the source DataFrame are stored as lists of
  // indices
  private final List<Integer> rowIndices;
  private final List<Integer> columnIndices;

  // DataFrameView constructors:

  /**
   * Constructs a DataFrameView that refers to an input source {@link DataFrame},
   * with the exact row and column references given as lists of integers.
   * 
   * @param sourceDf      the DataFrame object to create a view to
   * @param rowIndices    a {@link List} of {@link Integer} values that are the
   *                      indices of the rows in the source {@link DataFrame} that
   *                      the DataFrameView refers to. If left null then it refers
   *                      to all rows
   * @param columnIndices also a {@link List} of {@link Integer} values but stores
   *                      the references to the columns in the source
   *                      {@link DataFrame}. If left null then it refers to all
   *                      columns
   */
  public DataFrameView(DataFrame sourceDf, List<Integer> rowIndices, List<Integer> columnIndices) {
    this.source = sourceDf;
    this.rowIndices = List.copyOf(defaultRowIndices(rowIndices));
    this.columnIndices = List.copyOf(defaultColumnIndices(columnIndices));
  }

  /**
   * Overloaded constructor allows a DataFrameView to be instantiated that refers
   * to (logically) another DataFrameView.
   * The exact row and column references in this source DataFrameView are given as
   * lists of integers.
   * This allows for 'view chaining' - useful for repeated search operation
   * 
   * @param sourceView    the DataFrameView object to create a view to
   * @param rowIndices    a {@link List} of {@link Integer} values that are the
   *                      indices of the rows in the source DataFrameView that the
   *                      new DataFrameView refers to. If left null then it refers
   *                      to all rows
   * @param columnIndices also a {@link List} of {@link Integer} values but stores
   *                      the references to the columns in the source
   *                      DataFrameView. If left null then it refers to all
   *                      columns
   */
  public DataFrameView(DataFrameView sourceView, List<Integer> rowIndices, List<Integer> columnIndices) {
    // The DataFrameView inherits the sourceView's own source DataFrame
    this.source = sourceView.getSource();

    List<Integer> sourceRows = sourceView.rowIndices;
    List<Integer> sourceColumns = sourceView.columnIndices;

    // The input references are references to the sourceView's own references to the
    // source DataFrame.
    // Here we compact the logical chain of references to a single reference from
    // the current DataFrameView to the source DataFrame.
    this.rowIndices = List.copyOf(mapIndicesFromView(sourceRows, rowIndices));
    this.columnIndices = List.copyOf(mapIndicesFromView(sourceColumns, columnIndices));
  }

  // Static view operations:

  /**
   * Returns a {@link DataFrameView} that references every row and column in a
   * given DataFrame.
   * 
   * @param df the {@link DataFrame} to create a full view of
   * @return the instantiated DataFrameView object
   */
  public static DataFrameView fullView(DataFrame df) {
    return new DataFrameView(df, null, null);
  }

  /**
   * Returns a {@link DataFrameView} that references nothing.
   * 
   * @param df the {@link DataFrame} to create an empty view of
   * @return the instantiated DataFrameView object
   */
  public static DataFrameView emptyView(DataFrame df) {
    List<Integer> emptyIndices = new ArrayList<>();
    return new DataFrameView(df, emptyIndices, null);
  }

  // Getter methods:

  /**
   * Returns the {@link DataFrame} that the DataFrameView references
   * 
   * @return the source {@link DataFrame}
   */
  public DataFrame getSource() {
    return source;
  }

  /**
   * Returns the number of rows that the DataFrameView references
   * 
   * @return the number of rows
   */
  public int getRowCount() {
    return rowIndices.size();
  }

  /**
   * Returns true if the view references a column that matches an input name
   * 
   * @param columnName the column name to search for
   * @return true if the view references a column that matches the name; false if
   *         it does not
   */
  public boolean hasColumn(String columnName) {
    return getColumnNames().contains(columnName);
  }

  /**
   * Returns a list of the names of the columns that the DataFrameView references.
   * 
   * @return A {@link List} of {@link String} values that are the names of the
   *         referenced columns
   */
  public List<String> getColumnNames() {
    // Get the column names in the source
    List<String> sourceNames = source.getColumnNames();
    // Interpret them in the view
    List<String> viewNames = new ArrayList<>(columnIndices.size());
    for (int columnIndex : columnIndices) {
      viewNames.add(sourceNames.get(columnIndex));
    }
    return viewNames;
  }

  /**
   * Returns all entries of a specified column as a List<String>.
   * 
   * @param columnName the name of the column whose data is retrieved
   * @return a {@link List} of {@link String} values in the specified column
   * @throws IllegalArgumentException if the specified column does not exist
   */
  public List<String> getColumnValues(String columnName) {
    // Get the column values in the source
    List<String> sourceColumn = source.getColumnValues(columnName);
    // Interpret them in the view
    List<String> viewColumn = new ArrayList<>(rowIndices.size());
    for (int rowIndex : rowIndices) {
      viewColumn.add(sourceColumn.get(rowIndex));
    }
    return viewColumn;
  }

  /**
   * Returns all entries of a column specified by index as a List<String>.
   * 
   * @param columnIndex the index of the column whose data is retrieved
   * @return a {@link List} of {@link String} values in the specified column
   * @throws IllegalArgumentException if the specified column does not exist
   */
  public List<String> getColumnValuesByIndex(int columnIndex) {
    String columnName = getColumnNames().get(columnIndex);
    return getColumnValues(columnName);
  }

  /**
   * Returns the row index of the first occurrence of a given value in the
   * specified column in the context of the view
   * 
   * @param columnName the name of the column to search
   * @param value      the {@link String} value to search for
   * @return the zero-based index of the first matching row, or -1 if the value is
   *         not found
   * @throws IllegalArgumentException if the specified column does not exist
   */
  public int getRowIndexByValue(String columnName, String value) {
    List<String> column = getColumnValues(columnName);
    for (int i = 0; i < column.size(); i++) {
      if (column.get(i).equals(value)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Return a de-referenced row index
   * 
   * @param rowIndex the row index to de-reference
   * @return the row index in the source {@link DataFrame} that the input row
   *         index referenced
   */
  public int getRowIndexInDf(int rowIndex) {
    return rowIndices.get(rowIndex);
  }

  // View restriction methods:

  /**
   * Return a new DataFrameView that references all columns in the current view
   * but only a range of the rows, given by a start and end index.
   * 
   * @param start the lower bound of the range (inclusive)
   * @param end   the upper bound of the the range (exclusive)
   * @return the DataFrameView with restricted rows
   */
  public DataFrameView restrictToRowRange(int start, int end) {
    List<Integer> restrictedRows = IntStream.range(start, end).boxed().toList();
    return new DataFrameView(this, restrictedRows, null);
  }

  /**
   * Return a new DataFrameView that references all columns in the current view
   * but only a single row, given by an input index.
   * 
   * @param rowIndex the single row index to reference
   * @return the DataFrameView restricted to a single row
   */
  public DataFrameView restrictToRow(int rowIndex) {
    return new DataFrameView(this, List.of(rowIndex), null);
  }

  /**
   * Return a new DataFrameView that references all rows in the current view
   * but only a subset (specified by an input List<Integer>) of the columns.
   * 
   * @param columnIndices the {@link List} of {@link Integer} values that are the
   *                      indices of the columns to reference
   * @return the DataFrameView with restricted columns
   */
  public DataFrameView restrictColumns(List<String> columnNames) {
    List<Integer> restrictedColumns = columnNames.stream().map(name -> getColumnNames().indexOf(name))
        .filter(index -> index != -1).toList();
    return new DataFrameView(this, null, restrictedColumns);
  }

  // Private helper methods:

  // Returns the given list of indices, or a default range from 0 to the number of
  // rows in the source DataFrame if null. Filters indices that are out of range
  private List<Integer> defaultRowIndices(List<Integer> indices) {
    int rowCount = source.getRowCount();
    if (indices != null) {
      return indices.stream().filter(i -> i >= 0 && i < rowCount).toList();
    }
    // return full range [0, rowCount)
    return IntStream.range(0, rowCount).boxed().toList();
  }

  // Returns the given list of indices, or a default range from 0 to the number of
  // columns in the source DataFrame if null. Filters indices that are out of
  // range
  private List<Integer> defaultColumnIndices(List<Integer> indices) {
    int columnCount = source.getColumnNames().size();
    if (indices != null) {
      return indices.stream().filter(i -> i >= 0 && i < columnCount).toList();
    }
    // return full range [0, columnCount)
    return IntStream.range(0, columnCount).boxed().toList();
  }

  // De-references a list of indices toMap from source
  private List<Integer> mapIndicesFromView(List<Integer> source, List<Integer> toMap) {
    // If toMap is null, return all indices
    if (toMap == null) {
      return List.copyOf(source);
    }
    return toMap.stream().filter(i -> i >= 0 && i < source.size()).map(source::get).toList();
  }
}