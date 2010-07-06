
<%
	pageContext.setAttribute("TITLE", "Edit permissions for object ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

You are editing permissions for the following object:
<soak:renderBean bean="${editOrgUnitAclEntityCommand.entity}" />
<hr>
<c:set var="formUrl">
	<c:url value="/admin/editOrgUnitAclEntity.do" />
</c:set>
<form:form commandName="editOrgUnitAclEntityCommand">
	<form:hidden path="type" />
	<form:hidden path="id" />

	<table>
		<thead>
			<tr>
				<th>Org Unit</th>
				<th>Default</th>
				<th>Denied</th>
				<th>Allowed</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${orgUnits}" var="ou">
				<tr>
					<td>${ou.name }</td>

					<td class="${(defaultPerms[ou] eq 'ALLOWED')? "allowed" : "denied" }"><form:radiobutton
						path="acl[${ou.id}]" value="${null}" /></td>
					<td class="denied"><form:radiobutton path="acl[${ou.id}]"
						value="DENIED" /></td>
					<td class="allowed"><form:radiobutton path="acl[${ou.id}]"
						value="ALLOWED" /></td>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	<input type="submit" value="Save" />
</form:form>



<%@ include file="/footer.include.jsp"%>