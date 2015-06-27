
<%
	pageContext.setAttribute("TITLE", "Add/Update DHCP Server ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="server">
	<spring:hasBindErrors name="server">
	Please fix all errors before continuing.	
	</spring:hasBindErrors>
	<soak:renderBean bean="${server}" view="form" objectBase="server" />
	<input type="submit" name="submit" value="Update server" />
</form:form>

<%@ include file="/footer.include.jsp"%>
