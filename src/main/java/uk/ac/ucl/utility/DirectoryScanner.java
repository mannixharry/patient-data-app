package uk.ac.ucl.utility;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for scanning directories and retrieving files. Works with CSV and JSON.
 */
public final class DirectoryScanner {

  private static final Logger LOGGER = Logger.getLogger(DirectoryScanner.class.getName());

  /**
   * Scans the input directory and returns a sorted list of filenames ending in .csv or .json 
   * @param directoryPath the path to the directory to scan
   * @return a sorted list of matching file names, or an empty list if an error occurs
   */
  public static List<String> getCsvAndJsonFiles(String directoryPath) {

    if (directoryPath == null || directoryPath.isBlank()) {
      throw new IllegalArgumentException("directoryPath cannot be left null or blank");
    }

    Path directory = Paths.get(directoryPath);
    if (!Files.isDirectory(directory)) {
      throw new IllegalArgumentException("Not a valid directory: " + directoryPath);
    }

    try (Stream<Path> stream = Files.list(Paths.get(directoryPath))) {
      return stream
          .filter(path -> {
            String name = path.getFileName().toString().toLowerCase();
            return name.endsWith(".csv") || name.endsWith(".json");
          })
          .map(path -> path.getFileName().toString())
          .sorted()
          .toList();

    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to scan directory: " + directoryPath, e);
      return List.of(); // Return empty if there is an error
    }
  }
}