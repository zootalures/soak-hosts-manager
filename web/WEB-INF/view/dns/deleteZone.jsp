
<%
	pageContext.setAttribute("TITLE", "Delete DNS Zone ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="zone">
	You are about to delete the DNS Zone: <b>${zone.displayName}</b>. 
	<b>Please note:</b>
	<ul>
	<li>This operation may take some time. </li>
	<li>This will not effect any live DNS data. </li>
	<li> This operation cannot be undone.</li>
	</ul>
	<br>
	<input type="submit" name="submit" value="Delete zone" />
</form:form>

<%@ include file="/footer.include.jsp"%>
