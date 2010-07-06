<%@ include file="/base.include.jsp"%>
<div class="cmdListEntry hostsDbCmdEntry">
<div class="cmdListTitle">Hosts database</div>

<div><c:choose>

	<c:when test="${bean.creation }">
Create host  <b>${bean.host.hostName }</b> in database.

	</c:when>
	<c:otherwise>
Save changes to host <b>${bean.host.hostName }</b> to database.
	</c:otherwise>
</c:choose></div>
</div>
