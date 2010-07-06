
<%
	pageContext.setAttribute("TITLE", "Delete organisational unit");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="deleteOrgUnitCmd">

You are about to delete the organisational unit  <b>${deleteOrgUnitCmd.toDelete.name}</b>.  This operation cannot be undone.<br>

Move all hosts in this organisational unit to the following OU: 
<form:select path="replaceWith">
<form:option value="" label="Select an org unit"/>
<form:options items="${ orgUnits}" itemLabel="name" itemValue="id" />
</form:select>
<br/>
	<input type="submit" name="submit" value="Delete organisational unit " />
</form:form>


<%@ include file="/footer.include.jsp"%>
