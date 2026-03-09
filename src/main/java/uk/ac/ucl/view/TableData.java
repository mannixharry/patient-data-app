package uk.ac.ucl.view;

import uk.ac.ucl.model.DataFrameView;
import java.util.List;
import java.util.ArrayList;

public class TableData {

  private final List<String> names; 
  private final List<List<String>> columns;

  public TableData(List<String> names, List<List<String>> columns) {
    this.names = names;
    this.columns = columns;
  }

  public static TableData fromView(DataFrameView view)
  {
    List<String> names = view.getColumnNames();
    List<List<String>> columns = new ArrayList<>(names.size());

    for (String columnName : names) {
      columns.add(view.getColumnValues(columnName));
    }
    return new TableData(names, columns);
  }

  public List<String> getNames() {return names;}

  public List<List<String>> getColumns() {return columns;}
}