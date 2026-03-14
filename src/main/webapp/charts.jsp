<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Set" %>

<div class="chart-container">
  <%
    Set<String> titleSet = (Set<String>) request.getAttribute("chartTitles");
    List<String> titles = new ArrayList<>(titleSet);
    
    for (String title : titles) {
    %>
    <img src="<%=request.getContextPath()%>/charts?title=<%=title%>"/>
    <% } %>
</div>

