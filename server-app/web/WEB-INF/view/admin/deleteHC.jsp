
<%
	pageContext.setAttribute("TITLE", "Create or edit host type");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="deleteHostClassCmd">

You are about to delete the host type <b>${deleteHostClassCmd.toDelete.name}</b>.  This operation cannot be undone.<br>

Convert all existing hosts of this type to the following type: 
<form:select path="replaceWith">
<form:option value="" label="Select a host type"/>

<form:options items="${ hostClasses}" itemLabel="name" itemValue="id" />
</form:select>
<br/>
	<input type="submit" name="submit" value="Delete host type" />
</form:form>


<%@ include file="/footer.include.jsp"%>
