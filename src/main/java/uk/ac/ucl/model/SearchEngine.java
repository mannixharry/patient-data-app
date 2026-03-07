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
  public DataFrameView search(Frame frame, String searchColumn, String keyword, boolean useRegex)
  {
    Pattern pattern = buildPattern(keyword, useRegex);

    List<Integer> filteredIndices = new ArrayList<>();
    List<String> columnValues = frame.getColumnValues(searchColumn);
    for (int rowIndex = 0; rowIndex < columnValues.size(); rowIndex++) {
      String value = columnValues.get(rowIndex);
      if (pattern.matcher(value).find()) {
        filteredIndices.add(rowIndex);
      }
    }

    // Create a new DataFrameView of the original Frame's source DataFrame
    if (frame instanceof DataFrame df) {
      return new DataFrameView(df, filteredIndices);
    } else if (frame instanceof DataFrameView view) {
      return new DataFrameView(view, filteredIndices);
    } else {
      throw new IllegalArgumentException("Unsupported Frame type");
    }
  }
  private Pattern buildPattern(String keyword, boolean useRegex) 
  {
    if (useRegex) {
      return Pattern.compile(keyword);
    } else {
      return Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
    }
  }
}