<%@ include file="/base.include.jsp"%>

<h3>Maintenance Mode</h3>
<div class="infoBox">The current maintenance mode is : <i><c:choose>
	<c:when test="${bean eq 'NORMAL' }">
Normal operation (no maintenance restrictions)
	</c:when>

	<c:when test="${bean eq 'ADMIN_MAINTENANCE' }">
	Administrative maintenance (no non-admin host changes).
	</c:when>
	<c:when test="${bean eq 'FULL_MAINTENANCE' }">
	Full maintenance (no host changes by anyone).
	</c:when>
</c:choose> </i></div>

<a class="actionlink"
	href="<c:url value="/admin/maintenanceMode/setMaintenanceMode.do"/>">
Change the current maintenance mode.</a>

