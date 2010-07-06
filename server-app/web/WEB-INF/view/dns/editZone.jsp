
<%
	pageContext.setAttribute("TITLE", "Add/Update DNS Zone ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="zone">
	<table>
		<tbody>
			<tr>
				<td><label>Display Name </label></td>
				<td><form:input path="displayName" /> <form:errors path="displayName" /></td>
			</tr>

			<tr>
				<td><label>Suffix </label></td>
				<td><form:input path="domain" /> <form:errors path="domain" /></td>
			</tr>
			<tr>
				<td><label>Server IP</label></td>
				<td><form:input path="serverIP" /> <form:errors
					path="serverIP" /></td>
			</tr>
			<tr>
				<td><label>Server Port</label></td>
				<td><form:input path="serverPort" /> <form:errors
					path="serverPort" /></td>
			</tr>
			<tr>
				<td><label>Default Zone TTL </label></td>
				<td><form:input path="defaultTTL" /> <form:errors path="defaultTTL" /></td>
			</tr>
			<tr>
				<td><label>TSIG Key </label></td>
				<td><form:input path="sigKey" /> <form:errors path="sigKey" /></td>
			</tr>

			<tr>
				<td><label>Use TCP </label></td>
				<td><form:checkbox path="useTCP" /> <form:errors path="useTCP" /></td>
			</tr>
			<tr>
				<td><label>Don't match the following domains </label></td>
				<td><form:textarea cols="50" rows="5" path="ignoreHostRegexps" /><br/>
				(Enter a list of regular expressions, one per line)<form:errors
					path="ignoreHostRegexps" /></td>
			</tr>
			<tr>
				<td><label>Don't match the following record bodies </label></td>
				<td><form:textarea cols="50" rows="5" path="ignoreTargetRegexps" /><br/>
				(Enter a list of regular expressions, one per line)<form:errors
					path="ignoreTargetRegexps" /></td>
			</tr>

		</tbody>
	</table>
	<input type="submit" name="submit" value="Save zone" />
</form:form>

<%@ include file="/footer.include.jsp"%>
