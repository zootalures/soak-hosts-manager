<%@ include file="/base.include.jsp"%>
<h4>Network access</h4>
<c:choose>
	<c:when test="${ bean.lastIpSeen !=null }">
		<c:if
			test="${bean.hostView.host.macAddress !=null && !(bean.lastIpSeen.macAddress eq bean.hostView.host.macAddress)}">
			<div class="warnBox">WARNING: The machine using the IP address
			associated with this host
			(${bean.hostView.host.ipAddress.hostAddress} is active on a different
			mac address (${bean.lastIpSeen.macAddress }) to the one recorded in
			the hosts database (${bean.hostView.host.macAddress }). <br />
			This may simply because a previous host has been associated with this
			IP address and the hosts manager has not yet detected the new host on
			this IP address.</div>
		</c:if>
		This host was last seen on the network at <b><fmt:formatDate
			value="${bean.lastIpSeen.changedAt }" type="both" /></b> using the MAC:<b>
		${bean.lastIpSeen.macAddress }</b>.
	</c:when>
	<c:otherwise>
Either this host has not used the network recently or it is connected to a subnet for which we do not have usage information.
</c:otherwise>
</c:choose>

<c:if test="${! empty bean.historyForIp }">
	<h4>History for IP</h4>
The IP address of this host ${bean.hostView.host.ipAddress.hostAddress } has been associated with the following MAC addresses in the past:

<table class="listTable">
		<thead>
			<tr>
				<th>Date</th>
				<th>From MAC</th>
				<th>To MAC</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${bean.historyForIp }" var="hist">
				<tr>
					<td><fmt:formatDate type="both" value="${hist.changedAt}" /></td>
					<td>${hist.fromMac }</td>
					<td>${hist.toMac }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<c:if test="${! empty bean.historyForMac }">
	<h4>History for MAC</h4>
The  MAC address of this host <b>${bean.lastIpSeen.macAddress }</b> has been associated with the following IP addresses in the past :

<table class="listTable">
		<thead>
			<tr>
				<th>Date IP use started</th>
				<th>Previous MAC on IP</th>
				<th>IP</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${bean.historyForMac }" var="hist">
				<tr>
					<td><fmt:formatDate type="both" value="${hist.changedAt}" /></td>
					<td>${hist.fromMac }</td>

					<td>${hist.ipAddress.hostAddress }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>