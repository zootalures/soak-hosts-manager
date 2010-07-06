
<%
	pageContext.setAttribute("TITLE", "Undo Command : Preview");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


You are about to undo the following command :
<div class="commandbox"><soak:renderBean
	bean="${baseCommand.baseChange}" /></div>

The following commands will also be undone:
<div><c:forEach items="${baseCommand.commands}" var="change">
	<soak:renderBean bean="${change}" />
</c:forEach></div>


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
	<input type="submit" name="_eventId_submit" value="Undo command" />
</form:form>

<%@ include file="/footer.include.jsp"%>