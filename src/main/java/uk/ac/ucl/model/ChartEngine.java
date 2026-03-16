package uk.ac.ucl.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

/**
 * Provides methods to process and convert data in an input
 * {@link DataFrameView} into a format that is ready to be displayed as a chart.
 */
public class ChartEngine {

  private final int maxSectors;

  /**
   * Constructor for ChartEngine.
   * 
   * @param maxPieChartSectors sets the greatest number of sectors that will
   *                           appear on any generated pie chart
   */
  public ChartEngine(int maxPieChartSectors) {
    maxSectors = Math.max(1, maxPieChartSectors);
  }

  /**
   * Returns a Map<String, Map<String, String>>. This represents a
   * Map<"ChartTitle", Map<"Category", "Value">>. Where "ChartTitle" is the name
   * of the chart, "Category" is a label (ie the name a of sector in a pie chart)
   * and "value" is the value associated with that category. Generates several
   * different types of charts (see code).
   * 
   * @return the chartData
   */
  public Map<String, Map<String, Integer>> getChartData(DataFrameView view) {
    Map<String, Map<String, Integer>> charts = new LinkedHashMap<>();

    // Setup title and corresponding column name map for basic pie charts
    Map<String, String> basicCharts = new LinkedHashMap<>();
    basicCharts.put("Gender Distribution", "GENDER");
    basicCharts.put("Marital Status Distribution", "MARITAL");
    basicCharts.put("Race Distribution", "RACE");
    basicCharts.put("Ethnicity Distribution", "ETHNICITY");
    basicCharts.put("State Distribution", "STATE");
    basicCharts.put("City Population Distribution", "CITY");
    basicCharts.put("Description Distribution", "DESCRIPTION");
    basicCharts.put("Reason-Description Distribution", "REASONDESCRIPTION");
    basicCharts.put("Encounter Class Distribution", "ENCOUNTERCLASS");
    basicCharts.put("Body Site Distribution", "BODYSITE_DESCRIPTION");
    basicCharts.put("Modality Distribution", "MODALITY_DESCRIPTION");
    basicCharts.put("SOP Distribution", "SOP_DESCRIPTION");
    basicCharts.put("Speciality Distribution", "SPECIALITY");

    // Before adding to charts, we check each column name required to generate the
    // chart is present. We store the logic for all the possible charts we could
    // draw, see which are possible and add those to the charts map

    // Process basic pie charts
    for (Map.Entry<String, String> entry : basicCharts.entrySet()) {
      String title = entry.getKey();
      String columnName = entry.getValue();
      if (view.hasColumn(columnName)) {
        charts.put(title, getValueCounts(view, columnName));
      }
    }

    // Special Alive vs Dead pie chart
    if (view.hasColumn("DEATHDATE")) {
      charts.put("Alive vs Dead Distribution", deadVsAlivePieChart(view));
    }

    // Birth Decade Histogram
    if (view.hasColumn("BIRTHDATE")) {
      charts.put("Birth Year Histogram", birthDecadeHistogram(view));
    }

    // Age Histogram
    if (view.hasColumn("BIRTHDATE") && view.hasColumn("DEATHDATE")) {
      charts.put("Age Histogram", ageHistogram(view));
    }

    // Cost Histogram
    if (view.hasColumn("COST")) {
      charts.put("Cost Histogram", costHistogram(view));
    }

    return charts;
  }

  // Private helper methods:

  // Converts a column of data (in a DataFrameView) to a map of (value, frequency)
  // pairs for Pie chart processing
  private Map<String, Integer> getValueCounts(DataFrameView view, String columnName) {
    Map<String, Integer> valueCounts = new LinkedHashMap<>();
    for (String value : view.getColumnValues(columnName)) {
      if (value == null || value.isBlank()) {
        value = "unknown";
      }
      valueCounts.merge(value, 1, Integer::sum);
    }
    return condensePieData(valueCounts);
  }

  private Map<String, Integer> birthDecadeHistogram(DataFrameView view) {
    // TreeMap sorts the histogram categories
    // We first build the categories (with numeric values)
    Map<Integer, Integer> numericCounts = new TreeMap<>();
    for (String dateString : view.getColumnValues("BIRTHDATE")) {
      if (dateString == null || dateString.isBlank()) {
        continue;
      }
      LocalDate date = LocalDate.parse(dateString);
      int decade = date.getYear() - (date.getYear() % 10);
      numericCounts.merge(decade, 1, Integer::sum);
    }
    // And then convert the numeric keys to strings, with the suffix "s"
    Map<String, Integer> result = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : numericCounts.entrySet()) {
      result.put(entry.getKey() + "s", entry.getValue());
    }
    return result;
  }

  private Map<String, Integer> ageHistogram(DataFrameView view) {
    // TreeMap sorts the histogram categories
    // We first build the categories (with numeric values)
    Map<Integer, Integer> numericCounts = new TreeMap<>();
    for (String dateString : view.getColumnValues("BIRTHDATE")) {
      if (dateString == null || dateString.isBlank()) {
        continue;
      }
      LocalDate birthDate = LocalDate.parse(dateString);
      int age = Period.between(birthDate, LocalDate.now()).getYears();
      int decade = age - (age % 10);
      numericCounts.merge(decade, 1, Integer::sum);
    }

     // And then convert the numeric keys to strings, with the suffix "s"
    Map<String, Integer> result = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : numericCounts.entrySet()) {
      result.put(entry.getKey() + "s", entry.getValue());
    }
    return result;
  }

  // Helper method to assist in generating a histogram for numeric data (of unknown classes)
  private Map<String, Integer> classifyNumericData(List<Double> data, int classCount, String format) {

    Map<String, Integer> result = new LinkedHashMap<>();
    if (data.isEmpty()) {
      return result;
    }

    // Find min, max and class width of the data
    // classCount is the number of classes
    double min = data.stream().min(Double::compare).get();
    double max = data.stream().max(Double::compare).get();
    double width = (max - min) / classCount;

    // Add the class labels, marking classCount regular intervals of length width from min to max. 
    List<String> labels = new ArrayList<>();
    for (int i = 0; i < classCount; i++) {
      double start = min + i * width;
      double end = start + width;
      String classLabel = String.format(format, start, end);
      result.put(classLabel, 0);
      labels.add(classLabel);
    }

    // Iterate through the data and increment the frequency associated with the class that each data item falls into
    for (double value : data) {
      int classIndex = (int) ((value - min) / width);
      if (classIndex == classCount)
        classIndex--;
      String label = labels.get(classIndex);
      result.merge(label, 1, Integer::sum);
    }

    return result;
  }

  private Map<String, Integer> costHistogram(DataFrameView view) {
    List<Double> costs = view.getColumnValues("COST").stream().filter(s -> s != null && !s.isBlank())
        .map(Double::parseDouble).toList();
    return classifyNumericData(costs, maxSectors, "£%.2f - £%.2f");
  }

  private Map<String, Integer> deadVsAlivePieChart(DataFrameView view) {
    Map<String, Integer> valueCounts = new LinkedHashMap<>();
    for (String deathDate : view.getColumnValues("DEATHDATE")) {
      if (deathDate == null || deathDate.isBlank()) {
        valueCounts.merge("alive", 1, Integer::sum);
      } else {
        valueCounts.merge("dead", 1, Integer::sum);
      }
    }
    return condensePieData(valueCounts);
  }

  // Sort the Pie Chart data, and group the classes not in the top 'maxPieChartSectors'.
  private Map<String, Integer> condensePieData(Map<String, Integer> valueCounts) {
    //Sort by values (the frequency) in descending order
    List<Map.Entry<String, Integer>> sorted = new ArrayList<>(valueCounts.entrySet());
    sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

    Map<String, Integer> condensed = new LinkedHashMap<>();
    int otherCount = 0;
    for (int i = 0; i < sorted.size(); i++) {
      Map.Entry<String, Integer> entry = sorted.get(i);
      if (i < maxSectors - 1) {
        // In top 'maxPieChartSectors' -> display the category
        condensed.put(entry.getKey(), entry.getValue());
      } else {
        // Otherwise group into "other" category
        otherCount += entry.getValue();
      }
    }
    if (otherCount > 0) {
      condensed.put("Other", otherCount);
    }
    return condensed;
  }
}