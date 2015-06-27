<%@ include file="/base.include.jsp"%>
<table>
	<tr>
		<td><label for="name"> Name </label></td>
		<td>${bean.name}</td>
	</tr>
	<c:if test="${ view != 'short' }">
		<tr>
			<td><label for="description">Description </label></td>
			<td><c:out value="${bean.description}" /></td>
		</tr>
	</c:if>
	<tr>
		<td><label for="baseaddress">CIDR Address </label></td>
		<td><c:out value="${bean.minIP.hostAddress}" />/<c:out
			value="${bean.maskBits}" /></td>
	</tr>

	<tr>
		<td><label for="vlan"> Vlan </label></td>
		<td><c:if test="${ !empty bean.vlan}">
			<a
				href="<c:url value="/vlan/show.do"><c:param name="id" value="${bean.vlan.id}"/></c:url>"><B>${bean.vlan.number} : ${bean.vlan.name}</B></a>
		</c:if></td>
	</tr>

	<tr>
		<td><label for="vlan"> Subnet mask </label></td>
		<td>${bean.subnetMask.hostAddress}</td>
	</tr>
	<c:if test="${ view != 'short' }">
		<tr>
			<td><label for="vlan"> Useable addresses </label></td>
			<td>${bean.numUseableAddresses}</td>
		</tr>
	</c:if>


	<c:if test="${ view != 'short' }">
		<tr>
			<td><label for="vlan"> Network type </label></td>
			<td>${bean.networkClass.name}</td>
		</tr>
	</c:if>

	<tr>
		<td><label for="gateway">Default gateway </label></td>
		<td>${bean.gateway.hostAddress}</td>
	</tr>

	<c:if test="${ view != 'short' }">

		<tr>
			<td><label for="noScan">Remove from scans </label></td>
			<td>${bean.noScan}</td>
		</tr>
	</c:if>

</table>