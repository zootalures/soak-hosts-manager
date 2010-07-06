
<%
	pageContext.setAttribute("TITLE", "Admin: Name Domains");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<a class="actionlink"
	href="<c:url value="/admin/editNDPermissions.do"/>"> Edit name
domain/Host type permissions</a>
<a class="actionlink" href="<c:url value="/admin/editND.do"/>">New
name domain</a>

<table>
	<thead>
		<tr>
			<th>Suffix</th>
			<th>Hosts</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${nameDomains}" var="nd" varStatus="st">
			<tr>
				<td>${nd.suffix}</td>
				<td><a
					href="<c:url value="/host/search.do"><c:param name="nameDomain">${nd.suffix}</c:param> </c:url>">${hostCounts[nd]}</a></td>

				<td><a title="Delete name domain '${nd.suffix}'"
					class="deleteButton"
					href="<c:url value="/admin/deleteND.do" >
				<c:param name="suffix">${nd.suffix}</c:param>
</c:url>">
				<span>delete</span></a> <a
					title="Edit permissions for name domain  '${nd.suffix}'"
					class="editPermsButton"
					href="<c:url value="/admin/editOrgUnitAclEntity.do" >
							<c:param name="id">${nd.suffix}</c:param>
							<c:param name="type">nameDomain</c:param>
							<c:param name="returnURL">/admin/nameDomains.do</c:param>
</c:url>">
				<span>edit permissions</span></a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

</div>

<%@ include file="/footer.include.jsp"%>
