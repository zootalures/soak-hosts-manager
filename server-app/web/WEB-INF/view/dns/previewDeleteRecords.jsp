
<%
	pageContext.setAttribute("TITLE", "Preview record deletion");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
The following DNS records will be deleted:
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


<c:if test="${ ! empty preview.aggregateChanges}">

	<c:set scope="request" var="changes"
		value="${preview.aggregateChanges}" />
	<jsp:include page="../host/changesFragment.jsp" />
</c:if>
<form:form commandName="command">

	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<input type="submit" name="_eventId_back" value="Go back" />
	<input type="submit" name="_eventId_submit"
		value="Delete selected records" />
</form:form>
<%@ include file="/footer.include.jsp"%>
