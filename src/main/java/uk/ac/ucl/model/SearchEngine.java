package uk.ac.ucl.model;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchEngine {

  /*
   * Search for rows in a Frame where the value in the searchColumn matches the
   * keyword
   * Option to use regex
   * Returns a DataFrameView of the matching rows
   */

  public DataFrameView search(DataFrameView view, String searchColumn, String searchTerm, boolean useRegex) {
    Pattern pattern = buildPattern(searchTerm, useRegex);

    List<Integer> filteredIndices = new ArrayList<>();
    List<String> columnValues = view.getColumnValues(searchColumn);
    for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
      String value = columnValues.get(rowIndex);
      if (pattern.matcher(value).find()) {
        filteredIndices.add(rowIndex);
      }
    }

    return new DataFrameView(view, filteredIndices, null);
  }

  public DataFrameView searchAll(DataFrameView view, String searchTerm, boolean useRegex) {
    Pattern pattern = buildPattern(searchTerm, useRegex);

    List<Integer> filteredIndices = new ArrayList<>();
    Set<Integer> filteredSet = new HashSet<>();

    for (String searchColumn : view.getColumnNames()) {
      List<String> columnValues = view.getColumnValues(searchColumn);
      for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
        String value = columnValues.get(rowIndex);
        if (pattern.matcher(value).find() && !filteredSet.contains(rowIndex)) {
          filteredIndices.add(rowIndex);
          filteredSet.add(rowIndex);
        }
      }
    }
    return new DataFrameView(view, filteredIndices, null);
  }

  private Pattern buildPattern(String searchTerm, boolean useRegex) {
    if (useRegex) {
      return Pattern.compile(searchTerm);
    } else {
      return Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE);
    }
  }
}