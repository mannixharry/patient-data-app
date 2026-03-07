package uk.ac.ucl.model;

import java.util.List;

public record JSPTable (
  List<String> names,
  List<List<String>> columns
) {}