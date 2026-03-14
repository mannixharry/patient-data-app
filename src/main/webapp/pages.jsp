<% 
int currentPage = (int) request.getAttribute("page");
int pageSize = (int) request.getAttribute("pageSize");
int pageTotal = (int) request.getAttribute("pageTotal");
  int rowTotal = (int) request.getAttribute("rowTotal");
%>
<div class="button-row">
  <form class="inline-form" action="/data" method="get">
    <span class="info">Set page:</span>
    <input type="number" name="page" placeholder="Page number" value="<%=currentPage%>">
    <button type="submit" class="small-button">Go</button>
  </form>
  <form class="inline-form" action="/data" method="get">
    <button type="submit" name="page" value="<%=currentPage-1%>" class="small-button">Previous</button>
  </form>
  <form class="inline-form" action="/data" method="get">
    <button type="submit" name="page" value="<%=currentPage+1%>" class="small-button">Next</button>
    <span class="info">Viewing page <%=currentPage%> of <%=pageTotal%> </span>
    <span class="info">Pages of size <%=pageSize%> </span>
    <span class="info"><%=rowTotal%> rows</span>
  </form>
</div>
