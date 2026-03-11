package uk.ac.ucl.view;


import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;

public class TableExporter {

  private static final Logger logger = Logger.getLogger(TableExporter.class.getName());

  private final TableData table;

  public TableExporter(TableData table) {
    this.table = table; 
  }

  private String escapeCSV(String value) {
    // Avoid user-entered data being interpreted as a CSV 'special' token
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return ("\"" + value.replace("\"", "\"\"") + "\"");
      // CSV uses " as the escape character for " in a record so we wrap the string with " ... "
      // and add the escape character for each "
    } else {
      return value;
    }
  }

  public void toCSV(String pathToCsv)
  { 
    Path path = Paths.get(pathToCsv);
    try (BufferedWriter writer = Files.newBufferedWriter(path))
    {
      List<String> names = table.getNames();
      List<List<String>> columns = table.getColumns();
      int rowCount = columns.isEmpty() ? 0 : columns.get(0).size();
      int columnCount = names.size();
      
      StringBuilder headerString = new StringBuilder();
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
      {
        headerString.append(escapeCSV(names.get(columnIndex)));
        if (columnIndex < columnCount - 1) {
          headerString.append(",");
        }
      }
      headerString.append("\n");
      writer.write(headerString.toString());

      for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
        StringBuilder rowString = new StringBuilder();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
          String value = columns.get(columnIndex).get(rowIndex);
          rowString.append(escapeCSV(value));
          if (columnIndex < columnCount - 1) {
            rowString.append(",");
          }
        }
        rowString.append("\n");
        writer.write(rowString.toString());
      }
    }
    catch (IOException e) {
      logger.log(Level.SEVERE, ("Failed to create / edit CSV file: " + pathToCsv), e);
    }
  }
}