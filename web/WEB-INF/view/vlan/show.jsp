
<%
	pageContext.setAttribute("TITLE", "Show Vlan ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<h3>Vlan Details</h3>
<soak:renderBean bean="${vlan}" />

<h3>Subnets</h3>
<c:if test="${empty vlan.subnets}">

This vlan is not used by any  subnets. 
</c:if>
<c:if test="${!empty vlan.subnets}">

This vlan is used on the following subnets:
<ul>
		<c:forEach items="${vlan.subnets}" var="subnet">
			<li><a
				href="<c:url value="/subnet/view.do"><c:param name="id" value="${subnet.id }"/></c:url>">${subnet.minIP.hostAddress}
			${subnet.name }</a></li>
		</c:forEach>
	</ul>
</c:if>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink"
		href="<c:url value="/admin/vlan/edit.do" >
<c:param name="id">${vlan.id}</c:param>
</c:url>">
	edit</a>
	<a class="actionlink"
		href="<c:url value="/admin/vlan/delete.do" >
<c:param name="id">${vlan.id}</c:param>
</c:url>">
	delete</a>
</authz:authorize>



<%@ include file="/footer.include.jsp"%>
