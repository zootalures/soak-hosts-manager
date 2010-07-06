<%@ include file="/base.include.jsp"%>
<h4>DHCP Status</h4>


<c:if
	test="${empty bean.reservations and (bean.parent.host.hostClass.DHCPStatus eq 'REQUIRED')}">
	<div class="errorBox">The policy is for this type of host to use
	DHCP, however no reservations are present. To add a reservation set the
	hosts MAC address.</div>
</c:if>
<c:if
	test="${empty bean.reservations and  (bean.parent.host.hostClass.DHCPStatus == 'IF_POSSIBLE')}">
	<div class="warnBox">Hosts of this type may have DHCP
	reservation. To create a reservation set the host's MAC address.</div>
</c:if>

<c:if test="${!empty bean.reservations and bean.hasErrors }">
	<div class="errorBox">One or more reservations associated with
	this host appears to be incorrect or missing.</div>
</c:if>
<c:if test="${!empty bean.reservations and !bean.hasErrors }">
	<div class="infoBox">The DHCP reservation(s) associated with this
	host appear to be in order.</div>
</c:if>

<c:if test="${! empty bean.reservations}">
	<table class="listTable">
		<thead>
			<tr>
				<th>Server</th>
				<th>Scope</th>
				<th>IP</th>
				<th>MAC</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${bean.reservations }" var="rec">
				<c:set var="state" value="${bean.reservationState[rec]}" />
				<tr class="dnsRecordEndorsed dns_${state} ">
					<td>${rec.scope.server.displayName}</td>
					<td>${rec.scope.minIP.hostAddress }</td>
					<td>${rec.ipAddress.hostAddress }</td>
					<td>${rec.macAddress }</td>
					<td><c:choose>
						<c:when test="${state eq 'MISSING'}">Should be present but is not</c:when>
						<c:when test="${state eq 'SPURIOUS'}">Is present but may be incorrect</c:when>
					</c:choose></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>