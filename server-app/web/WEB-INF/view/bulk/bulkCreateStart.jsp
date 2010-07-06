
<%
	pageContext.setAttribute("TITLE", "Create multiple hosts");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Please choose how you would like to create the hosts:
<form:form commandName="chooseType">

	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<table>
		<tr>
			<td><label for="newHostClass">Upload CSV file</label></td>
			<td><form:radiobutton path="type" value="CSV_UPLOAD" />
		</tr>

		<tr>
			<td><label for="newHostClass">Define hosts using a range</label></td>
			<td><form:radiobutton path="type" value="RANGE" />
		</tr>


	</table>
	<br />
	<input type="submit" name="_eventId_submit" value="Continue" />
</form:form>

<%@ include file="/footer.include.jsp"%>