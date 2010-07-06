<%@ include file="/base.include.jsp"%>
Web-service based DHCP server
<table class="infotable">
	<tbody>
		<tr>
			<td><label> Display Name</label></td>
			<td>${bean.displayName }</td>
		</tr>
		<tr>
			<td><label> DHCP Server IP</label></td>
			<td>${bean.serverIP.hostAddress }</td>
		</tr>
		<tr>
			<td><label> Web service URL</label></td>
			<td>${bean.agentUrl }</td>
		</tr>
		<tr>
			<td><label> Scopes last synchronized</label></td>
			<td><fmt:formatDate type="both" value="${bean.lastSubnetsFetched }"/></td>
		</tr>
		
	</tbody>
</table>