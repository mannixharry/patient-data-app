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

public class TableExporter {

  private final TableData table;

  public TableExporter(TableData table) {
    this.table = table;
  }

  private String escapeCSV(String value) {
    // Avoid user-entered data being interpreted as a CSV 'special' token
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return ("\"" + value.replace("\"", "\"\"") + "\"");
      // CSV uses " as the escape character for " in a record so we wrap the string
      // with " ... "
      // and add the escape character for each "
    } else {
      return value;
    }
  }

  public void writeCSV(Writer writer) throws IOException {

    List<String> names = table.getNames();
    List<List<String>> columns = table.getColumns();
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

  public void toCSV(Path pathToCsv) throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(pathToCsv);
    writeCSV(writer);
  }

  public void writeJSON(Writer writer) throws IOException {
    List<String> names = table.getNames();
    List<List<String>> columns = table.getColumns();

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

  public void toJSON(Path pathToJson) throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(pathToJson);
    writeJSON(writer);
  }
}