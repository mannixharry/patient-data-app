<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
  String pageMode = (String) request.getAttribute("pageMode");
  if ("save".equals(pageMode)) {

  TableData table = (TableData) request.getAttribute("table");
  String path = (String) request.getAttribute("path");

  String confirmation = request.getParameter("confirmation");

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
  <form action="/saveFile" method="get" class = "inline-form">
    <span class="info">Confirm: Save <%=path%></span>
    <button class="small-button" type="submit" name="confirmation" value="yes">Yes</button>
  </form>
  <form action="/main" method="get" class="inline-form">
    <button aciton="/main" method="get" type="submit" class="small-button">No</button>
  </form>
</div>

<%-- Show live-feedback: opened file name; chosen key + 'Back' button --%>
<% if (confirmation != null && !confirmation.isEmpty()) { %>
</div>
  <div class="button-row">
    <span class="info">Saved <%=rowCount%> rows to <%=path%> </span>
  </div>
<% } %>
<% } %>