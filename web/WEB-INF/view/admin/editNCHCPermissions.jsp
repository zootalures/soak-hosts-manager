
<%
	pageContext.setAttribute("TITLE", "Set network class  permissions");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<p>This page allows you to specifiy course-grained permissions for which host types are allowed to be created on which subnets (specified by network class). Subnets can override specific permissions.  </p>
<p>This does not apply to existing hosts, only new hosts.   </p>
<form:form commandName="command" method="post">

	<table>
		<tr>

			<th></th>
			<c:forEach items="${hostClasses}" var="hc">
				<th>${hc.id}</th>
			</c:forEach>
		</tr>
		<c:forEach items="${networkClasses }" var="nc">

			<tr>
				<td>${nc.name}</td>
				<c:forEach items="${hostClasses}" var="hc">
					<td><form:checkbox id="${nc.id}_${hc.id}"
						path="permissions[${nc.id}][${hc.id}]" /></td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>

	<input type="submit" value="Save changes" />
</form:form>


<%@ include file="/footer.include.jsp"%>
