
<%
	pageContext.setAttribute("TITLE", "Delete subnet");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

You are about to delete subnet the following subnet , this operation
cannot be undone.
<soak:renderBean bean="${subnet}" />

Click
<b> confirm deletion</b>
to continue.
<form action="delete.do"><input type="hidden" name=id
	" value="${subnet.id}"> <input type="hidden" name=noReally
	" value="1"> <input type="submit" value="Confirm Deletion">
</form>
<%@ include file="/footer.include.jsp"%>
