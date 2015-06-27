<%@ include file="/base.include.jsp"%>
<c:choose>
	<c:when test="${empty bean.aclEntries}">
No permissions set
</c:when>
	<c:otherwise>
		<table class="listTable">
			<thead>
				<tr>
					<th>OU</th>
					<th>Permission</th>
			</thead>
			<tbody>
				<c:forEach var="entry" items="${bean.aclEntries }">
					<tr>
						<td><a
							href="<c:url value="/orgunit/showOrgUnit.do"><c:param name="id" value="${entry.key.id }"/></c:url>">${entry.key.name
						}</a></td>
						<td>${entry.value}</td>

					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>