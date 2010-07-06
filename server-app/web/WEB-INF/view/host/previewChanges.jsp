
<%
	pageContext.setAttribute("TITLE", "Preview host changes");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<soak:renderBean bean="${changeResult.baseChange}" />
<c:if test="${ ! empty changeResult.aggregateChanges}">
	<c:set scope="request" var="changes" value="${changeResult.aggregateChanges}" />
	<jsp:include page="changesFragment.jsp" />
</c:if>

<form:form commandName="editHostCmd" method="post">
	<label for="changeComment">Comment for this change:</label>
	<form:textarea rows="5" cols="60" path="changeComments" />
	<br />

	<input type="submit" name="_eventId_back" value="Go back" />
	<input type="submit" name="_eventId_submit" value="Apply Changes" />
</form:form>
<%@ include file="/footer.include.jsp"%>
