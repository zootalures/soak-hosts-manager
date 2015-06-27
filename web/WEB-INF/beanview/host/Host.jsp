<%@ include file="/base.include.jsp"%>
<table>
	<tr>
		<td><label> Host name</label></td>
		<td>${bean.hostName }</td>

	</tr>


	<c:if test="${! empty  bean.description}">
		<tr>
			<td><label>Description </label></td>
			<td>${bean.description}</td>
		</tr>
	</c:if>
	<c:if test="${! empty  bean.hostAliases}">
		<tr>
			<td><label>Aliases</label></td>
			<td><c:forEach items="${bean.hostAliases}" var="alias">
				${alias.alias} ( ${alias.type}  )<br />
			</c:forEach></td>
		</tr>
	</c:if>
	<tr>
		<td><label> Host type</label></td>
		<td>${bean.hostClass.name }</td>
	</tr>
	<tr>
		<td><label> IP address</label></td>
		<td>${bean.ipAddress.hostAddress }</td>
	</tr>
	<tr>
		<td><label> Owner</label></td>
		<td>${bean.ownership.orgUnit.name }</td>
	</tr>

	<c:if test="${! empty  bean.macAddress}">
		<tr>
			<td><label> MAC address</label></td>
			<td>${bean.macAddress }</td>
		</tr>
	</c:if>
	<c:if
		test="${! empty  bean.location.room or! empty  bean.location.building }">
		<tr>
			<td><label> Location</label></td>
			<td>${bean.location }</td>
		</tr>
	</c:if>
</table>

