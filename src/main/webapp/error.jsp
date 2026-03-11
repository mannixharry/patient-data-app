<html>
  <head>
    <title>Error</title>
  </head>
  <body>
    <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
    <% if (errorMessage != null) { %>
    <p class="error-message">Something went wrong</h2>
      <p class="error-message"><%= errorMessage %></p>
      <% } %>
    </body>
  </html>