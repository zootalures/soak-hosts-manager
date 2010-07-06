
<%
	pageContext.setAttribute("TITLE", "Undo Command: Complete");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="infoBox">The following command has been undone.</div>

<div class="commandbox"><soak:renderBean
	bean="${baseCommand.baseChange}" /></div>

<%@ include file="/footer.include.jsp"%>
