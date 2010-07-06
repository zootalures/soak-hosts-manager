<%@ include file="/base.include.jsp"%>
<c:choose>
	<c:when test="${ numStarredHosts > 0}">
		
		
		
		<img  class="star"
			src="<c:url value="/images/starred.png"/>" /> ${numStarredHosts }
		host${numStarredHosts>1?"s":"" } selected 
	

	</c:when>
	<c:otherwise>
	</c:otherwise>
</c:choose>