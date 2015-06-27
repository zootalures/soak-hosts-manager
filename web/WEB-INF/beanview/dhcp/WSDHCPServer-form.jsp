<%@ include file="/base.include.jsp"%>
<table>
	<tbody>
		<tr>
			<td><label> Display Name</label></td>
			<td><form:input path="displayName" /> <form:errors cssClass="errorBox"
				path="displayName" /></td>
		</tr>
		<tr>
			<td><label> DHCP Server IP</label></td>
			<td><form:input path="serverIP" /><form:errors cssClass="errorBox"
				path="serverIP" /></td>
		</tr>
		<tr>
			<td><label> Web service URL</label></td>
			<td><form:input path="agentUrl" /><form:errors cssClass="errorBox"
				path="agentUrl" /></td>
		</tr>
		<tr>
			<td><label> Remote Username</label></td>
			<td><form:input path="userName" /><form:errors cssClass="errorBox"
				path="userName" /></td>
		</tr>
		<tr>
			<td><label> Remote Password</label></td>
			<td><form:password path="password"  /><form:errors cssClass="errorBox"
				path="password" /></td>
		</tr>
	</tbody>
</table>