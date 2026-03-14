<%@ page import = "uk.ac.ucl.view.TableData" %>
<html>
  <head>
    <jsp:include page="/header.jsp"/>
  </head>

  <body>
    <div class="main">
      <h3> Patient Data </h3>
      <jsp:include page="/error.jsp"/>
      <jsp:include page="/manage.jsp"/>
    </div>
  </body>
</html>