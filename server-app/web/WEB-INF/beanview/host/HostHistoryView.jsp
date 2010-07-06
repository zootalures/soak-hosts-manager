<%@ include file="/base.include.jsp"%>
<table class="listTable">
	<thead>
		<tr>
			<th>Date</th>
			<th>Change Type</th>
			<th>Host name</th>
			<th>IP</th>
			<th>OU</th>
			<th>User</th>
			<th>Command</th>
			<th>Comment</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${bean.changes.results}" var="hostChange">
			<tr>
				<td><fmt:formatDate type="both"
					value="${hostChange.changeDate}" /></td>
				</td>
				<td><c:choose>
					<c:when test="${hostChange.changeType eq 'ADD' }">
							Created
						</c:when>
					<c:when test="${hostChange.changeType eq 'DELETE' }">
						Deleted
						</c:when>
					<c:when test="${hostChange.changeType eq 'CHANGE' }">
						<a
							href="<c:url value="/host/showChange.do"><c:param name="id" value="${hostChange.id }"/></c:url>">
						Edited</a>
					</c:when>
				</c:choose></td>
				<td><soak:displayChange
					before="${bean.hostBefore[hostChange].hostName}"
					after="${hostChange.hostName}" /></td>
				<td><soak:displayChange
					before="${bean.hostBefore[hostChange].ipAddress.hostAddress}"
					after="${hostChange.ipAddress.hostAddress }" /></td>
				<td><soak:displayChange
					before="${bean.hostBefore[hostChange].ownership.orgUnit.id}"
					after="${hostChange.orgUnit.id }" /></td>

				<td>${hostChange.userId }</td>
				<td><c:if test="${not empty hostChange.commandId}">
					<a
						href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${hostChange.commandId}"/></c:url>">
					${hostChange.commandDescription }</a>
				</c:if></td>
				<td>${hostChange.changeComments }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<a
	href="<c:url value="/changes/search.do"><c:param name="searchTerm" value="id:${bean.host.id }"/></c:url>" />
Show all changes for this host</a>
