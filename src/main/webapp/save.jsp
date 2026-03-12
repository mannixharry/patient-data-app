<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
  String pageMode = (String) request.getAttribute("pageMode");
  if ("save".equals(pageMode)) {

  TableData table = (TableData) request.getAttribute("table");
  String pathToCsv = (String) request.getAttribute("path");

  List<String> names = table.getNames();
  List<List<String>> columns = table.getColumns();
  
  // Validate retrieved table data
  boolean hasData = columns != null
  && !columns.isEmpty()
  && !names.isEmpty()
  && !columns.get(0).isEmpty();
  int columnCount = names.size();
  int rowCount = hasData ? columns.get(0).size() : 0;
%> 
  <div class="button-row">
    <span class="info">Saved <%=rowCount%> rows to <%=pathToCsv%> </span>
  </div>
<% } %>