<%@ page import = "java.util.List" %>

<%
boolean isSinglePatient = hasData && rowCount == 1;
boolean isNew = (boolean) request.getAttribute("isNew");
Boolean retryFlagObject = (Boolean) request.getAttribute("retryFlag");
boolean retryFlag = retryFlagObject != null && retryFlagObject;

Integer rowID = (Integer) request.getAttribute("rowID");
String patientID = (String) request.getAttribute("patientID");
boolean edit = "edit-by-row".equals(pageMode) || "edit-by-ID".equals(pageMode);
boolean hasRow = rowID != null;
boolean hasKey = patientID != null;

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
              <% if (name.equals(key) && !isNew) { %>
                  <th>
                    <%= value %>
                    <input type="hidden" name="<%= key %>" value="<%= value %>">
                  </th>
                
                </div>
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