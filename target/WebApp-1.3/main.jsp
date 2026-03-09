<%@ page import = "java.util.List" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<% TableData table = (TableData) request.getAttribute("table");
String pageMode = (String) request.getAttribute("pageMode"); %>

<html>
<head>
  <%@ include file="/header.jspf" %>
</head>

<body>
  <div class="main">
    <h3> Patient Data </h3>
    <%@ include file="/error.jsp" %>
    <%@ include file="/table.jspf" %>
    <%@ include file="/searchBar.jspf" %>
    <%@ include file="/buttonRow.jspf" %>
  </div>
</body>
</html>