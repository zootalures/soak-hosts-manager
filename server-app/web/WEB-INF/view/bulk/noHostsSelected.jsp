
<%
	pageContext.setAttribute("TITLE", "No hosts selected");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="errorBox">You have not selected any hosts to perform
this operation on .</div>
<p>This may be because your session has expired.</p>
<%@ include file="/footer.include.jsp"%>