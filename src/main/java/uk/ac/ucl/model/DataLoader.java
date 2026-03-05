package uk.ac.ucl.model;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class DataLoader {
  
  private static final Logger logger = Logger.getLogger(DataLoader.class.getName());

  private void validatePath(String pathToCsv) 
  {
    if (pathToCsv == null || pathToCsv.isBlank()) {
      throw new IllegalArgumentException("Path to CSV cannot be null / empty");
    }
  }

  private void addRow(DataFrame df, CSVRecord record, String header)
  {
    // Default "" for missing data
    if (record.isMapped(header)) {
      df.addValue(header, record.get(header).trim());
    } else {
      logger.fine("Missing value for column: " + header + " at row: " + record.getRecordNumber());
      df.addValue(header, "");
    }
  }

  private void parseCsv(DataFrame df, CSVParser csvParser, String pathToCsv)
  {
    List<String> headers = csvParser
      .getHeaderNames()
      .stream()
      .map(String::trim)
      .toList();

    if (headers.isEmpty())
    {
      logger.warning("CSV file has no headers: " + pathToCsv);
    }

    headers.forEach(header -> df.addColumn(header));

    for (CSVRecord record : csvParser)
    {
      for (String header : headers)
      {
        addRow(df, record, header);
      }
    }
    logger.info("Loaded " + df.getRowCount(headers.get(0)) + " rows from " + pathToCsv);
  }

  public DataFrame load(String pathToCsv)
  {
    validatePath(pathToCsv);
    DataFrame df = new DataFrame();
    try (
      Reader reader = new FileReader(pathToCsv); 
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