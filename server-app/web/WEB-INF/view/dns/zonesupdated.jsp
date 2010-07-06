
<%
	pageContext.setAttribute("TITLE", "DNS Udpated");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<h3> Zones Updated</h3>
<ul>
<c:forEach var="zupd" items="${updates}">
<li> Updated ${zupd.zone.displayName } : ++${zupd.numAdded}  : -- ${zupd.numDeleted}   </li>
</c:forEach>
</ul>
<%@ include file="/footer.include.jsp"%>
