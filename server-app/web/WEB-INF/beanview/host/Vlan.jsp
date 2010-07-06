<%@ include file="/base.include.jsp"%>
<table>
	<tr>
		<td><label for="name"> Vlan number </label></td>
		<td>${bean.number}</td>
	</tr>

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
</table>