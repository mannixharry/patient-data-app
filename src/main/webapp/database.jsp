<%@ page import="java.util.List" %>
<html>
  <head>
    <title>Patient Data App</title>
  </head>

  <body>
    <div class="main"> 

      <% 
      String errorMessage = (String) request.getAttribute("errorMessage");
      if (errorMessage != null) {
      %>
        <p style="color: red;"><%= errorMessage %></p>
      <%
      }
      %>

      <ul>
        <%
          List<String> columnNames = (List<String>) request.getAttribute("columnNames");
          if (columnNames != null) {
            for (String column : columnNames) {
        %>
              <li><%= column %></li>
        <%
            }
          }
        %>
      </ul>
    </div>
  </body>
</html>