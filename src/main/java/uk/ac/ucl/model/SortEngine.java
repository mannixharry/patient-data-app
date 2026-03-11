package uk.ac.ucl.model;

import java.util.List;
import java.util.ArrayList;

public class SortEngine {
  public DataFrameView sort(DataFrameView view, String sortColumn, boolean ascending) {
    /* 
    Sorts a DataFrame by a single column in ascending or descending order
    */
    List<String> columnValues = view.getColumnValues(sortColumn);
    int rowCount = columnValues.size();
    List<Integer> rowIndices = new ArrayList<>(rowCount);
    for (int i = 0; i < rowCount; i++) {
      rowIndices.add(i);
    }

    rowIndices.sort(
      (i1, i2) -> {
        String v1 = columnValues.get(i1);
        String v2 = columnValues.get(i2);
        int c = v1.compareTo(v2); // c is the result of the comparison
        return ascending ? c : -c;
      }
    );

    List<Integer> sortedIndices = List.copyOf(rowIndices);
    return new DataFrameView(view, sortedIndices, null);
  }
}