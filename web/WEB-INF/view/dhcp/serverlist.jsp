
<%
	pageContext.setAttribute("TITLE", "DHCP servers");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink" href="<c:url value="/admin/dhcp/editServer.do"/>">
	Add New DHCP Server</a>
	<a class="actionlink"
		href="<c:url value="/admin/dhcp/exportDHCPData.do"/>"> Export all
	DHCP reservations</a>

</authz:authorize>
<p>The following DHCP servers are configured:</p>
<c:forEach var="server" items="${servers }">
	<h3>Server: ${server.displayName }</h3>
	<soak:renderBean bean="${server}" />

	<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
		<a class="actionlink"
			href="<c:url value="/admin/dhcp/updateServer.do"><c:param name="id" value="${server.id }"/></c:url>">
		Re-synchronize Local DHCP information from this server</a
		<a class="actionlink"
			href="<c:url value="/admin/dhcp/editServer.do"><c:param name="id" value="${server.id}"/></c:url>" >Edit
		server properties</a>
	</authz:authorize>
	<table>
		<tr>
			<th>Scope</th>
			<th>Subnet</th>
			<th>#clients</th>
			<th>Last updated</th>
		</tr>

		<c:forEach var="scope" items="${scopes[server]}">
			<tr>
				<td><a
					href="<c:url value="/dhcp/viewScope.do">
				<c:param name="id" value="${scope.id}" /></c:url>">${scope.minIP.hostAddress}
				- ${scope.maxIP.hostAddress}</a></td>
				<td><c:if test="${not empty subnets[scope]}">
					<c:set var="subnet" value="${subnets[scope]}" />
					<a
						href="<c:url value="/subnet/view.do"><c:param name="id" value="${subnet.id }"/></c:url>">
					${subnet.name } </a>
				</c:if></td>

				<td>${fn:length(scope.reservations) }/${scope.numAddresses}</td>
				<td><fmt:formatDate type="both" value="${scope.fetchedOn }" />
				</td>
			</tr>
		</c:forEach>
	</table>
</c:forEach>

<%@ include file="/footer.include.jsp"%>