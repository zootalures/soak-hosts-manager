
<%
	pageContext.setAttribute("TITLE", "Selected hosts");
	pageContext.setAttribute("cmdMenu", "host-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<c:set scope="request" var="searchUrl">
	<c:url value="/host/showStarred.do">
		<c:param name="searchTerm" value="${s.searchTerm }" />
		<c:param name="hostClass" value="${s.hostClass.id }" />
		<c:param name="subnet" value="${s.subnet.id}" />
		<c:param name="orgUnit" value="${s.orgUnit.id }" />
		<c:param name="nameDomain" value="${s.nameDomain.suffix }" />
		<c:param name="onlyIncludeMyHosts" value="${s.onlyIncludeMyHosts }" />
	</c:url>
</c:set>
<c:set var="searchUrlOrder" scope="request"
	value="&orderBy=${s.orderBy}&ascending=${s.ascending}" />
	
<c:set scope="request" var="baseSearchUrl" value="/host/showStarred.do" />

<c:choose>
	<c:when test="${empty  starredHostIds }">
No hosts selected.  To select a host click on the <img
			src="<c:url value="/images/starred.png"/>" /> star next to a host in the host or search view.  
</c:when>

	<c:when test="${!empty  starredHostIds }">
		<div id="starredSearchBox"><jsp:include
			page="searchBoxFragment.jsp" /></div>
		<c:if test="${null!= results }">
			<br>
	Searching <img src="<c:url value="/images/starred.png"/>">selected hosts  for <soak:renderBean
				bean="${s}" />. 
			<a href='#' onClick="SoakStarred.clearSelection()"> Clear
			Selection </a>.
			<br>
			<c:if test="${empty  results.results }">
No results found for search. 
</c:if>
			<c:if test="${!empty  results.results }">
				<jsp:include page="searchPagesFragment.jsp" />
				<jsp:include page="hostSearchResultsFragment.jsp" />
			</c:if>
		</c:if>
	</c:when>
</c:choose>


<%@ include file="/footer.include.jsp"%>