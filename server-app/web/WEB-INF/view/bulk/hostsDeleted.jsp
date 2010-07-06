
<%
	pageContext.setAttribute("TITLE", "Delete multiple Hosts ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="infoBox">Deleted <b>${fn:length(command.hosts) }</b>
host${fn:length(command.hosts)  > 1?"s":"" }.</div>


<%@ include file="/footer.include.jsp"%>