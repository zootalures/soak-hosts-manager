
<%
	pageContext.setAttribute("TITLE", "User details");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<c:if test="${not  isUser }">
	<div class="errorBox">This user does not currently have access to
	the hosts manager. To add access either add them to the access user
	group or set them as an administrator of an organisational unit.</div>
</c:if>
<table>
	<tr>
		<td><label for="name"> User name </label></td>
		<td>${showUserDetails.username}</td>
	</tr>

	<tr>
		<td><label for="name"> Display name </label></td>
		<td>${showUserDetails.friendlyName}</td>
	</tr>
	<tr>
		<td><label for="name"> Email address </label></td>
		<td>${showUserDetails.email}</td>
	</tr>
	<tr>
		<td><label for="name"> Super user </label></td>
		<td>${isAdmin?"Yes":"No"}</td>
	</tr>
	<tr>
		<td><label for="name"> Organisational Units which user can
		administer </label></td>
		<td><c:forEach items="${ouAdmins}" var="ouAuth">
			<a
				href="<c:url value="/orgunit/showOrgUnit.do"><c:param name="id" value="${ouAuth.id }"/></c:url>">
			${ouAuth.name }</a>
			<br>
		</c:forEach></td>
	</tr>
</table>

<a class="actionlink"
	href="<c:url value="/changes/search.do"><c:param name="searchTerm" value="user:${showUserDetails.username }"/></c:url>">Recent
Changes by ${showUserDetails.username }</a>
<a class="actionlink" href="<c:url value="/undo/recentCommands.do"/>">Recent
Commands</a>
<%@ include file="/footer.include.jsp"%>