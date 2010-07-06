
<%
	pageContext.setAttribute("TITLE", "Delete Multiple Hosts ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


You are about to delete the following hosts:
<br />
<c:set var="hostList" value="${command.hosts  }" scope="request" />
<jsp:include page="bulkHostListFragment.jsp" />
<hr />

<c:if test="${ ! empty preview.aggregateChanges}">
	<c:set scope="request" var="changes"
		value="${preview.aggregateChanges}" />
	<jsp:include page="../host/changesFragment.jsp" />
</c:if>

<br />
<form:form commandName="command">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<label for="comment">Comment for this operation</label>
	<form:textarea rows="5" cols="40" path="changeComments" />
	<br />
	<input type="submit" name="_eventId_submit" value="Delete Hosts" />
</form:form>

<%@ include file="/footer.include.jsp"%>