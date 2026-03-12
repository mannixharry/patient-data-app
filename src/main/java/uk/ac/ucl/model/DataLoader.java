package uk.ac.ucl.model;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataLoader {
  
  private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

  private void validatePath(Path pathToCsv) 
  {
    if (pathToCsv == null) {
      throw new IllegalArgumentException("Path to CSV cannot be null");
    }
  }

  private void addRow(DataFrame df, CSVRecord record)
  {
    // Default "" for missing data

    List<String> row = new ArrayList<String>();
    List<String> headers = df.getColumnNames();

    for (String header : headers) 
    {
      boolean mapped = record.isMapped(header);
      String value = mapped ? record.get(header).trim() : "";
      if (!mapped) {
        logger.fine("Missing value for column: " + header + " at row: " + record.getRecordNumber());
      }
      row.add(value);
    }
    df.addRowByValues(row);
  }

  private void parseCsv(DataFrame df, CSVParser csvParser, Path pathToCsv)
  {
    List<String> headers = csvParser
      .getHeaderNames()
      .stream()
      .map(String::trim)
      .toList();

    if (headers.isEmpty())
    {
      logger.warning("CSV file has no headers: " + pathToCsv);
      return;
    }

    headers.forEach(header -> df.addColumn(header));

    for (CSVRecord record : csvParser)
    {
      addRow(df, record);
    }
    logger.info("Loaded " + df.getRowCount() + " rows from " + pathToCsv);
  }

  public DataFrame load(Path pathToCsv)
  {
    validatePath(pathToCsv);
    DataFrame df = new DataFrame();
    try (
      BufferedReader reader = Files.newBufferedReader(pathToCsv); 
      CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
    ) { 
        parseCsv(df, csvParser, pathToCsv);
      }
    catch (FileNotFoundException e) {
      logger.warning("CSV file not found: " + pathToCsv);
    }
    catch (IllegalArgumentException e) {
      logger.log(Level.SEVERE, "Improperly formatted CSV file: " + pathToCsv, e);
    }
    catch (IOException e) {
      // This handles file not found / cannot read etc
      logger.log(Level.SEVERE, ("Failed to read CSV file: " + pathToCsv), e);
      // Goes on to return an empty data frame instead of crashing 
    }
    return df;
  }
}