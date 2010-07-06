<%@page import="java.io.PrintWriter"%>
<%
	pageContext.setAttribute("TITLE", "An error has occurred");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="errorBox">An error has occurred. The action you were conducting could not be
continued, please try again. If you continue to have problems please report the problem to
<a href="mailto:support-hostsmanager@bath.ac.uk">support-hostsmanager@bath.ac.uk</a>, including all of the text below. </div>

<div style="width:60%; height:300px; border:1px solid black; background-color:#dddddd; overflow:scroll;">
<%
Exception e = (Exception)request.getAttribute("theException");
PrintWriter pw = new PrintWriter(out);
e.printStackTrace(pw);
%>
</div>
<%@ include file="/footer.include.jsp"%>
