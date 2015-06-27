
<%
	pageContext.setAttribute("TITLE", "Edit Name Domain permissions");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<form:form commandName="command" method="post" enctype="multipart/form-data">
	
	<form:errors path="*"  element="div" cssClass="errorBox"/>
	
	XML file to import <input type="file" name="importData"><br/>
	
	<input type="submit" value="Import" />
</form:form>

<%@ include file="/footer.include.jsp"%>
