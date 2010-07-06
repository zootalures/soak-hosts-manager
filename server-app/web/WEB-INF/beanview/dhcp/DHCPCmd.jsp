<%@ include file="/base.include.jsp"%>
<div class="cmdListEntry">
<div class="cmdListTitle">DHCP system</div>
<ul>
	<c:forEach items="${bean.changes}" var="change">
		<li><c:if test="${change.addition}">
			<b>Addition</b> of reservation 
</c:if> <c:if test="${!change.addition}">
			<b>Deletion</b> of reservation
</c:if> <b>${change.reservation.ipAddress.hostAddress}</b> :
		${change.reservation.macAddress} (${change.reservation.hostName }) on
		server <B>${change.reservation.scope.server.displayName}</B></li>
	</c:forEach>

</ul>
</div>