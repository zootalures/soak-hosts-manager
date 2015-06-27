
<%
	pageContext.setAttribute("TITLE", "DNS Zones");
	pageContext.setAttribute("cmdMenu", "dns-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink" href="<c:url value="/admin/dns/updateZones.do" />">
	Refresh all zones</a>
	<a class="actionlink"
		href="<c:url value="/flow/cleanup-unused-dns-records-flow.flow" />">
	Delete unused DNS records</a>
	<h3>Forward Zones</h3>
	<a class="actionlink"
		href="<c:url value="/admin/dns/editZone.do?type=forward" />"> New
	forward zone</a>

</authz:authorize>
<table>
	<thead>
		<tr>
			<th>Suffix</th>
			<th>Name</th>
			<th>Server</th>
			<th>Last update</th>
		</tr>
	</thead>
	<tbody>

		<c:forEach items="${fwZones}" var="zone">
			<tr>

				<td><a
					href="<c:url value="/dns/show.do">
					<c:param name="id"
						value="${zone.id}"/>
			</c:url>">
				${zone.domain}</a></td>
				<td>${zone.displayName}</td>
				<td>${zone.serverIP.hostAddress } : ${zone.serverPort }</td>
				<td><fmt:formatDate type="both" value="${zone.lastUpdate}" /> (
				${zone.serial})</td>
				<td><a class="showButton"
					href="<c:url value="/dns/show.do?id=${zone.id}"/>"><span>show</span></a>
				<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
					<a class="editButton"
						href="<c:url value="/admin/dns/editZone.do?id=${zone.id}"/>"><span>edit</span></a>

					<a class="deleteButton"
						href="<c:url value="/admin/dns/deleteZone.do?id=${zone.id}"/>"><span>delete</span></a>
				</authz:authorize></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<h3>Reverse Zones</h3>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink"
		href="<c:url value="/admin/dns/editZone.do?type=reverse" />"> New
	reverse zone</a>
</authz:authorize>

<table>
	<thead>
		<tr>
			<th>Suffix</th>
			<th>Name</th>
			<th>Server</th>
			<th>Last update</th>
		</tr>
	</thead>
	<tbody>

		<c:forEach items="${rvZones}" var="zone">
			<tr>

				<td><a
					href="<c:url value="/dns/show.do">
					<c:param name="id"
						value="${zone.id}"/>
			</c:url>">
				${zone.domain}</a></td>
				<td>${zone.displayName}</td>
				<td>${zone.serverIP.hostAddress } : ${zone.serverPort }</td>
				<td><fmt:formatDate type="both" value="${zone.lastUpdate}" /> (
				${zone.serial})</td>

				<td><a class="showButton"
					href="<c:url value="/dns/show.do?id=${zone.id}"/>"><span>show</span></a>
				<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
					<a class="editButton"
						href="<c:url value="/admin/dns/editZone.do?id=${zone.id}"/>"><span>edit</span></a>

					<a class="deleteButton"
						href="<c:url value="/admin/dns/deleteZone.do?id=${zone.id}"/>"><span>delete</span></a>
				</authz:authorize></td>

			</tr>
		</c:forEach>
	</tbody>
</table>
<%@ include file="/footer.include.jsp"%>
