
<%
	pageContext.setAttribute("TITLE", "Show command");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<c:set scope="request" var="searchUrl">
	<c:url value='/user/recentCommands.do' />
</c:set>

The following command was run by
<b>${command.user }</b>
on
<b><fmt:formatDate type="both" value="${command.changeTime}" /></b>

<h3>Command details</h3>
<soak:renderBean bean="${baseCommand.baseChange}" />
<h3>Host changes</h3>
The command affected the following hosts;
<table class="listTable">
	<thead>
		<tr>
			<th>Change Type</th>
			<th>Host name</th>
			<th>IP</th>
			<th>OU</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${hostChanges}" var="hostChange">
			<tr>
				</td>
				<td><c:choose>
					<c:when test="${hostChange.changeType eq 'ADD' }">
							Create
						</c:when>
					<c:when test="${hostChange.changeType eq 'DELETE' }">
						Delete
						</c:when>
					<c:when test="${hostChange.changeType eq 'CHANGE' }">
						<a
							href="<c:url value="/host/showChange.do"><c:param name="id" value="${hostChange.id }"/></c:url>">
						Edit</a>
					</c:when>
				</c:choose></td>
				<td><a
					href="<c:url value="/host/show.do"> <c:param name="id" value="${hostChange.hostId }"/></c:url>"><soak:displayChange
					before="${hostChange.host.hostName}" after="${hostChange.hostName}" /></a></td>
				<td><soak:displayChange
					before="${hostChange.host.ipAddress.hostAddress}"
					after="${hostChange.ipAddress.hostAddress }" /></td>
				<td><soak:displayChange
					before="${hostChange.host.ownership.orgUnit.id}"
					after="${hostChange.orgUnit.id }" /></td>

			</tr>
		</c:forEach>
	</tbody>
</table>
<h3>Secondary changes</h3>
<c:forEach var="change" items="${baseCommand.commands}">
	<div class="commandBox"><soak:renderBean bean="${change}" /></div>
</c:forEach>
<a class="actionlink"
	href="<c:url value="/flow/undo-command-flow.flow"> <c:param name="commandId" value="${command.id}"/></c:url>">undo
this command.</a>
</td>

<%@ include file="/footer.include.jsp"%>