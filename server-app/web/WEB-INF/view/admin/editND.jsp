
<%
	pageContext.setAttribute("TITLE", "Create name domain");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="editNameDomainCmd">
	<form:hidden path="creation" />
	<table>
		<tbody>
			<tr>
				<td><label>Suffix </label></td>

				<td><form:input path="nameDomain.suffix" /> <form:errors
					path="nameDomain.suffix" /></td>
			</tr>
	</table>
	<input type="submit" name="submit" value="Save name domain " />
</form:form>


<%@ include file="/footer.include.jsp"%>
