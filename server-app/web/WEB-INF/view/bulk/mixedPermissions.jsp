
<%
	pageContext.setAttribute("TITLE",
			"No permission to modify some  selected hosts");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<div class="errorBox">You you do not have permission to modify all
of the hosts that were selected.<br>
You may still proceed with the hosts which you do have permission to
edit:</div>
<c:set scope="request" var="hostList" value="${permittedHosts}" />
<jsp:include page="bulkHostListFragment.jsp" />

<form><input type="hidden" name="_flowExecutionKey"
	value="${flowExecutionKey}"> <br />
<input type="submit" name="_eventId_proceed"
	value="Proceed with these hosts" /></form>

<%@ include file="/footer.include.jsp"%>