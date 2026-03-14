package uk.ac.ucl.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

public class ChartEngine {

  private final int maxSectors;

  public ChartEngine(int maxPieChartSectors) {
    maxSectors = Math.max(1, maxPieChartSectors);
  }

  public Map<String, Map<String, Integer>> getChartData(DataFrameView view) {
    Map<String, Map<String, Integer>> charts = new LinkedHashMap<>();

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
    
    for (Map.Entry<String, String> entry : basicCharts.entrySet()) {
      String title = entry.getKey();
      String columnName = entry.getValue();
      if (view.hasColumn(columnName)) {
        charts.put(title, getValueCounts(view, columnName));
      }
    }

    if (view.hasColumn("DEATHDATE")) {
      charts.put("Alive vs Dead Distribution", deadVsAlivePieChart(view));
    }

    if (view.hasColumn("BIRTHDATE")) {
      charts.put("Birth Year Histogram", birthDecadeHistogram(view));
    }

    if (view.hasColumn("BIRTHDATE") && view.hasColumn("DEATHDATE")) {
      charts.put("Age Histogram", ageHistogram(view));
    }

    if (view.hasColumn("COST")) {
      charts.put("Cost Histogram", costHistogram(view));
    }

    return charts;
  }

  public Map<String, Integer> getValueCounts(DataFrameView view, String columnName) {
    Map<String, Integer> valueCounts = new LinkedHashMap<>();
    for (String value : view.getColumnValues(columnName)) {
      if (value == null || value.isBlank()) {
        value = "unkown";
      }
      valueCounts.merge(value, 1, Integer::sum);
    }
    return condensePieData(valueCounts);
  }

  public Map<String, Integer> birthDecadeHistogram(DataFrameView view) {
    Map<Integer, Integer> numericCounts = new TreeMap<>();
    for (String dateString : view.getColumnValues("BIRTHDATE")) {
      if (dateString == null || dateString.isBlank()) {
        continue;
      }
      LocalDate date = LocalDate.parse(dateString);
      int decade = date.getYear() - (date.getYear() % 10);
      numericCounts.merge(decade, 1, Integer::sum);
    }

    // Convert to strings keys with "s" ending
    Map<String, Integer> result = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : numericCounts.entrySet()) {
      result.put(entry.getKey() + "s", entry.getValue());
    }
    return result;
  }

  public Map<String, Integer> ageHistogram(DataFrameView view) {

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

    // Convert to strings keys with "s" ending
    Map<String, Integer> result = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : numericCounts.entrySet()) {
      result.put(entry.getKey() + "s", entry.getValue());
    }
    return result;
  }

  public Map<String, Integer> classifyNumericData(List<Double> data, int classCount, String format) {

    Map<String, Integer> result = new LinkedHashMap<>();
    if (data.isEmpty()) {
      return result;
    }

    double min = data.stream().min(Double::compare).get();
    double max = data.stream().max(Double::compare).get();
    double width = (max - min) / classCount;

    List<String> labels = new ArrayList<>();
    for (int i = 0; i < classCount; i++) {
      double start = min + i * width;
      double end = start + width;
      String classLabel = String.format(format, start, end);
      result.put(classLabel, 0);
      labels.add(classLabel);
    }

    for (double value : data) {
      int classIndex = (int) ((value - min) / width);
      if (classIndex == classCount)
        classIndex--;
      String label = labels.get(classIndex);
      result.merge(label, 1, Integer::sum);
    }

    return result;
  }

  public Map<String, Integer> costHistogram(DataFrameView view) {
    List<Double> costs = view.getColumnValues("COST").stream().filter(s -> s != null && !s.isBlank())
        .map(Double::parseDouble).toList();
    return classifyNumericData(costs, maxSectors, "£%.2f - £%.2f");
  }

  public Map<String, Integer> deadVsAlivePieChart(DataFrameView view) {
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

  public Map<String, Integer> condensePieData(Map<String, Integer> valueCounts) {
    List<Map.Entry<String, Integer>> sorted = new ArrayList<>(valueCounts.entrySet());
    sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    Map<String, Integer> condensed = new LinkedHashMap<>();
    int otherCount = 0;
    for (int i = 0; i < sorted.size(); i++) {
      Map.Entry<String, Integer> entry = sorted.get(i);
      if (i < maxSectors - 1) {
        condensed.put(entry.getKey(), entry.getValue());
      } else {
        otherCount += entry.getValue();
      }
    }
    if (otherCount > 0) {
      condensed.put("Other", otherCount);
    }
    return condensed;
  }
}