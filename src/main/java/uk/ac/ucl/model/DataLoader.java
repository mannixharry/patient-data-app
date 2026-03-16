package uk.ac.ucl.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * DataLoader provides methods to load a JSON or CSV file's contents into a
 * {@link DataFrame}
 */
public class DataLoader {

  // Set up logging for live feedback
  private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

  /**
   * Returns a {@link @DataFrame} constructed from data stored in a JSON/CSV file
   * specified by an input path.
   * 
   * @param path the path to the JSON/CSV file to load from
   * @return the newly instantiated {@link DataFrame}
   * @throws IllegalArgumentException if the input path is null / not to a JSON or
   *                                  CSV file
   */
  public DataFrame load(Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }

    // Set file-type flags for the input path
    boolean isPathToCsv = path.toString().toLowerCase().endsWith(".csv");
    boolean isPathToJson = path.toString().toLowerCase().endsWith(".json");

    if (!isPathToCsv && !isPathToJson) {
      throw new IllegalArgumentException("Path must be to a CSV or JSON file");
    }

    DataFrame df = new DataFrame();

    // Try-with-resources attempts to load file
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      if (isPathToCsv) {
        // Instantiate a CSVParser with a flag to indicate the first record of the CSV
        // file contains the column headers
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        parseCsv(df, csvParser, path);
      } else if (isPathToJson) {
        // parseJSON a BufferedReader directly
        parseJSON(df, reader, path);
      }
    } catch (NoSuchFileException e) {
      logger.warning("CSV file not found: " + path);
    } catch (IllegalArgumentException e) {
      logger.log(Level.SEVERE, "Improperly formatted JSON / CSV file: " + path, e);
    } catch (IOException e) {
      // This handles file not found / cannot read
      logger.log(Level.SEVERE, ("Failed to read JSON / CSV file: " + path), e);
      // Goes on to return an empty data frame instead of crashing
    }
    return df;
  }

  // Used by parseCsv. Adds a single CSVRecord (equivalent to a row) to the
  // DataFrame
  private void addRow(DataFrame df, CSVRecord record) {
    List<String> row = new ArrayList<String>();
    List<String> columnNames = df.getColumnNames();

    for (String name : columnNames) {
      boolean isMapped = record.isMapped(name);
      // Default "" for missing data
      String value = isMapped ? record.get(name).trim() : "";
      if (!isMapped) {
        logger.fine("Missing value for column: " + name + " at row: " + record.getRecordNumber());
      }
      row.add(value);
    }
    // Add the fully-formed row to the DataFrame
    df.addRowByValues(row);
  }

  // Opens a CSV file and creates a DataFrame of its data.
  private void parseCsv(DataFrame df, CSVParser csvParser, Path pathToCsv) {
    // Stream to get trimmed header names
    List<String> headers = csvParser.getHeaderNames().stream().map(String::trim).toList();

    if (headers.isEmpty()) {
      logger.warning("CSV file has no headers: " + pathToCsv);
      return;
    }

    // Add each header to the DataFrame
    headers.forEach(header -> df.addColumn(header));

    // Add each record to the DataFrame
    for (CSVRecord record : csvParser) {
      addRow(df, record);
    }

    logger.info("Loaded " + df.getRowCount() + " rows from " + pathToCsv);
  }

  // Opens a JSON file and creates a DataFrame of its data.
  private void parseJSON(DataFrame df, Reader reader, Path pathToJson) throws IOException {

    // Find the root node of the JSON file and create a row iterator
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(reader);
    Iterator<String> rowIterator = rootNode.fieldNames();

    // Protect against an empty file
    if (!rowIterator.hasNext())
      return;

    // Get first row to define the column names
    JsonNode firstRowNode = rootNode.get(rowIterator.next());
    // Add column names given in the first row to the DataFrame
    firstRowNode.fieldNames().forEachRemaining(df::addColumn);
    // Add the first row values to the DataFrame
    List<String> firstRow = df.getColumnNames().stream().map(name -> {
      JsonNode valueNode = firstRowNode.get(name);
      return valueNode != null ? valueNode.asText() : "";
    }).toList();
    df.addRowByValues(firstRow);

    // Repeat over remaining rows, adding to each to the DataFrame
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
}