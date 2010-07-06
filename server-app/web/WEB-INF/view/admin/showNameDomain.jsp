showNameDomain.jsp
<%
	pageContext.setAttribute("TITLE", "Configuration");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<table>

<c:forEach items="${nameDomains}" var="nd" varStatus="st">
<tr><td>${nd.suffix} </td><td><a title="Show name domain '${nd.suffix}'" class="showButton"
						href="<c:url value="/admin/showNd.do" >
				<c:param name="id">${nd.suffix}</c:param>
</c:url>">
					<span>show</span></a>

					<a title="Edit permissions for name domain  '${nd.suffix}'"
						class="editPermsButton"
						href="<c:url value="/admin/editOrgUnitAclEntity.do" >
							<c:param name="id">${nd.suffix}</c:param>
							<c:param name="type">nameDomain</c:param>
							<c:param name="returnURL">/admin/nameDomains.do</c:param>
</c:url>">
					<span>edit permissions</span></a></td></tr>
</c:forEach>

</table>

<a class="actionlink"
	href="<c:url value="/admin/editNDPermissions.do"/>"> Edit name
domain/Host class allocations</a> <a class="actionlink"
	href="<c:url value="/admin/newNd.do"/>">New name domain</a></div>

<%@ include file="/footer.include.jsp"%>
