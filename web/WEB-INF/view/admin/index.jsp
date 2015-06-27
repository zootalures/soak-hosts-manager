
<%
	pageContext.setAttribute("TITLE", "Admin Console");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<c:forEach items="${adminObjects }" var="adminObject">
	<soak:renderBean bean="${adminObject}" view="adminConsole" />
</c:forEach>

<%@ include file="/footer.include.jsp"%>
