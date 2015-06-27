
<%
	pageContext.setAttribute("hideLogin", true);
	pageContext.setAttribute("TITLE", "Permission Denied");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="errorBox">You do not have permission view the selected
page. <a href="<c:url value="/j_acegi_logout"/>"> Log out of the hosts manager</a></div>
<%@ include file="/footer.include.jsp"%>
