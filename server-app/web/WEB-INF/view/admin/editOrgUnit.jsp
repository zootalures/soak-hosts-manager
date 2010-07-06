
<%
	pageContext.setAttribute("TITLE", "Create or edit organisational unit");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="editOrgUnitCmd">
	<form:hidden path="creation" />
	
	<div class="infoBox"> Users will need to log-out and log back in again for changes to take effect</div>
	<table>
		<tbody>
			<tr>
				<td><label>Identifier (Short name) </label></td>

				<td><c:choose>
					<c:when test="${editOrgUnitCmd.creation }">
						<form:input path="orgUnit.id" />
						<form:errors path="orgUnit.id" />
					</c:when>
					<c:otherwise>
						<form:hidden path="orgUnit.id" />
						<b>${editOrgUnitCmd.orgUnit.id }</b>
					</c:otherwise>
				</c:choose></td>
			</tr>

			<tr>
				<td><label>Display Name </label></td>
				<td><form:input path="orgUnit.name" /> <form:errors
					path="orgUnit.name" /></td>
			</tr>
			<tr>
				<td><label>Allow administration from the following Users </label></td>
				<td><form:textarea path="users" /> <form:errors
					path="users" /><br />
				(Enter a list of user names, one per line)</td>
			</tr>

			<tr>
				<td><label>Allow administration from the following LDAP groups </label></td>
				<td><form:textarea path="groups" /> <form:errors
					path="groups" /><br />
				(Enter a list of group names, one per line)</td>
			</tr>
		</tbody>
	</table>
	<input type="submit" name="submit" value="Save organisational unit  " />
</form:form>


<%@ include file="/footer.include.jsp"%>
