<%@ page isErrorPage="true" %>
<html>
<head>
  <title>Error</title>
</head>
<body>
  <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
  <% if (errorMessage != null) { %>
      <h2>Something went wrong</h2> 
      <p class="error-message"><%= errorMessage %></p>
      <a href="index.jsp">Return to Home</a>
  <% } %>
</body>
</html>