
<%
	pageContext.setAttribute("TITLE", "Showing Zone");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<h3>Zone information</h3>
<soak:renderBean bean="${zone}" />
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink"
		href="<c:url value="/admin/dns/editZone.do?id=${zone.id}"/>"><span>Edit
	zone</span></a>
</authz:authorize>
<h3>Zone Records</h3>
<table>

	<thead>
		<tr>
			<th>name</th>
			<th>type</th>
			<th>ttl</th>
			<th>data</th>
			<th>serial</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${records }" var="rec">
			<tr>
				<td>${rec.hostName}</td>
				<td>${rec.type}</td>
				<td>${rec.ttl}</td>
				<td>${rec.target}</td>
				<td>${rec.lastUpdateSerial}</td>
			</tr>
		</c:forEach>
	</tbody>

</table>
<%@ include file="/footer.include.jsp"%>
