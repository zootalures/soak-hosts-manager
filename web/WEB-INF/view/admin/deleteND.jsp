
<%
	pageContext.setAttribute("TITLE", "Create or edit host type");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="deleteNameDomainCmd">

You are about to delete the name domain <b>${deleteNameDomainCmd.toDelete.suffix}</b>.  This operation cannot be undone.<br>

	<br />
	<input type="submit" name="submit" value="Delete name domain" />
</form:form>


<%@ include file="/footer.include.jsp"%>
