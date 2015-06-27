<%@ include file="/base.include.jsp"%>

Bulk deletion of selected unused DNS records from domains:
<ul>
	<c:forEach items="${bean.zones}" var="zone">
		<li>${zone.domain} : ${zone.displayName }</li>
	</c:forEach>
</ul>