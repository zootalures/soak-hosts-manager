<%@ include file="/base.include.jsp"%>
<h4>DHCP Options</h4>
<table>
	<tr>
		<td><label for="forceRefresh">Force DHCP refresh:</label></td>
		<td><form:checkbox path="${objectBase}.refreshDHCP" /></td>
	</tr>
</table>