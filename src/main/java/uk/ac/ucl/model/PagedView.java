package uk.ac.ucl.model;

public class PagedView {
  
  private int pageSize;
  private DataFrameView sourceView;
  private int currentPage = 0;
  
  public PagedView(DataFrameView sourceView, int pageSize) {
    this.sourceView = sourceView;
    this.pageSize = pageSize;
  }

  public void updateSourceView(DataFrameView newView) {
    sourceView = newView;
    if (currentPage >= getTotalPages()) {
      currentPage = Math.max(0, getTotalPages()-1);
    }
  }

  public int getTotalPages() {
    return (int) Math.ceil((double) sourceView.getRowCount() / pageSize);
  }

  public DataFrameView getCurrentPageView() {
    int start = pageSize * currentPage;
    int end = Math.min(start + pageSize, sourceView.getRowCount());
    return sourceView.restrictToRowRange(start, end);
  }

  public void setCurrentPage(int pageIndex) {
    if (0 <= pageIndex && pageIndex < getTotalPages())
    this.currentPage = pageIndex;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageSize() {
    return pageSize; 
  }

  public void setPageSize(int size) {
    pageSize = Math.max(1, size);
    currentPage = Math.max(0,getTotalPages()-1);
  }
}

