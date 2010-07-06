<%@ include file="/base.include.jsp"%>
<c:choose>
	<c:when test="${bean.creation}">
Creation the following host 
<soak:renderBean bean="${bean.newHost}" />
	</c:when>
	<c:otherwise>
	Modification of the following host:
<soak:renderBean bean="${bean.newHost}" />
	</c:otherwise>
</c:choose>

<c:if test="${bean.hasRenderableOptions}">
	<c:forEach items="${bean.renderableOptions}" var="flagset">
		<soak:renderBean bean="${flagset.value}" />
	</c:forEach>
</c:if>