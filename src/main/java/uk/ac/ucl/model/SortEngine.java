package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains methods to re-arrange a DataFrameView's references
 * according to given sorting criteria.
 */
public class SortEngine {
  /**
   * Sorts a {@link DataFrameView} by a single column in ascending or descending
   * order.
   * 
   * @param view       the {@link DataFrameView} to sort
   * @param sortColumn the column in the view to sort by
   * @param ascending  if true then sort in ascending order; if false then sort in
   *                   descending order
   * @return a {@link DataFrameView} to the sorted lsit
   */
  public DataFrameView sort(DataFrameView view, String sortColumn, boolean ascending) {

    List<String> columnValues = view.getColumnValues(sortColumn);
    int rowCount = columnValues.size();
    List<Integer> rowIndices = IntStream.range(0, rowCount).boxed().collect(Collectors.toCollection(ArrayList::new));
    boolean isNumeric = isNumericColumn(columnValues);
    rowIndices.sort(
        (i1, i2) -> {
          String v1 = columnValues.get(i1);
          String v2 = columnValues.get(i2);
          int c;
          if (isNumeric) {
            c = Double.compare(parseDouble(v1), parseDouble(v2));
          } else {
            c = v1.compareTo(v2);
          } // c is the result of the comparison
          return ascending ? c : -c;
        });

    List<Integer> sortedIndices = List.copyOf(rowIndices);
    return new DataFrameView(view, sortedIndices, null);
  }

  // Validates if every element of a column is numeric
  private boolean isNumericColumn(List<String> values) {
    for (String value : values) {
      if (value != null && !value.isEmpty()) {
        try {
          Double.parseDouble(value);
        } catch (NumberFormatException e) {
          return false;
        }
      }
    }
    return true;
  }

  // Ensures null values do not break numeric columns' sorting
  private double parseDouble(String value) {
    if (value == null || value.isEmpty()) {
      return Double.NEGATIVE_INFINITY;
    }
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return Double.NEGATIVE_INFINITY;
    }
  }
}