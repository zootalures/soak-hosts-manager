
<%
	pageContext.setAttribute("TITLE", "Change Maintenance Mode");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="infoBox">The current maintenance mode is : <i><c:choose>
	<c:when test="${maintenanceMode eq 'NORMAL' }">
Normal operation (no maintenance restrictions)
	</c:when>

	<c:when test="${maintenanceMode eq 'ADMIN_MAINTENANCE' }">
	Administrative maintenance (no non-admin host changes).
	</c:when>
	<c:when test="${maintenanceMode eq 'FULL_MAINTENANCE' }">
	Full maintenance (no host changes by anyone).
	</c:when>
</c:choose> </i></div>
<form:form commandName="command" method="post">
	<label> Change maintenance mode to: </label>
	<form:select path="newMode">
		<form:option value="NORMAL">Normal operation (no maintenance restrictions)</form:option>
		<form:option value="ADMIN_MAINTENANCE">Administrative maintenance (no non-admin host changes).</form:option>
		<form:option value="FULL_MAINTENANCE">Full maintenance (no host changes by anyone).</form:option>
	</form:select>
	<input type="submit" value="Set maintenance mode" />
</form:form>


<%@ include file="/footer.include.jsp"%>
