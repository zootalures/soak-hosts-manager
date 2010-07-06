
<%
	pageContext.setAttribute("TITLE", "Admin: Host Types");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<a class="actionlink"
	href="<c:url value="/admin/editNDPermissions.do"/>"> Edit name
domain/Host type permissions</a>

<a class="actionlink" href="<c:url value="/admin/editHC.do"/>">New
Host Class</a>
<table>
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Description</th>
		<th>DHCP</th>
		<th>Aliases</th>
		<th>Hosts</th>
		<th>Allowed Org. Units</th>

	</tr>
	<c:forEach items="${hostClasses}" var="hc" varStatus="st">
		<tr class="${st.index%2==0?'even':'odd' }">
			<td>${hc.id }</td>
			<td>${hc.name }</td>
			<td><soak:trimText maxLength="20" value="${hc.description }" /></td>
			<td>${hc.DHCPStatus }</td>
			<td><c:choose>
				<c:when test="${hc.canHaveAliases eq true }">
					Yes
				</c:when>
				<c:otherwise>
				No
				</c:otherwise>
			</c:choose></td>
			<td><a
				href="<c:url value="/host/search.do"><c:param name="hostClass">${hc.id}</c:param> </c:url>">${hostCounts[hc]}</a></td>

			<td><c:forEach items="${hc.orgUnitAcl.aclEntries}" var="entry">
				<c:if test="${entry.value == 'ALLOWED'}">
					${entry.key.name }<br>
				</c:if>
			</c:forEach></td>
			<td><a class="editButton"
				href="<c:url value="/admin/editHC.do?id=${hc.id}"/>"><span>edit</span></a>
			<a class="deleteButton"
				href="<c:url value="/admin/deleteHC.do?id=${hc.id}"/>"><span>delete</span></a>
			<a title="Edit permissions for host class  '${hc.name}'"
				class="editPermsButton"
				href="<c:url value="/admin/editOrgUnitAclEntity.do" >
							<c:param name="id">${hc.id}</c:param>
							<c:param name="type">hostClass</c:param>
							<c:param name="returnURL">/admin/hostClasses.do?flash=acls-updated</c:param>
</c:url>">
			<span>edit permissions</span></a></td>
		</tr>
	</c:forEach>
</table>


<%@ include file="/footer.include.jsp"%>
