package uk.ac.ucl.model;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataLoader {

  private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

  private void addRow(DataFrame df, CSVRecord record) {
    // Default "" for missing data

    List<String> row = new ArrayList<String>();
    List<String> headers = df.getColumnNames();

    for (String header : headers) {
      boolean mapped = record.isMapped(header);
      String value = mapped ? record.get(header).trim() : "";
      if (!mapped) {
        logger.fine("Missing value for column: " + header + " at row: " + record.getRecordNumber());
      }
      row.add(value);
    }
    df.addRowByValues(row);
  }

  private void parseCsv(DataFrame df, CSVParser csvParser, Path pathToCsv) {
    List<String> headers = csvParser
        .getHeaderNames()
        .stream()
        .map(String::trim)
        .toList();

    if (headers.isEmpty()) {
      logger.warning("CSV file has no headers: " + pathToCsv);
      return;
    }

    headers.forEach(header -> df.addColumn(header));

    for (CSVRecord record : csvParser) {
      addRow(df, record);
    }
    logger.info("Loaded " + df.getRowCount() + " rows from " + pathToCsv);
  }

  private void parseJSON(DataFrame df, Reader reader, Path pathToJson) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(reader);
    Iterator<String> rowIterator = rootNode.fieldNames();

    // Get first row to define columns
    if (!rowIterator.hasNext())
      return;

    JsonNode firstRowNode = rootNode.get(rowIterator.next());
    firstRowNode.fieldNames().forEachRemaining(df::addColumn);
    List<String> firstRow = df.getColumnNames().stream().map(name -> {
      JsonNode valueNode = firstRowNode.get(name);
      return valueNode != null ? valueNode.asText() : "";
    }).toList();
    df.addRowByValues(firstRow);
    
    while (rowIterator.hasNext()) {
      String rowIndex = rowIterator.next();
      JsonNode rowNode = rootNode.get(rowIndex);
      List<String> row = df.getColumnNames().stream().map(name -> {
        JsonNode valueNode = rowNode.get(name);
        return valueNode != null ? valueNode.asText() : "";
      }).toList();
      df.addRowByValues(row);
    }
  }

  public DataFrame load(Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }

    boolean isPathToCsv = path.toString().toLowerCase().endsWith(".csv");
    boolean isPathToJson = path.toString().toLowerCase().endsWith(".json");

    if (!isPathToCsv && !isPathToJson) {
      throw new IllegalArgumentException("Path must be to a CSV or JSON file");
    }

    DataFrame df = new DataFrame();

    try (BufferedReader reader = Files.newBufferedReader(path)) {
      if (isPathToCsv) {
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        parseCsv(df, csvParser, path);
      } else if (isPathToJson) {
        parseJSON(df, reader, path);
      }
    } catch (FileNotFoundException e) {
      logger.warning("CSV file not found: " + path);
    } catch (IllegalArgumentException e) {
      logger.log(Level.SEVERE, "Improperly formatted JSON / CSV file: " + path, e);
    } catch (IOException e) {
      // This handles file not found / cannot read etc
      logger.log(Level.SEVERE, ("Failed to read JSON / CSV file: " + path), e);
      // Goes on to return an empty data frame instead of crashing
    }
    return df;
  }
}