<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>

<%
  String pageMode = (String) request.getAttribute("pageMode");
  List<String> keyList = (List<String>) request.getAttribute("keyList");
  int currentPageSize = (int) request.getAttribute("pageSize");
  
if (pageMode.equals("setView")) { %>
<%--- Display 'fileName.csv' buttons that pass input to ImportServlet through 'path' parameter --%>

<form id="viewForm" action="/view" method="get">
  <div class="button-row">
    <span class="info">Select columns to display (leave blank for all):</span>
    <% for (String key : keyList) {
      String id = "column-" + key;
    %>
    <input type="checkbox" id="<%=id%>" name="columns" value="<%=key%>" hidden>
    <label for="<%=id%>" class="small-button"><%=key%></label>
    <% } %>
  </div>

  <div class="button-row">
    <span class="info">Rows per page:</span>
    <input type="number" name="pageSize" value="<%=currentPageSize%>" min="1">
    <button form="viewForm" type="submit" class="small-button">Go</button>
  </div>
</form>


<% } %>