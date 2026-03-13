package uk.ac.ucl.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.stream.Stream;

public final class DirectoryScanner {
  public static List<String> getCsvAndJsonFiles(String directoryPath) {
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
      return List.of(); // Return empty if there is an error
    }
  }
}