package uk.ac.ucl.model;

/**
 * Handles pagination of a {@link DataFrameView} object. Constructed via a
 * reference to
 * a DataFrameView object and then can be used to generate pages.
 */
public class PagedView {

  private int pageSize;
  private DataFrameView sourceView;
  private int currentPage = 0;

  /**
   * Constructor attaches the sourceView to the newly instantiated PagedView.
   * 
   * @param sourceView the {@link DataFrameView} that the PagedView references
   * @param pageSize   the number of rows to include on each page
   */
  public PagedView(DataFrameView sourceView, int pageSize) {
    this.sourceView = sourceView;
    this.pageSize = pageSize;
  }

  /**
   * Updates the {@link DataFrameView} that the PagedView references.
   * 
   * @param newView the new {@link DataFrameView object} to base paging on
   */
  public void updateSourceView(DataFrameView newView) {
    sourceView = newView;
    if (currentPage >= getTotalPages()) {
      // Update current page
      currentPage = Math.max(0, getTotalPages() - 1);
    }
  }

  /**
   * Returns the total number of pages
   * 
   * @return page count
   */
  public int getTotalPages() {
    return (int) Math.ceil((double) sourceView.getRowCount() / pageSize);
  }

  /**
   * Returns a view to the current page
   * 
   * @return a {@link DataFrameView} object of the current page
   */
  public DataFrameView getCurrentPageView() {
    int start = pageSize * currentPage;
    int end = Math.min(start + pageSize, sourceView.getRowCount());
    return sourceView.restrictToRowRange(start, end);
  }

  /**
   * Sets the current page to a value specified by an input page index.
   * 
   * @param pageIndex the zero-indexed page number to change page to
   */
  public void setCurrentPage(int pageIndex) {
    if (0 <= pageIndex && pageIndex < getTotalPages())
      this.currentPage = pageIndex;
  }

  /**
   * Getter for the value of the current page index.
   * 
   * @return the current page
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * Getter for the value of the current page size.
   * 
   * @return the page size
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * Setter for the page size. Sets the page size to that specified by an input.
   * Ensures page size can never be set to a number less than 1.
   * @param size the new page size to set
   */
  public void setPageSize(int size) {
    pageSize = Math.max(1, size);
    currentPage = Math.max(0, getTotalPages() - 1);
  }
}
