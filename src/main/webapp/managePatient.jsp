<%@ page import = "java.util.List" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>

<%

TableData table = (TableData) request.getAttribute("patientTable");
String pageMode = (String) request.getAttribute("pageMode");
String key = (String) request.getAttribute("key");

List<String> names = null;
List<List<String>> columns = null;
boolean hasData = false;
int columnCount = 0;
int rowCount = 0;

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

boolean isSinglePatient = hasData && rowCount == 1;
boolean isNew = (boolean) request.getAttribute("isNew");
Boolean retryFlagObject = (Boolean) request.getAttribute("retryFlag");
boolean retryFlag = retryFlagObject != null && retryFlagObject;

List<String> retryValues = null;
if (retryFlag) {
  retryValues = (List<String>) request.getAttribute("retryValues");
}
%>

<% if (!isSinglePatient && !isNew) { %>
  <p class = "error-message"> Error: Patient key is not unique / does not exist</p>
  <a href="main" class="button">Back</a>
<% } else { %>

  <h3> Edit Patient </h3> 
  <form action ="<%= isNew ? "new" : "patient"%>" method ="post">
    <div class="table-container">
      <table>
        <tbody>
          <% for (int colIndex = 0; colIndex < names.size(); colIndex++) {
              String name = names.get(colIndex);
              String value = isNew 
                ? (retryFlag && retryValues != null ? retryValues.get(colIndex) : "") 
                : columns.get(colIndex).get(0);
          %>
            <tr>
              <% if (colIndex == 0) { %>
                <div class=top-header>
              <% } else { %>
                <div>
              <% } %>

              <th class="left-header"><%= name %></th>
              <% if (name.equals(key)) { 
                if (isNew) { %>
                  <th class="left-header top-header">
                    <input type="text" name="<%= key %>" value="<%= value %>">
                  </th>
                <% } else { %>
                  <th>
                    <%= value %>
                    <input type="hidden" name="<%= key %>" value="<%= value %>">
                  </th>
                </div>
                <% } %>
              <% } else { %>
                <td>
                  <input type="text" name="<%= name %>" value="<%= value %>">
                </td>
              <% } %>
            </tr>
          <% } %>
        </tbody>
      </table>
    </div>
    <div class="button-row">
      <% if (isNew) { %>
        <button class="button" type="submit" name="action" value="add">Add</button>
      <% } else { %>
        <button class="button" type="submit" name="action" value="update">Update</button>
        <button class="button" type="submit" name="action" value="delete">Delete</button>
      <% } %>
      <a href="main" class="button">Back</a>
      <% if (retryFlag) { %>
        <p class="info">ID field cannot be empty</p>
      <% } %>
    </div>
  </form>
<% } %>