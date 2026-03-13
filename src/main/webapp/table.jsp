
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>

<%
  // Fetch and unpack TableData from request
  TableData table = (TableData) request.getAttribute("table");
  String pageMode = (String) request.getAttribute("pageMode");
  String key = (String) request.getAttribute("key");

  int currentPage = (int) request.getAttribute("page");
  int pageSize = (int) request.getAttribute("pageSize");
  int pageTotal = (int) request.getAttribute("pageTotal");

  List<String> names = table.getNames();
  List<List<String>> columns = table.getColumns();
  
  // Validate retrieved table data
  boolean hasData = columns != null
  && !columns.isEmpty()
  && !names.isEmpty()
  && !columns.get(0).isEmpty();
  int columnCount = names.size();
  int rowCount = hasData ? columns.get(0).size() : 0;
  
  // Assumption: pageMode equal to 'edit' -> 'row' intialized
  Integer row = (Integer) request.getAttribute("row");
  // Display error message if the table is missing data
if (!hasData) { %>
<p class = "error-message">Error: No results</p>
<% } else { %>
<%-- Logic to display table --%>
<div class = "table-container">
  <table>
    <thead>
      <tr>
        <%-- Add headers to table attaching 'left-header' to the top left cell
        and 'top-header' to each header in the row --%>
        <th class="top-header left-header">Row</th>
        <% for (String name : names) { %>
        <th class="top-header"><%= name %></th>
        <% } %>
      </tr>
    </thead>
    <tbody>
      <%-- Display each table entries by: iterating through the rows and nesting iteration over columns --%>
      <% for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) { %>
      <tr>
        <td class="left-header"> <%-- First column gets 'left-header' tag --%>
          <%-- If in 'edit' mode, display the original 'row' number instead of 0 --%>
          <% if ("edit".equals(pageMode)) { %>
          <span class="row-link"><%=1+row+pageSize*(currentPage-1)%></span>
          <% } else { %>
          <%-- Display the row number in the current model view (rowIndex) --%>
          <a href = "<%=request.getContextPath() + "/edit?row=" + rowIndex%>" class="row-link"><%=1+rowIndex+pageSize*(currentPage-1)%></a>
          <% } %>
        </td>
        <%-- Iterate over column indices --%>
        <% for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
          String value = columns.get(columnIndex).get(rowIndex);
        %>
        <td>
          <%-- Display the value in row rowIndex and column ColumnIndex --%>
          <%-- If the column is the primary key then make displayed value a hyperlink to that patient --%>
          <%-- Could maybe pass row into parameter instead (need to check)--%>
          <% if (names.get(columnIndex).equals(key) && !"edit".equals(pageMode)) { %>
          <a href = "<%=request.getContextPath() + "/edit?row=" + rowIndex%>">
            <%= value %>
          </a>
          <% } else { %>
          <%= value %>
          <% } %>
        </td>
        <% } %>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>
<% } %>