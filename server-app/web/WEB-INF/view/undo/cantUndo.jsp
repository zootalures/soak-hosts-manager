
<%
	pageContext.setAttribute("TITLE", "Cannot undo command");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="errorBox">The selected command cannot be undone: <b>${stateException.cause.message}</b>
</div>

This was because of an attempt to undo the following command failed:
<soak:renderBean bean="${stateException.cause.command}" />
<%@ include file="/footer.include.jsp"%>