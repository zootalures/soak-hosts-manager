
<%
	pageContext.setAttribute("TITLE", "Plugins");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
The following plugins are installed:
<c:forEach items="${plugins }" var="plugin">
	<h4>${plugin.pluginName }</h4>
	<table class="infoTable">
		<tr>
			<td><label> Description</label></td>
			<td>${plugin.pluginDescription }</td>
		</tr>
		<tr>
			<td><label> Version </label></td>
			<td>${plugin.pluginVersion }</td>
		</tr>
		<c:if test="${not empty plugin.pluginUrl }">
			<tr>
				<td><label> Home page </label></td>
				<td><a href="${plugin.pluginUrl }">${plugin.pluginUrl }</a></td>
			</tr>
		</c:if>
		<c:if test="${not empty plugin.pluginConfigUrl }">
			<tr>
				<td></td>
				<td><a href="<c:url value="${plugin.pluginConfigUrl }"/>">Configuration</a></td>
			</tr>
		</c:if>
	</table>
</c:forEach>

<%@ include file="/footer.include.jsp"%>
