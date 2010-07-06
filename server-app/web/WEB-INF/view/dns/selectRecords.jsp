
<%
	pageContext.setAttribute("TITLE", "Clean up unused DNS Records");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="command">
	<form:errors path="zones" cssClass="errorBox" />
	According to the hosts database, the following host-related records are unused:
	<table class="listTable">
		<tbody>
			<c:forEach items="${unusedRecords}" var="rec">
				<tr>
					<td>${rec.zone.displayName }</td>
					<td>${rec.hostName }</td>
					<td>${rec.type }</td>
					<td>${rec.ttl }</td>
					<td>${rec.target }</td>
					<td><form:checkbox path="toDelete" value="${rec.id }" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<input type="submit" name="_eventId_back" value="Go back" />
	<input type="submit" name="_eventId_submit"
		value="Preview records to delete" />
</form:form>
<%@ include file="/footer.include.jsp"%>
