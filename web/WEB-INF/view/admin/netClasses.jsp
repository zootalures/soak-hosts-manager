
<%
	pageContext.setAttribute("TITLE", "Admin: Network Classes");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<table>
	<tr>
		<th>ID</th>
		<th>Name</th>
		<th>Subnets</th>
		<th>Description</th>
		<th>Allowed Host types</th>
		<th>Allowed Org. Units</th>
	</tr>

	<c:forEach items="${networkClasses}" var="nc" varStatus="st">
		<tr class="${st.index%2==0?'even':'odd' }">
			<td>${nc.id }</td>
			<td>${nc.name }</td>
			<td>${fn:length(nc.subnets) }</td>
			<td>${nc.description }</td>
			<td><c:forEach items="${hostClasses}" var="hc">
				<c:if test="${soak:contains(nc.allowedHostClasses,hc) }">
					${hc.name }<br />
				</c:if>
			</c:forEach></td>

			<td><c:forEach items="${nc.orgUnitAcl.aclEntries}" var="entry">
				<c:if test="${entry.value == 'ALLOWED'}">
					${entry.key.name }<br/>
				</c:if>
			</c:forEach></td>
			<td><a class="editButton"
				href="<c:url value="/admin/editNC.do?id=${nc.id}"/>"><span>edit</span></a>
			<a class="deleteButton"
				href="<c:url value="/admin/deleteNC.do?id=${nc.id}"/>"><span>delete</span></a>
			<a title="Edit permissions for network class  '${nd.suffix}'"
				class="editPermsButton"
				href="<c:url value="/admin/editOrgUnitAclEntity.do" >
							<c:param name="id">${nc.id}</c:param>
							<c:param name="type">networkClass</c:param>
							<c:param name="returnURL">/admin/netClasses.do</c:param>
</c:url>">
			<span>edit permissions</span></a></td>
		</tr>
	</c:forEach>
</table>
<soak:helpLink path="Network Classes" title="About network classes" />
<a class="actionlink" href="<c:url value="/admin/editNC.do"/>"> New
network class</a>

<a class="actionlink"
	href="<c:url value="/admin/editNCHCPermissions.do"/>"> Edit All Network
class/Host type mappings </a>
<%@ include file="/footer.include.jsp"%>
