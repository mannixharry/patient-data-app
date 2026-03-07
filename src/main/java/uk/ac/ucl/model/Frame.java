package uk.ac.ucl.model;

import java.util.List;

public interface Frame {
  /*
  Compile the Frame's data into a JSP-friendly data structure
   */
  List<String> getColumnNames();
  List<String> getColumnValues(String columnName);
  JSPTable toJSPTable();
}