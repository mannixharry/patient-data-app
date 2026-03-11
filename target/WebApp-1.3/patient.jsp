<%@ page import = "java.util.List" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>

<% TableData table = (TableData) request.getAttribute("patientTable");
String pageMode = (String) request.getAttribute("pageMode"); %>
<html>
<head>
  <%@ include file="/header.jsp" %>
</head>

<body>
 <div class="main">
    <h3> Patient Data </h3>
    <jsp:include page="/error.jsp"/>
    <jsp:include page="/table.jsp"/>
    <jsp:include page="/managePatient.jsp"/>
  </div>
</body>
</html>