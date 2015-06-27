
<%
	pageContext.setAttribute("TITLE",
			"Admin: Organisational Units/Users");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<c:set var="returnURL">/admin/orgUnits.do</c:set>
<div>
<form action="<c:url value="/user/details.do"/>" method="GET"><label>Show
details for specific user </label> <input type="text" name="user" /> <input
	type="submit" value="go" /></form>
</div>
<br>

<table class="listTable">
	<thead>
		<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Admins</th>
			<th>Hosts</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${orgUnits}" var="ou" varStatus="st">

			<tr class="${st.index%2==0?'even':'odd' }">
				<td>${ou.id }</td>
				<td><a
					href="<c:url value="/orgunit/showOrgUnit.do"><c:param name="id" value="${ou.id}"/></c:url>">${ou.name
				}</a></td>
				<td><c:forEach items="${orgUnitMappings[ou]}" var="mapping">
					<c:choose>
						<c:when test="${mapping.type == 'USER' }">
							<b> ${mapping.principal }</b>
						</c:when>
						<c:otherwise>
				group: ${mapping.principal }
				</c:otherwise>
					</c:choose>
					<br />
				</c:forEach></td>
				<td><a
					href="<c:url value="/host/search.do"><c:param name="orgUnit">${ou.id}</c:param> </c:url>">${hostCounts[ou]}</a></td>
				<td><a class="showButton"
					href="<c:url value="/orgunit/showOrgUnit.do"><c:param name="id" value="${ou.id}"/></c:url>"><span>show</span></a>
				<a class="editButton"
					href="<c:url value="/admin/editOU.do"><c:param name="id" value="${ou.id}"/> <c:param name="returnURL">${returnURL}?flash=orgunit-saved</c:param></c:url>"><span>edit
				permissions</span></a> <a class="editPermsButton"
					href="<c:url value="/admin/editOrgUnitAcls.do"><c:param name="id" value="${ou.id}"/> <c:param  name="returnURL">${returnURL}?flash=acls-updated</c:param></c:url>"><span>show</span></a>
				<a class="deleteButton"
					href="<c:url value="/admin/deleteOU.do"><c:param name="id" value="${ou.id}"/></c:url>"><span>delete</span></a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<a class="actionlink" href="<c:url value="/admin/editOU.do"/>"> New
organisational unit</a>
<soak:helpLink path="Organisational Units"
	title="About organisation units" />
<%@ include file="/footer.include.jsp"%>
