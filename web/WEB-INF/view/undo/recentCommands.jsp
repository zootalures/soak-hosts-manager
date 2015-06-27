
<%
	pageContext.setAttribute("TITLE", "Command History");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<c:set scope="request" var="searchUrl">
	<c:url value='/user/recentCommands.do' />
</c:set>
<!--  Local Search URL  URL  -->
<c:set scope="request" var="baseSearchUrl"
	value="/undo/recentCommands.do" />
Display results ${results.firstResultOffset+1 } -
${results.lastResultOffset } / ${results.totalResults }
<br />

 
<table class="listTable">
	<thead>
		<tr>
			<th>Date</th>
			<th>Command</th>
			<th>Command comment</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach varStatus="status" var="cmd" items="${results.results}">
			<tr>
				<td><fmt:formatDate type="both" value="${cmd.changeTime}" /></td>
				<td>${cmd.commandDescription}</td>
				<td>${cmd.changeComments}</td>
				<td><a class="showButton"
					href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${cmd.id }"/></c:url>"><span>show</span></a>
				<a
					href="<c:url value="/flow/undo-command-flow.flow"> <c:param name="commandId" value="${cmd.id}"/></c:url>">undo</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<%@ include file="/footer.include.jsp"%>