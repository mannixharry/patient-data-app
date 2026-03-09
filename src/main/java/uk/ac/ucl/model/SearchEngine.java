package uk.ac.ucl.model;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class SearchEngine {

  /* 
  Search for rows in a Frame where the value in the searchColumn matches the keyword
  Option to use regex
  Returns a DataFrameView of the matching rows 
   */

  public DataFrameView search(DataFrameView view, String searchColumn, String searchTerm, boolean useRegex)
  {
    Pattern pattern = buildPattern(searchTerm, useRegex);

    List<Integer> filteredIndices = new ArrayList<>();
    List<String> columnValues = view.getColumnValues(searchColumn);
    for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
      String value = columnValues.get(rowIndex);
      if (pattern.matcher(value).find()) {
        filteredIndices.add(rowIndex);
      }
    }

    return new DataFrameView(view, filteredIndices);
  }

  public DataFrameView searchAll(DataFrameView view, String searchTerm, boolean useRegex)
  {
    Pattern pattern = buildPattern(searchTerm, useRegex);

    List<Integer> filteredIndices = new ArrayList<>();
    for (String searchColumn : view.getColumnNames()) {
      List<String> columnValues = view.getColumnValues(searchColumn);
      for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
        String value = columnValues.get(rowIndex);
        if (pattern.matcher(value).find()) {
          filteredIndices.add(rowIndex);
        }
      }
    }
    return new DataFrameView(view, filteredIndices);
  }
  

  private Pattern buildPattern(String searchTerm, boolean useRegex) 
  {
    if (useRegex) {
      return Pattern.compile(searchTerm);
    } else {
      return Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE);
    }
  }
}