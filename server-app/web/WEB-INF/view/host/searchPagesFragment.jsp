<%@ include file="/base.include.jsp"%>
Display results ${results.firstResultOffset+1 } - ${
results.lastResultOffset } / ${results.totalResults }
<br />

<c:set var="currentPageNumber"
	value="${results.currentPage.pageNumber }" />
<c:set var="totalPages" value="${fn:length(results.searchPages) }" />
<c:if test="${totalPages > 1}">
	<c:set var="maxPage"
		value="${(currentPageNumber < 10)?20:(currentPageNumber + 10)}" />
	<c:set var="minPage"
		value="${(currentPageNumber > (totalPages - 10))?(totalPages - 20):(currentPageNumber - 10)}" />
Page:		<c:forEach items="${results.searchPages }" var="page">

		<c:choose>

			<c:when test="${page eq results.currentPage}">${page.pageNumber+1}</c:when>
			<c:when
				test="${page.pageNumber <= maxPage && page.pageNumber > minPage}">

				<a href="${searchUrl}${searchUrlOrder}&firstResult=${page.startOffset}">
				${page.pageNumber+1}</a>
			</c:when>
		</c:choose>
	</c:forEach>
</c:if>


