<%@ page import = "java.util.List" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>

<%
  // Fetch and unpack TableData from request
  TableData table = (TableData) request.getAttribute("table");
  String pageMode = (String) request.getAttribute("pageMode");
  String key = (String) request.getAttribute("key");
  
  List<String> names = table.getNames();
  List<List<String>> columns = table.getColumns();
  
  // Validate retrieved table data
  boolean hasData = columns != null
  && !columns.isEmpty()
  && !names.isEmpty()
  && !columns.get(0).isEmpty();
  int columnCount = names.size();
  int rowCount = hasData ? columns.get(0).size() : 0;
  boolean isSinglePatient = hasData && rowCount == 1;
  
  // Assumption: pageMode equal to 'edit' -> 'row' intialized
  Integer row = (Integer) request.getAttribute("row");

  // Logic for 'new' action
  boolean isNew = (boolean) request.getAttribute("isNew");
  // retryFlag indicates if the user has submitted the form once already and failed to input a (unique) key
  String retryMessage = (String) request.getAttribute("retryMessage");
  boolean retryFlag = retryMessage != null;
  
  // Saved form values from a previously incorrect input
  // Assumption: retryFlag -> retryValues initialized in request
  List<String> retryValues = (List<String>) request.getAttribute("retryValues");
%>

<% if ("edit".equals(pageMode)) { %>
<% if (!isSinglePatient && !isNew) { %>
<p class = "error-message"> Error: Patient key is not unique / does not exist</p>
<a href="main" class="button">Back</a>
<% } else if (names == null) { %>
<p class = "error-message"> Error: No patient data available</p>
<a href="main" class="button">Back</a>
<% } else { %>

<%-- Set the servlet to recieve the input data isNew -> NewServlet, !isNew -> EditServlet --%>
<form action ="<%= isNew ? "new" : "edit"%>" method ="post">
  <input type="hidden" name="row" value="<%=row%>">
  <div class="table-container">
    <table>
      <tbody>
        <%-- Iterate over table columns, getting the column name and value --%>
        <% for (int colIndex = 0; colIndex < names.size(); colIndex++) {
          String name = names.get(colIndex);
          // isNew -> Load any saved inputs from previous (invalid) form submission
          // !isNew -> Load actual data values
          String value = isNew
          ? (retryFlag && retryValues != null ? retryValues.get(colIndex) : "")
          : columns.get(colIndex).get(0);
        %>
        <tr>
          <%-- Mark the table cells in the top row with 'top-header' tag --%>
          <% if (colIndex == 0) { %>
          <div class=top-header>
            <% } else { %>
            <div>
              <% } %>
              <%-- Add a row made of the column name and a text box to input / edit data --%>
              <%-- Mark table cells in the first column with 'left-header' tag --%>
              <th class="left-header"><%= name %></th>
              <%-- If the column is the key field, then: --%>
              <%-- isNew -> Display text-box for input with a special border --%>
              <%-- !isNew -> Display the key field's value without the option to edit --%>
              <% if (name.equals(key)) {
              if (isNew) { %>
              <th class="left-header top-header">
                <input type="text" name="<%= key %>" value="<%= value %>">
              </th>
              <% } else { %>
              <th>
                <%= value %>
                <%-- hidden input ensures key is included in Post request parameter --%>
                <input type="hidden" name="<%= key %>" value="<%= value %>">
              </th>
            </div>
            <% } %>
            <% } else { %>
            <%-- Default: add a text-based input attaching value to column name parameter --%>
            <td>
              <input type="text" name="<%= name %>" value="<%= value %>">
            </td>
            <% } %>
          </tr>
          <% } %>
        </tbody>
      </table>
    </div>
    <%-- Display edit bar --%>
    <%-- Selection is passed as 'action' parameter in Post request --%>
    <div class="button-row">
      <%-- isNew -> show 'add' button --%>
      <%-- !isNew -> show 'update' and 'delete' buttons --%>
      <% if (isNew) { %>
      <button class="button" type="submit" name="action" value="new">Add</button>
      <% } else { %>
      <button class="button" type="submit" name="action" value="update">Update</button>
      <button class="button" type="submit" name="action" value="delete">Delete</button>
      <% } %>
      <%-- Show back button (really a link to /main) --%>
      <a href="main?noRefresh=true" class="button">Back</a>
      <%-- Give feedback on invalid inputs --%>
      <% if (retryFlag) { %>
      <p class="info"><%=retryMessage%></p>
      <% } %>
    </div>
  </form>
  <% } %>
  <% } %>