<% 
int currentPage = (int) request.getAttribute("page");
int pageTotal = (int) request.getAttribute("pageTotal");

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
  </form>
</div>
