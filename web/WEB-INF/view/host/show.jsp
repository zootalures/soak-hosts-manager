
<%
	pageContext.setAttribute("TITLE", "View Host ");
	pageContext.setAttribute("cmdMenu", "host-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div id="hosttabs" class="yui-navset">
<ul class="yui-nav">
	<c:forEach items="${hostView.sortedTabs}" var="tab" varStatus="status">
		<c:if test="${!empty tab.renderBeans}">
			<li ${status.first?"class='selected'":"" }><a
				href="#${tab.tabName }"><em>${tab.tabTitle }</em></a></li>
		</c:if>
	</c:forEach>
</ul>

<div class="yui-content"><c:forEach items="${hostView.sortedTabs}"
	var="tab" varStatus="status">
	<c:if test="${!empty tab.renderBeans}">
		<div id="${tab.tabName }"><c:forEach items="${tab.renderBeans}"
			var="bean">
			<soak:renderBean bean="${bean }" />
		</c:forEach></div>
	</c:if>
</c:forEach></div>
</div>

<div class="actionlink" href="#" id="hostrow_${hostView.host.id }"
	class="${soak:contains(starredHostIds,hostView.host.id)? "
	selected":"" }"
	onclick="SoakStarred.flipStarred([${hostView.host.id}],!SoakStarred.isChecked(${hostView.host.id}))">
<span style="float:left;" class="starSelect">&nbsp;</span><a
	href="#">&nbsp; toggle selected status</a></div>
<c:if test="${soak:canEdit(hostView.host.ownership) }">
	<a class="actionlink"
		href="<c:url value="/flow/update-host-flow.flow"> <c:param name="id" value="${hostView.host.id}" /></c:url>">Edit
	host</a>
	<a class="actionlink"
		href="<c:url value="/flow/delete-host-flow.flow"> <c:param name="id" value="${hostView.host.id}" /></c:url>">Delete
	host</a>
</c:if>
<a class="actionlink" href="<c:url value="/host/search.do"/> ">Search
hosts</a>
<script type="text/javascript">  
var tabView;
	YAHOO.util.Event.onContentReady("hosttabs",function(){
     tabView = new YAHOO.widget.TabView('hosttabs');
	
	});
  
    </script>
<%@ include file="/footer.include.jsp"%>