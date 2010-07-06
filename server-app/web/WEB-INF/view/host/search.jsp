
<%
	pageContext.setAttribute("TITLE", "Search hosts");
	pageContext.setAttribute("cmdMenu", "host-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<!--  Expanded search URL  -->
<c:set scope="request" var="searchUrl">
	<c:url value="/host/search.do">
		<c:param name="searchTerm" value="${s.searchTerm }" />
		<c:param name="hostClass" value="${s.hostClass.id }" />
		<c:param name="subnet" value="${s.subnet.id}" />
		<c:param name="orgUnit" value="${s.orgUnit.id }" />
		<c:param name="nameDomain" value="${s.nameDomain.suffix }" />
		<c:param name="onlyIncludeMyHosts" value="${s.onlyIncludeMyHosts }" />
	</c:url>
</c:set>
<c:set scope="request" var="starSearchURL">
	<c:url value="/host/starSearch.do">
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
<!--  Local Search URL  URL  -->
<c:set scope="request" var="baseSearchUrl" value="/host/search.do" />

<jsp:include page="searchBoxFragment.jsp" />


<c:set scope="request" var="showLIU" value="${true }" />
<c:if test="${null== results }">
	<soak:helpLink path="Searching Hosts" title="Searching Hosts" />
</c:if>

${starSearchUrl }
<c:if test="${null!= results }">
	Searching for <soak:renderBean bean="${s}" />
	<br>
	<c:if test="${empty  results.results }">
No results found for search. 
</c:if>
	<c:if test="${!empty  results.results }">
		<jsp:include page="searchPagesFragment.jsp" />

		<a href="#"
			onClick="SoakStarred.selectSearch('${starSearchURL}&value=true',true);return false;">Select
		all </a>
|
<a href="#"
			onClick="SoakStarred.selectSearch('${starSearchURL}&value=false',false);return false;">De-select
		all </a>
|
<a href="${searchUrl}&display=csv">Export CSV</a>

		<jsp:include page="hostSearchResultsFragment.jsp" />

		<jsp:include page="searchPagesFragment.jsp" />



		<a href="#"
			onClick="SoakStarred.selectSearch('${starSearchURL}&value=true',true);return false;">Select
		all </a>
|
<a href="#"
			onClick="SoakStarred.selectSearch('${starSearchURL}&value=false',false);return false;">De-select
		all </a>

|
<a href="${searchUrl}&display=csv">Export CSV</a>

	</c:if>
</c:if>

<%@ include file="/footer.include.jsp"%>