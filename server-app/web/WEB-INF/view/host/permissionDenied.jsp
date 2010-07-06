
<%
	pageContext.setAttribute("TITLE", "Permission Denied");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="errorBox">You do not have sufficient permissions to
create hosts or to edit the selected host.</div>
<%@ include file="/footer.include.jsp"%>
