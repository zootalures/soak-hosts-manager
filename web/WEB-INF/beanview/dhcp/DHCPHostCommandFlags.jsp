<%@ include file="/base.include.jsp"%>
<c:if test="${bean.refreshDHCP}">
<h4>DHCP options</h4>
	<table>
		<tr>
			<td><label for="refreshDHCP">Refresh DHCP Reservations
			for host</label></td>
			<td><b> yes</b></td>
		</tr>
	</table>
</c:if>