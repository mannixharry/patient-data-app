<%@ page import = "java.util.List" %>

<% 
String path = request.getParameter("path");
String importKey = request.getParameter("key");

List<String> files = (List<String>) request.getAttribute("files");
List<String> keyList = (List<String>) request.getAttribute("keyList");

if (pageMode.equals("import")) { %>
  <form action="/import" method="get" class="button-row">
    <span class="info">Select a file</span>
    <% for (String fileName : files) { %>
      <button class="small-button" type="submit" name="path" value="<%=fileName%>"> <%= fileName %> </button>
    <% } %>
  </form> 
  <% if (path != null && !path.isEmpty()) { %>
    <form action="/import" method="get" class = "button-row">
      <span class="info">Select key</span>
      <button class="small-button" type="submit" name="key" value="">None</button>
      <% for (String columnName : keyList) { %>
        <button class="small-button" type="submit" name="key" value="<%=columnName%>"> <%= columnName %> </button>
      <% } %>
    </form>
  <% } %> 
  
  <% if (path != null && !path.isEmpty()) { %>
    <div class="button-row">
        <span class="info">Opened <%= path %></span>
        <form action="/main" method="get" class="inline-form">
          <button type="submit" class="small-button">Back</button>
        </form>
    </div>
  <% } %>
<% } %>