
<%
	pageContext.setAttribute("TITLE", "Host Deleted");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="infoBox">Changes applied</div>
The Following host has been deleted:
<soak:renderBean bean="${changeResult.baseChange.host}" />

<c:if test="${ ! empty changeResult.aggregateChanges}">
	<c:set scope="request" var="changes"
		value="${changeResult.aggregateChanges}" />
	<jsp:include page="../host/changesFragment.jsp" />
</c:if>

<a class="actionlink"
	href="<c:url value="/flow/update-host-flow.flow"/>">New
host</a>
<a class="actionlink" href="<c:url value="/host/search.do"/> ">Search
hosts</a>
<%@ include file="/footer.include.jsp"%>
