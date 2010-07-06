
<%
	pageContext.setAttribute("TITLE", "Create multiple Hosts ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="infoBox">The following hosts have been created:</div>

<c:set var="hostList" value="${command.hosts  }" scope="request" />
<jsp:include page="bulkHostListFragment.jsp" />
<hr />

<c:if test="${ ! empty result.aggregateChanges}">

	<c:set scope="request" var="changes" value="${result.aggregateChanges}" />
	<jsp:include page="../host/changesFragment.jsp" />
</c:if>


<br />
<%@ include file="/footer.include.jsp"%>