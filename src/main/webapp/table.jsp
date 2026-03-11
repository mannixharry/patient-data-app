
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
String pageMode = (String) request.getAttribute("pageMode");
TableData table = (TableData) request.getAttribute("table");

String key = (String) request.getAttribute("key");

List<String> names = null;
List<List<String>> columns = null;
int columnCount = 0;
int rowCount = 0;
boolean hasData = false;

if (table != null) {
  names = table.getNames();
  columns = table.getColumns();
  hasData = columns != null 
  && !columns.isEmpty() 
  && !names.isEmpty() 
  && !columns.get(0).isEmpty();
  columnCount = names.size();
  rowCount = hasData ? columns.get(0).size() : 0;
}

Integer rowID = (Integer) request.getAttribute("rowID");
String patientID = (String) request.getAttribute("patientID");
boolean edit = "edit-by-row".equals(pageMode) || "edit-by-ID".equals(pageMode);
boolean hasRow = rowID != null;
boolean hasKey = patientID != null;

if (!hasData) { %>
  <p class = "error-message">Error: No results</p>
<% } else { %>
  <div class = "table-container">
    <table>
      <thead>
        <tr>
          <th class="top-header left-header">Row</th>
          <% for (String name : names) { %>
            <th class="top-header"><%= name %></th>
          <% } %>
        </tr>
      </thead>
      <tbody>
        <% for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) { %>
          <tr>
              <td class="left-header">
                <% if (edit) { %>
                  <span class="row-link"><%=rowID%></span>
                <% } else { %>
                  <a href = "<%="patient?row=" + rowIndex%>" class="row-link"><%=rowIndex%></a>
                <% } %>
              </td>
            <% for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
              String value = columns.get(columnIndex).get(rowIndex);
            %>
              <td>
                <% if (names.get(columnIndex).equals(key) && !edit) { %>
                <a href =  "<%="patient?" + key + "=" + columns.get(columnIndex).get(rowIndex)%>">
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