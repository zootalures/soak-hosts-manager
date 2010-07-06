
<%
	pageContext.setAttribute("TITLE", "Records deleted");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="infoBox">The following DNS records have been deleted:</div>
<table class="listTable">
	<tbody>
		<c:forEach items="${command.toDelete}" var="rec">
			<tr>
				<td>${rec.zone.displayName }</td>
				<td>${rec.hostName }</td>
				<td>${rec.type }</td>
				<td>${rec.ttl }</td>
				<td>${rec.target }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<%@ include file="/footer.include.jsp"%>
