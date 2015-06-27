
<%
	pageContext.setAttribute("TITLE", "Set name domain permissions");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="command" method="post">

	<table>
		<tr>

			<th></th>
			<c:forEach items="${hostClasses}" var="hc">
				<th>${hc.id}</th>
			</c:forEach>
		</tr>
		<c:forEach items="${nameDomains }" var="nd">

			<tr>
				<td>${nd.suffix}</td>
				<c:forEach items="${hostClasses}" var="hc">
					<td><form:checkbox id="${nd.suffix}_${hc.id}"
						path="permissions[${nd.suffix}][${hc.id}]" /></td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>

	<input type="submit" value="Save changes" />
</form:form>


<%@ include file="/footer.include.jsp"%>
