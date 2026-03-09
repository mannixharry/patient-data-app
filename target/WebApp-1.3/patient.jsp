<%@ page import = "java.util.List" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>

<% TableData table = (TableData) request.getAttribute("patientTable"); %> 
<html>
<head>
  <%@ include file="/header.jspf" %>
</head>

<body>
 <div class="main">
    <h3> Patient Data </h3>
    <%@ include file="/error.jsp" %>
    <%@ include file="/table.jspf" %>
    <%@include file="/editPatient.jspf" %>
  </div>
</body>
</html>