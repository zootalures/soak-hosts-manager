
<%
	pageContext.setAttribute("TITLE", "Preview host deletion changes");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
The following host will be deleted:
<soak:renderBean bean="${changeResult.baseChange.host}" />

<c:if test="${ ! empty changeResult.aggregateChanges}">
	<c:set scope="request" var="changes"
		value="${changeResult.aggregateChanges}" />
	<jsp:include page="changesFragment.jsp" />
</c:if>

<form:form commandName="deleteHostCmd">

	<label for="changeComments">Comment for this change:</label>
	<form:textarea rows="5" cols="60" path="changeComments" />
	<br />

	<input type="hidden" name="_eventId" value="submit" />
	<input type="submit" name="Delete Host" value="Delete Host" />
</form:form>
<%@ include file="/footer.include.jsp"%>
