<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%--
Assumptions:
"table", "pageMode" and "files" are initialized in any valid request
If path parameter has been set, then "keyList" has been initialized
--%>
<%
  String pageMode = (String) request.getAttribute("pageMode");
  
  String path = request.getParameter("path");
  String importKey = request.getParameter("key");
  
  List<String> files = (List<String>) request.getAttribute("files");
  List<String> keyList = (List<String>) request.getAttribute("keyList");
  
if (pageMode.equals("import")) { %>
<%--- Display 'fileName.csv' buttons that pass input to ImportServlet through 'path' parameter --%>
<form action="/importFile" method="get" class="button-row">
  <span class="info">Select a file</span>
  <% for (String fileName : files) { %>
  <button class="small-button" type="submit" name="path" value="<%=fileName%>"> <%= fileName %> </button>
  <% } %>
</form>
<%-- Run only if file has been selected --%>
<% if (path != null && !path.isEmpty()) { %> 
<form action="/importFile" method="get" class = "button-row">
  <%-- Hidden 'path' input ensures previously input path parameter is not lost upon form submission--%>
  <input type="hidden" name="path" value = "<%=path%>">
  <%-- Display 'key' buttons which list the possible primary keys for the user to choose from --%>
  <%-- And pass the input to ImportServlet using 'key' parameter --%>
  <span class="info">Select key</span>
  <button class="small-button" type="submit" name="key" value="">None</button>
  <% for (String columnName : keyList) { %>
  <button class="small-button" type="submit" name="key" value="<%=columnName%>"> <%= columnName %> </button>
  <% } %>
</form>
<% } %>

<%-- Show live-feedback: opened file name; chosen key + 'Back' button --%>
<% if (path != null && !path.isEmpty()) { %>
<div class="button-row">
  <span class="info">
    Opened <%= path %>
    <% if (importKey != null && !importKey.isEmpty()) { %>
    with key <%= importKey %>
    <% } %>
  </span>
  <form action="/main" method="get" class="inline-form">
    <input type="hidden" name="noRefresh" value="true">
    <button type="submit" class="small-button">Back</button>
  </form>
</div>
<% } %>
<% } %>