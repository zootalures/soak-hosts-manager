<%@ include file="/base.include.jsp"%>
Deletion of the following hosts :
<c:set var="hostList" value="${bean.hosts  }" scope="request" />
<jsp:include page="../../view/bulk/bulkHostListFragment.jsp" />