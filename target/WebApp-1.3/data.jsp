<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Map" %>

<% 
String errorMessage = (String) request.getAttribute("errorMessage");
List<String> names = (List<String>) request.getAttribute("names");
List<List<String>> columns = (List<List<String>>) request.getAttribute("columns");

int columnCount = names.size();
int rowCount = 0;
if (columns != null && !columns.isEmpty()) {
  rowCount = columns.get(0).size();
} else {
  // Fall-back empty lists for safety
  names = List.of();
  columns = List.of();    
}
%>

<html>
<head>
    <title>Patient Data App</title>
    <link rel = "stylesheet" href = "style.css">
</head>

<body>
 <div class="main">
    <h3> Patient Data </h3>
    <% if (errorMessage != null) { %>
      <p style="color: red"> <%= errorMessage %> </p>
    <% } %>
    <div class = "table-container">
      <table> 
        <tr>
          <% for (String name : names) { %>
            <th><%= name %></th>
          <% } %>
        </tr>

        <% for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) { %>
        <tr> 
          <% for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { %>
          <td>
            <% if (names.get(columnIndex).equals("ID")) { %>
            <a href = "patient?id=<%= columns.get(columnIndex).get(rowIndex) %>">
              <%= columns.get(columnIndex).get(rowIndex) %>
            </a>
            <% } else { %> 
              <%= columns.get(columnIndex).get(rowIndex) %>
            <% } %> 
          </td>
          <% } %> 
        </tr>
        <% } %>
      </table>
    </div> 
  </div>
</body>
</html>