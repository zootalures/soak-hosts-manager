
<%
	pageContext.setAttribute("TITLE", "Create/Edit  Vlan ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="vlan" method="post">
	<spring:hasBindErrors name="vlan">
Please fix all errors before continuing.
</spring:hasBindErrors>
	<form:errors>
	</form:errors>
	<table>
		<tr>
			<td><label for="name"> Number </label></td>
			<td><form:input path="number" /> <form:errors path="number"
				cssClass="errorBox" /></td>
		</tr>
	
		<tr>
			<td><label for="name"> Name </label></td>
			<td><form:input path="name" /> <form:errors path="name"
				cssClass="errorBox" /></td>
		</tr>
	
		<tr>
			<td><label for="description">Description </label></td>
			<td><form:textarea cols="60" path="description" /> <form:errors
				path="description" cssClass="errorBox" /></td>
		</tr>

	</table>
	<input type="submit" value="Save vlan" />
</form:form>

<%@ include file="/footer.include.jsp"%>
