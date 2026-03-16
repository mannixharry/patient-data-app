<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>

<%
  String pageMode = (String) request.getAttribute("pageMode");
  List<String> keyList = (List<String>) request.getAttribute("keyList");
  int currentPageSize = (int) request.getAttribute("pageSize");
  String primaryKey = (String) request.getAttribute("key");
  
if (pageMode.equals("setView")) { %>
<%--- Display 'fileName.csv' buttons that pass input to ImportServlet through 'path' parameter --%>

<form id="viewForm" action="/view" method="get">
  <div class="button-row">
    <span class="info">Select columns to display (leave blank for all):</span>
    <% for (String key : keyList) {
      String id = "column-" + key;
      boolean isPrimary = primaryKey.equals(key);
    %>
    <% if (isPrimary) { %>
    <!-- Always submit the primary key -->
    <input type="hidden" name="columns" value="<%=key%>">
    <!-- Optional checkbox just for display -->
    <input type="checkbox" id="<%=id%>" checked disabled hidden>
    <label for="<%=id%>" class="small-button"><%=key%></label>
    <% } else { %>
    <input type="checkbox" id="<%=id%>" name="columns" value="<%=key%>" hidden>
    <label for="<%=id%>" class="small-button"><%=key%></label>
    <% } %>
    <% } %>

  </div>

  <div class="button-row">
    <span class="info">Rows per page:</span>
    <input type="number" name="pageSize" value="<%=currentPageSize%>" min="1">
    <button form="viewForm" type="submit" class="small-button">Go</button>
  </div>
</form>


<% } %>