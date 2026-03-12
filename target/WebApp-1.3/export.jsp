<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "uk.ac.ucl.view.TableData" %>
<%
  String pageMode = (String) request.getAttribute("pageMode");
  if ("export".equals(pageMode)) {
    
    TableData table = (TableData) request.getAttribute("table");
    
    String fileName = request.getParameter("fileName");
    String fileType = request.getParameter("fileType");
    String exportLocation = request.getParameter("location");
    
    List<String> names = table.getNames();
    List<List<String>> columns = table.getColumns();
    
    // Validate retrieved table data
    boolean hasData = columns != null
    && !columns.isEmpty()
    && !names.isEmpty()
    && !columns.get(0).isEmpty();
    int columnCount = names.size();
    int rowCount = hasData ? columns.get(0).size() : 0;
  %>

  <form action="/export" method="get" class="button-row">
    <span class="info">Export as: </span>
    <button class="small-button" type="submit" name="fileType" value="csv">CSV</button>
    <button class="small-button" type="submit" name="fileType" value="json">JSON</button>
    </form>

    <% if (fileType != null && !fileType.isEmpty()) { %>
    <form action="/export" method="get" class="button-row">
      <span class="info">Export to: </span>
      <input type="hidden" name="fileType" value="<%=fileType%>"> </input>
      <button class="small-button" type="submit" name="location" value="download">Downloads</button>
      <button class="small-button" type="submit" name="location" value="data">Server Files</button>
      </form>

      <% if (exportLocation != null && !exportLocation.isEmpty()) { %>
      <div class="button-row">
        <form action="/export" method="get" class="inline-form">
        <input type="hidden" name="fileType" value="<%=fileType%>"> </input>
        <input type="hidden" name="location" value="<%=location%>"> </input>
          <span class="info">Exporting as <%=fileType%> to <%=location%> </span>
          <input type="text" name="fileName" placeholder="Enter file name" value="<%= fileName != null ? fileName : "" %>">
          <button type="submit" class="small-button">Go</button>
        </form>
      </div>

      <% } %>
      <% } %>
      <% } %>