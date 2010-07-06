
<%
	pageContext.setAttribute("TITLE", "Host updated");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="infoBox">Changes applied</div>

<soak:renderBean bean="${changeResult.baseChange}" />

<c:if test="${ ! empty changeResult.aggregateChanges}">
	<c:set scope="request" var="changes"
		value="${changeResult.aggregateChanges}" />
	<jsp:include page="../host/changesFragment.jsp" />
</c:if>

<a class="actionlink"
	href="<c:url value="/flow/undo-command-flow.flow">  <c:param name="commandId" value="${changeResult.commandId}"/></c:url>">Undo
this change</a>
<a class="actionlink"
	href="<c:url value="/host/show.do"> <c:param name="id" value="${changeResult.baseChange.newHost.id}" /></c:url>">Show
host</a>
<a class="actionlink"
	href="<c:url value="/flow/update-host-flow.flow"> <c:param name="id" value="${changeResult.baseChange.newHost.id}" /></c:url>">Edit
host</a>
<a class="actionlink" href="<c:url value="/host/search.do"/> ">Search
hosts</a>
<%@ include file="/footer.include.jsp"%>
