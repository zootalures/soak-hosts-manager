
<%
	pageContext.setAttribute("TITLE", "Create or edit network class");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="editNetworkClassCmd">
	<form:hidden path="creation" />
	<table>
		<tbody>
			<tr>
				<td><label>Identifier (Short name) </label></td>

				<td><c:choose>
					<c:when test="${editNetworkClassCmd.creation }">
						<form:input path="networkClass.id" />
						<form:errors path="networkClass.id" />
					</c:when>
					<c:otherwise>
						<form:hidden path="networkClass.id" />
						<b>${editNetworkClassCmd.networkClass.id }</b>
					</c:otherwise>
				</c:choose></td>
			</tr>

			<tr>
				<td><label>Display Name </label></td>
				<td><form:input path="networkClass.name" /> <form:errors
					path="networkClass.name" /></td>
			</tr>
			<tr>
				<td><label>Description</label></td>
				<td><form:textarea rows="5" cols="50"
					path="networkClass.description" /> <form:errors
					path="networkClass.description" /></td>
			</tr>
		</tbody>
	</table>

	<h3>Allowed Host Types</h3>
	Specify which host types can be created on this subnet. Defaults are inherited from the network class.
	<table>
		<thead>
			<tr>
				<th>Host type</th>
				<th>Denied</th>
				<th>Allowed</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach items="${hostClasses}" var="hc">
				<tr>
					<td>${hc.name }</td>
					<td class="denied"><form:radiobutton
						path="hostClassPermissions[${hc.id}]" value="false" /></td>
					<td class="allowed"><form:radiobutton
						path="hostClassPermissions[${hc.id}]" value="true" /></td>
			</c:forEach>
		</tbody>
	</table>
	<input type="submit" name="submit" value="Save network class " />
</form:form>


<%@ include file="/footer.include.jsp"%>
