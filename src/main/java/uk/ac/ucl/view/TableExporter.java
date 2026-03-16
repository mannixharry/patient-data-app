package uk.ac.ucl.view;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exports a {@link TableData} snapshot to CSV or JSON format.
 * Allows writing to an arbitrary {@link Writer} or directly to a file path.
 */
public class TableExporter {

  private final TableData table;

  /**
   * TableExporter constructor.
   * 
   * @param table the table data to export
   */
  public TableExporter(TableData table) {
    this.table = table;
  }

  /**
   * Escapes a value for safe inclusion in a CSV record, wrapping it in double
   * quotes if it contains commas, quotes or newlines
   */
  private String escapeCSV(String value) {
    // Avoid user-entered data being interpreted as a CSV 'special' token
    if (value == null) {
      return "";
    }
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return ("\"" + value.replace("\"", "\"\"") + "\"");
      // CSV uses " as the escape character for " in a record so we wrap the string
      // with " ... "
      // and add the escape character for each "
    } else {
      return value;
    }
  }

  /**
   * Writes the table as a CSV to the given writer.
   * 
   * @param writer the destination writer
   * @throws IOException if an IO error occurs
   */
  public void writeCSV(Writer writer) throws IOException {

    List<String> names = table.names();
    List<List<String>> columns = table.columns();
    int rowCount = columns.isEmpty() ? 0 : columns.get(0).size();
    int columnCount = names.size();

    String headerString = "";
    for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
      headerString += escapeCSV(names.get(columnIndex));
      if (columnIndex < columnCount - 1) {
        headerString += ",";
      }
    }
    headerString += "\n";
    writer.write(headerString);

    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      String rowString = "";
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        String value = columns.get(columnIndex).get(rowIndex);
        rowString += escapeCSV(value);
        if (columnIndex < columnCount - 1) {
          rowString += ",";
        }
      }
      rowString += "\n";
      writer.write(rowString);
    }
  }

  /**
   * Write the table as a CSV file to the given file path.
   * 
   * @param pathToCsv the destination file path
   * @throws IOException if an IO error occurs
   */
  public void toCSV(Path pathToCsv) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(pathToCsv)) {
      writeCSV(writer);
    }
  }

  /**
   * Writes the table as JSON to the given writer. (formatted nicely). Each row of
   * data has a key of its zero-based index.
   * 
   * @param writer the destination writer
   * @throws IOException if an IO error occurs
   */
  public void writeJSON(Writer writer) throws IOException {
    List<String> names = table.names();
    List<List<String>> columns = table.columns();

    int rowCount = columns.isEmpty() ? 0 : columns.get(0).size();
    int columnCount = names.size();

    Map<String, Map<String, String>> rowsByIndex = new LinkedHashMap<>();

    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      Map<String, String> row = new LinkedHashMap<>();
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        row.put(names.get(columnIndex), columns.get(columnIndex).get(rowIndex));
      }
      rowsByIndex.put(String.valueOf(rowIndex), row);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
    objectMapper.writeValue(writer, rowsByIndex);
  }

  /**
   * Writes the table as JSON to the given file path.
   * @param pathToJson the destination file path
   * @throws IOException if an IO error occurs
   */
  public void toJSON(Path pathToJson) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(pathToJson)) {
      writeJSON(writer);
    }
  }
}