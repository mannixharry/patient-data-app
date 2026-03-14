<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
  TableData table = (TableData) request.getAttribute("table");
  String pageMode = (String) request.getAttribute("pageMode");
  
  String searchColumn = request.getParameter("searchColumn");
  String searchTerm = request.getParameter("searchTerm");
%>
<% if (pageMode.equals("search")) {
  List<String> buttonNames = new ArrayList<>();
  buttonNames.add("ANY");
buttonNames.addAll(table.getNames()); %>
<div class="button-row">
  <span class="info">Select a column</span>
  <form action="/search" method="get" class="inline-form">
    <% for (String name : buttonNames) { %>
    <button class="small-button" type="submit" name="searchColumn" value="<%=name%>"> <%= name %> </button>
    <% } %>
  </form>
</div>

<% if (searchColumn != null && !searchColumn.isEmpty()) { %>
<div class="button-row">
  <form action="/search" method="get" class="inline-form">
    <input type="hidden" name="searchColumn" value="<%= searchColumn %>">
    <span class="info">Searching in <%= searchColumn %> </span>
    <input type="text" name="searchTerm" placeholder="Enter search term" value="<%= searchTerm != null ? searchTerm : "" %>">
    <button type="submit" class="small-button">Go</button>
  </form>
  <form action="/main" method="get" class="inline-form">
    <input type="hidden" name="noRefresh" value="true">
    <button type="submit" class="small-button">Back</button>
  </form>
</div>
<% } %>
<% } %>