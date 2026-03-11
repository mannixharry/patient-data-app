<html>
  <head>
    <jsp:include page="/header.jsp" />
  </head>

  <body>
    <div class="main">
      <h3> Patient Data </h3>
      <jsp:include page="/error.jsp" />
      <jsp:include page="/table.jsp" />
      <jsp:include page="/search.jsp" />
      <jsp:include page="/sort.jsp" />
      <jsp:include page="/import.jsp" />
      <jsp:include page="/buttonRow.jsp" />
    </div>
  </body>
</html>