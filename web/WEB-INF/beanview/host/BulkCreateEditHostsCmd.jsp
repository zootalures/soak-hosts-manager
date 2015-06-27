<%@ include file="/base.include.jsp"%>
Creation/editing of the following hosts :
<c:set var="hostList" value="${bean.hosts  }" scope="request" />
<jsp:include page="../../view/bulk/bulkHostListFragment.jsp" />

<c:if test="${bean.hasRenderableOptions}">
	<c:forEach items="${bean.renderableOptions}" var="flagset">
		<soak:renderBean bean="${flagset.value}" />
	</c:forEach>
</c:if>