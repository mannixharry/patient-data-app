<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%--
Assumptions:
"table", "pageMode" and "files" are initialized in any valid request
If path parameter has been set, then "keyList" has been initialized
--%>
<%
  TableData table = (TableData) request.getAttribute("table");
  String pageMode = (String) request.getAttribute("pageMode");
  Boolean fileOpenFlag = (Boolean) request.getAttribute("fileOpenFlag");
  String path = request.getParameter("path");
  String confirmation = request.getParameter("confirmation");
  
  List<String> files = (List<String>) request.getAttribute("files");
  
if (pageMode.equals("delete")) { %>
<%--- Display 'fileName.csv' buttons that pass input to ImportServlet through 'path' parameter --%>
<form action="/deleteFile" method="get" class="button-row">
  <span class="info">Select a file</span>
  <% for (String fileName : files) { %>
  <button class="small-button" type="submit" name="path" value="<%=fileName%>"> <%= fileName %> </button>
  <% } %>
</form>
<%-- Run only if file has been selected --%>
<% if (path != null && !path.isEmpty()) {
if (fileOpenFlag != null && fileOpenFlag) { %>
  <div class=button-row>
    <span class="info">Cannot delete currently open file</span>
  </div>
<% } else if (confirmation == null || confirmation.isEmpty()) { %>

<div class="button-row">
  <form action="/deleteFile" method="get" class = "inline-form">
    <%-- Hidden 'path' input ensures previously input path parameter is not lost upon form submission--%>
    <input type="hidden" name="path" value = "<%=path%>">
    <%-- Display 'key' buttons which list the possible primary keys for the user to choose from --%>
    <%-- And pass the input to ImportServlet using 'key' parameter --%>
    <span class="info">Confirm: Delete <%=path%></span>
    <button class="small-button" type="submit" name="confirmation" value="yes">Yes</button>
  </form>
  <form action="/main" method="get" class="inline-form">
    <button aciton="/main" method="get" type="submit" class="small-button">No</button>
  </form>
</div>
<% } %>

<%-- Show live-feedback: opened file name; chosen key + 'Back' button --%>
<% if (confirmation != null && !confirmation.isEmpty()) { %>
<div class="button-row">
  <span class="info">Successfully deleted file: </span>
  <span class="info"><%=path%></span>
  <form action="/main" method="get" class="inline-form">
    <button type="submit" class="small-button">Back</button>
  </form>
</div>

<% } %>
<% } %>
<% } %>