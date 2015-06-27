 
<%
	pageContext.setAttribute("TITLE", "Server Re-Synced");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Updated server ${server.displayName }
<table> 
<Tr>
<th> Scope</th><th> Added </th> <th> Deleted </th>
</Tr>
<c:forEach var="upd" items="${updates}">
<tr> 
<td> ${upd.scope.minIP.hostAddress} - ${upd.scope.maxIP.hostAddress}</td>
<td> ${upd.numAdded }</td>
<td> ${upd.numDeleted }</td>
</tr>
</c:forEach>
</table>
<%@ include file="/footer.include.jsp"%>
