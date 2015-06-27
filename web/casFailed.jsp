<%@page import="java.io.PrintWriter"%>
<%
	pageContext.setAttribute("TITLE", "An error has occurred");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="errorBox">An error has occurred while trying to login. If you continue to have problems please report the problem to
<a href="mailto:support-hostsmanager@localhost">support-hostsmanager@localhost</a>.</div>

<%@ include file="/footer.include.jsp"%>
