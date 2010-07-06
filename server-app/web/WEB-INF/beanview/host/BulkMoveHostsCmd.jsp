<%@ include file="/base.include.jsp"%>
Move ${fn:length(bean.hosts)} hosts to subnet
${bean.newSubnet.displayString } with the following IP changes:

<table class="listTable">
	<thead>
		<tr>
			<th>Host</th>
			<th>Old IP</th>
			<th>New IP</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${bean.hosts}" var="host">
			<tr>
				<td>${host.hostName }</td>
				<td>${host.ipAddress.hostAddress }</td>
				<td>${bean.hostAddresses[host.id].hostAddress }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>