<html>
  <head>
    <jsp:include page="/header.jsp" />
  </head>
  <body>
    <h1 class="page-title">Charts Dashboard</h1>
    <jsp:include page="/charts.jsp" />
    <form action="/main" method="get" class="button-row">
      <input type="hidden" name="noRefresh" value="true">
      <button type="submit" class="button">Back</button>
    </form>
  </body>
</html>

