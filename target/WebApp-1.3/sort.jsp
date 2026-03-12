<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
TableData table = (TableData) request.getAttribute("table");
String pageMode = (String) request.getAttribute("pageMode");

String sortColumn = request.getParameter("sortColumn");

if (pageMode.equals("sort")) {
  List<String> buttonNames = new ArrayList<>();
  buttonNames.addAll(table.getNames()); %>
  <form action="/sort" method="get" class="button-row">
    <% for (String name : buttonNames) { %>
      <button class="small-button" type="submit" name="sortColumn" value="<%=name%>"> <%= name %> </button>
    <% } %>
  </form> 

  <% if (sortColumn != null && !sortColumn.isEmpty()) { %>
    <div class="button-row">
      <form action="/sort" method="get" class="inline-form">
        <input type="hidden" name="sortColumn" value="<%= sortColumn %>">
        <p class="info">Sorting by <%=sortColumn%></p>
        <button type="submit" name="ascending" value="true" class="small-button">Ascending</button>
        <button type="submit" name="ascending" value="false" class="small-button">Descending</button>
      </form>
      <form action="/main" method="get" class="inline-form">
        <button type="submit" class="small-button">Back</button>
      </form>
    </div>
  <% } %>
<% } %>