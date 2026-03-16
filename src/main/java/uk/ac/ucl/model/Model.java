package uk.ac.ucl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main class for the Model in MVC. Provides an API to interact with the model.
 */
public class Model {

  private final DataLoader dataLoader = new DataLoader();
  private final SearchEngine searchEngine = new SearchEngine();
  private final SortEngine sortEngine = new SortEngine();
  private final ChartEngine chartEngine = new ChartEngine(25);

  private DataFrame df; // The main DataFrame
  private DataFrameView view; // The main view to the DataFrame
  private PagedView pagedView; // A paged view of the current view

  private Path path; // Path to the currently loaded file
  private String key; // Primary key for DataFrame (if there is one)

  /**
   * Constructs a model object. Loads the file at the input path into the model's
   * storage.
   * 
   * @param path the path to the JSON/CSV file to load into the model
   */
  public void load(Path path) {
    this.df = dataLoader.load(path);
    this.view = DataFrameView.fullView(df);
    this.pagedView = new PagedView(view, 50);
    this.path = path;
  }

  /**
   * Get the path to the file which the model's {@link DataFrame} is loaded from
   * 
   * @return the file path
   */
  public Path getPath() {
    return path;
  }

  /**
   * Returns the primary key for the DataFrame
   * 
   * @return the {@link String} primary key value
   */
  public String getKeyName() {
    return key;
  }

  /**
   * Sets the primary key for the DataFrame to a given value
   * 
   * @param key the new value of the key
   * @throws IllegalArgumentException if the key is invalid (ie not all values in
   *                                  column are unique)
   */
  public void setKey(String key) {
    if (checkValidKey(key)) {
      this.key = key;
    } else {
      throw new IllegalArgumentException("Invalid key: " + key);
    }
  }

  /**
   * Checks if a column is valid as a primary key. A key is valid if all entries
   * in its columns are unique.
   * 
   * @param key the name of the column in the model's {@link DataFrame} to check
   * @return true if the key field is unique; false if it is not
   */
  public boolean checkValidKey(String key) {
    if (key == null || key.isEmpty()) {
      return true;
    }
    List<String> keyEntries = df.getColumnValues(key);
    // Determine if all keys are unique
    return keyEntries.size() == keyEntries.stream().distinct().count();
  }

  /**
   * Getter method for the model's {@link DataFrame}
   * 
   * @return the {@link DataFrame} object
   */
  public DataFrame getDf() {
    return df;
  }

  /**
   * Sets the values of a row in the underlying {@link DataFrame}
   * using a row index relative to the current {@link DataFrameView}.
   * 
   * @param row    the zero-based index in the current view
   * @param values the {@link List} of {@link String} values to set for each
   *               column
   * @throws IndexOutOfBoundsException if the row index is invalid in the view
   * @throws IllegalArgumentException  if the number of values does not match the
   *                                   number of columns
   */
  public void setRowThroughView(int row, List<String> values) {

    List<String> columns = df.getColumnNames();
    int columnCount = columns.size();
    if (values.size() != columnCount) {
      throw new IllegalArgumentException("Row size mismatch");
    }
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      df.setValue(columns.get(columnIndex), view.getRowIndexInDf(row), values.get(columnIndex));
    }
  }

  /**
   * Deletes a row in the underlying {@link DataFrame}
   * using a row index relative to the current {@link DataFrameView}.
   * @param row the zero-based index in the current view
   * @throws IndexOutOfBoundsException if the row index is invalid in the view
   */
  public void deleteRowThroughView(int row) {
    df.deleteRow(view.getRowIndexInDf(row));
    refreshRowView();
  }

  // DataFrameView related methods:

  /**
   * Returns the model's current view.
   * 
   * @return A {@link DataFrameView} object of the model's current view
   */
  public DataFrameView getView() {
    return view;
  }

  /**
   * Returns a {@link DataFrameView} that references every row and column in the
   * model's {@link DataFrame}.
   * 
   * @return the instantiated DataFrameView object
   */
  public DataFrameView getFullView() {
    return DataFrameView.fullView(df);
  }

  /**
   * Refreshes the model's view (to a full view) and updates the model's paging.
   */
  public void refreshView() {
    view = DataFrameView.fullView(df);
    updatePaging();
  }

  /**
   * Refreshes the view to the model's rows whilst keeping the current viewed column selection. 
   */
  public void refreshRowView() {
    List<String> savedNames = getView().getColumnNames();
    refreshView();
    restrictViewColumns(savedNames);
    updatePaging();
  }

  /**
   * Filters the model's view to only include a set of specified column names.
   * 
   * @param columnNames the {@link List} of {@link String} values of the column names
   */
  public void restrictViewColumns(List<String> columnNames) {
    view = view.restrictColumns(columnNames);
    pagedView.updateSourceView(view);
  }

  // Searching and Sorting:

  /**
   * Updates the model's view to only reference rows that include an input search
   * term in an input search column.
   * 
   * @param searchColumn the column within to search in. "ANY" includes all
   *                     columns
   * @param searchTerm   the term (substring) to search for
   */
  public void search(String searchColumn, String searchTerm) {
    if (searchColumn.equals("ANY")) {
      view = searchEngine.searchAll(view, searchTerm);
      updatePaging();

    } else {
      view = searchEngine.search(view, searchColumn, searchTerm);
      updatePaging();
    }
  }

  /**
   * Reorders the references in the model's view to sort the data according to an
   * input column. Provides the option to sort in ascending and descending order.
   * 
   * @param sortColumn the column to use to sort the data
   * @param ascending  if true then sort in ascending order; if false sort in
   *                   descending order
   */
  public void sort(String sortColumn, boolean ascending) {
    view = sortEngine.sort(view, sortColumn, ascending);
    updatePaging();
  }

  // Paging related methods:

  /**
   * Update the model's pagedView to match the current view.
   */
  public void updatePaging() {
    pagedView.updateSourceView(view);
  }

  /**
   * Returns the model's paged view.
   * 
   * @return the {@link PagedView} object
   */
  public PagedView getPagedView() {
    return pagedView;
  }

  // Chart related methods:

  /**
   * Returns a Map<String, Map<String, String>>. This represents a
   * Map<"ChartTitle", Map<"Category", "Value">>. Where "ChartTitle" is the name
   * of the chart, "Category" is a label (ie the name a of sector in a pie chart)
   * and "value" is the value associated with that category.
   * 
   * @return the chartData
   */
  public Map<String, Map<String, Integer>> getChartData() {
    Map<String, Map<String, Integer>> charts = chartEngine.getChartData(view);
    return charts;
  }

  /**
   * Returns the titles of the charts that should be rendered as histograms.
   * 
   * @return a {@link Set} of {@link String} values contains the titles of the
   *         histograms
   */
  public Set<String> getHistogramTitles() {
    return Set.of("Birth Year Histogram", "Age Histogram", "Cost Histogram");
  }
}
