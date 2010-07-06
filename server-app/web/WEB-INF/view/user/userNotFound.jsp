
<%
	pageContext.setAttribute("TITLE", "User not found");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
	<div class="errorBox">Could not retrieve details for user ${userName}.</div>
<%@ include file="/footer.include.jsp"%>