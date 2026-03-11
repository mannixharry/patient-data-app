package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class ViewEngine {
  // At the moment I trust the programmer to give a sensible input 

  public DataFrameView restrictRows(DataFrameView view, List<Integer> rowIndices)
  {
    return new DataFrameView(view, rowIndices, null);
  }

  public DataFrameView restrictToRowRange(DataFrameView view, int start, int end)
  {
    List<Integer> restrictedRows = new ArrayList<>(end-start);
    for (int i=start; i<end; i++) {
      restrictedRows.add(i);
    }
    return new DataFrameView(view, restrictedRows, null);
  }

  public DataFrameView restrictColumns(DataFrameView view, List<String> columnNames) 
  {
    List<Integer> restrictedColumns = new ArrayList<>();
    List<String> viewNames = view.getColumnNames();
    for (String name : columnNames) {
      int index = viewNames.indexOf(name);
      if (index != -1) {restrictedColumns.add(index);}
    }
    return new DataFrameView(view, null, restrictedColumns);
  }
}
