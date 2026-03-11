package uk.ac.ucl.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.stream.Stream;

public final class CsvDirectoryScanner {
  public static List<String> getCsvFiles(String directoryPath) {
    try(Stream<Path> stream = Files.list(Paths.get(directoryPath))) {
      return stream
        .filter(path -> path.getFileName().toString().endsWith(".csv"))
        .map(path -> path.getFileName().toString())
        .sorted()
        .toList();

    } catch (IOException e) {
      return List.of(); // Return empty if there is an error
    } 
  }
}