package uk.ac.ucl.view;

import uk.ac.ucl.model.DataFrameView;
import java.util.List;
import java.util.ArrayList;

/**
 * A JSP-friendly snapshot of tabular data extracted from a
 * {@link DataFrameView}. Stores column names and their corresponding value
 * lists. 
 */
public record TableData(List<String> names, List<List<String>> columns) {

  /** Creates a {@link TableData} from the given {@link DataFrameView} 
   * 
   * @param view the data frame view to read from
   * @return a new TableData object containing the view's data
   */
  public static TableData fromView(DataFrameView view) {
    List<String> names = view.getColumnNames();
    List<List<String>> columns = new ArrayList<>(names.size());

    for (String columnName : names) {
      columns.add(view.getColumnValues(columnName));
    }
    return new TableData(names, columns);
  }
}