
<%
	pageContext.setAttribute("TITLE", "Create or edit host type");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="editHostClassCmd">
	<form:hidden path="creation" />
	<table>
		<tbody>
			<tr>
				<td><label>Identifier (Short name) </label></td>

				<td><c:choose>
					<c:when test="${editHostClassCmd.creation }">
						<form:input path="hostClass.id" />
						<form:errors path="hostClass.id" />
					</c:when>
					<c:otherwise>
						<form:hidden path="hostClass.id" />
						<b>${editHostClassCmd.hostClass.id }</b>
					</c:otherwise>
				</c:choose></td>
			</tr>

			<tr>
				<td><label>Display Name </label></td>
				<td><form:input path="hostClass.name" /> <form:errors
					path="hostClass.name" /></td>
			</tr>

			<tr>
				<td><label>DHCP Status </label></td>
				<td><form:select path="hostClass.DHCPStatus">
					<form:option value="" label="Select DHCP Status" />
					<form:options items="${dhcpStatuses}" />
				</form:select> <form:errors path="hostClass.DHCPStatus" /></td>
			</tr>
			<tr>
				<td><label>Description</label></td>
				<td><form:textarea rows="5" cols="50"
					path="hostClass.description" /> <form:errors
					path="hostClass.description" /></td>
			</tr>

			<tr>
				<td><label>Aliases allowed</label></td>
				<td><form:checkbox path="hostClass.canHaveAliases" /> <form:errors
					path="hostClass.canHaveAliases" />
				(N.B. This only applies to new hosts and new aliases)</td>
			</tr>
			<tr>
				<td><label>Permitted name formats for new hosts </label></td>
				<td><form:textarea path="hostClass.allowedNamePatterns" /> <form:errors
					path="hostClass.allowedNamePatterns" /><br />
				(Enter a list of regular expressions, one per line, leave blank to
				allow any format of name )</td>
			</tr>
			<tr>
				<td><label>Example host name </label></td>
				<td><form:input path="hostClass.exampleName" /> <form:errors
					path="hostClass.exampleName" /></td>
			</tr>
		</tbody>
	</table>
	<input type="submit" name="submit" value="Save host type " />
</form:form>


<%@ include file="/footer.include.jsp"%>
