
<%
	pageContext.setAttribute("TITLE", "Shope DHCP Scope ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Showing scope
<b> ${scope.minIP.hostAddress }</b>
-
<b> ${scope.maxIP.hostAddress }</b>

last updated <fmt:formatDate type="both" value="${scope.fetchedOn }"/>.

<table>
	<thead>
		<tr>
			<th>IP</th>
			<th>MAC</th>
			<th>Hostname</th>
			<th>Updated</th>
			
		</tr>
	</thead>
	<tbody>
		<c:forEach var="res" items="${reservations }">
			<tr>
			
				<td>${res.ipAddress.hostAddress}</td>
				<td>${res.macAddress}</td>
				<td>${res.hostName}</td>
				<td><fmt:formatDate type="both" value="${res.updated}"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/footer.include.jsp"%>
