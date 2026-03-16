package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains methods for limiting the view of a DataFrameView to only contain
 * rows that match search criteria.
 */
public class SearchEngine {

  /**
   * Returns a {@link DataFrameView} referencing rows in a given
   * {@link DataFrameView}
   * that match an input search term in an input search column.
   * 
   * @param view         the DataFrameView to search
   * @param searchColumn the column to search
   * @param searchTerm   the keyword to match
   * @return a {@link DataFrameView} referencing the matching columns
   */
  public DataFrameView search(DataFrameView view, String searchColumn, String searchTerm) {

    String term = searchTerm.toLowerCase();
    List<Integer> filteredIndices = new ArrayList<>();
    List<String> columnValues = view.getColumnValues(searchColumn);
    for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
      String value = columnValues.get(rowIndex);
        if (value != null && value.toLowerCase().contains(term)) {
        filteredIndices.add(rowIndex);
      }
    }
    return new DataFrameView(view, filteredIndices, null);
  }

  /**
   * Returns a {@link DataFrameView} referencing rows in a given
   * {@link DataFrameView}
   * that match an input search term in any column
   * 
   * @param view       the DataFrameView to search
   * @param searchTerm the keyword to match
   * @return a {@link DataFrameView} referencing the matching columns
   */
  public DataFrameView searchAll(DataFrameView view, String searchTerm) {

    String term = searchTerm.toLowerCase();
    List<Integer> filteredIndices = new ArrayList<>();
    Set<Integer> filteredSet = new HashSet<>();

    for (String searchColumn : view.getColumnNames()) {
      List<String> columnValues = view.getColumnValues(searchColumn);
      for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
        String value = columnValues.get(rowIndex);
        if (value != null && value.toLowerCase().contains(term) && !filteredSet.contains(rowIndex)) {
          filteredIndices.add(rowIndex);
          filteredSet.add(rowIndex);
        }
      }
    }
    return new DataFrameView(view, filteredIndices, null);
  }
}