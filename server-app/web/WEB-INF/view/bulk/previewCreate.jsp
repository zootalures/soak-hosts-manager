
<%
	pageContext.setAttribute("TITLE", "Create multple hosts ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


You are about to create the following hosts:
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
	<input type="submit" name="_eventId_back" value="Go back" />
	<input type="submit" name="_eventId_submit" value="Save changes" />
</form:form>

<%@ include file="/footer.include.jsp"%>