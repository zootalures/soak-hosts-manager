
<%
	pageContext.setAttribute("TITLE", "Delete subnet");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<div class="errorBox">The following subnet cannot be deleted, it
still contains hosts.</div>
<soak:renderBean bean="${subnet}" />
<a class="actionlink"
	href="<c:url value="/host/search.do" >
<c:param name="subnet" value="${subnet.id}"/>
</c:url>">
View Hosts on this subnet</a>
<%@ include file="/footer.include.jsp"%>
