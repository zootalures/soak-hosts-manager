
<%
	pageContext.setAttribute("TITLE", "Clean up unused DNS Records");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="command">
	<form:errors path="zones" cssClass="errorBox" />
	Select the zones which you want to clean up records for. (This operation may take some time) 
	
	
	<table class="listTable">
		<tbody>
			<c:forEach items="${dnsZones}" var="zone">
				<tr>
					<td>${zone.displayName }</td>
					<td>${zone.domain}</td>
					<td><form:checkbox path="zones" value="${zone.id }" /></td>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<input type="submit" name="_eventId_submit"
		value="Search for unused records" />
</form:form>
<%@ include file="/footer.include.jsp"%>
