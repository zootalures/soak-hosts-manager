<jsp:useBean id="subnetCmd" scope="request"
	type="edu.bath.soak.web.subnet.EditSubnetCommand" />
<%
	String title;
	if (request.getAttribute("isNew") == Boolean.TRUE)
		title = "Create new subnet";
	else
		title = "Editing subnet \"" + subnetCmd.getName() + "\"";

	pageContext.setAttribute("TITLE", title);
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="subnetCmd" method="post">
	<spring:hasBindErrors name="subnetCmd">
Please fix all errors before continuing.
 
</spring:hasBindErrors>
	<form:errors>
	</form:errors>
	<table>
		<tr>
			<td><label for="name"> Name </label></td>
			<td><form:input path="name" /> <form:errors path="name"
				cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="description">Description </label></td>
			<td><form:textarea cols="60" path="description" /> <form:errors
				path="description" cssClass="errorBox" /></td>
		</tr>

		<tr>
			<td><label for="baseaddress">CIDR Address </label></td>
			<td><form:input path="baseAddress" />/<form:select
				path="numBits" items="${subnetValues}" /> <form:errors
				path="baseAddress" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="vlan"> Vlan </label></td>
			<td><form:select path="vlan">
				<form:option value="" label="No VLAN" />
				<form:options items="${vlans}" itemLabel="stringRep" itemValue="id" />

			</form:select> <form:errors path="vlan" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="networkClass"> Network Class </label></td>
			<td><form:select items="${networkClasses}" itemLabel="name"
				itemValue="id" path="networkClass" /> <form:errors
				path="networkClass" cssClass="errorBox" /></td>
		</tr>

		<tr>
			<td><label for="gateway">Default Gateway </label></td>
			<td><form:input path="gateway" /> <form:errors path="gateway"
				cssClass="errorBox" /></td>
		</tr>

		<tr>
			<td><label for="noScan">Omit from network scans </label></td>
			<td><form:checkbox path="noScan" /></td>
		</tr>
		<tr>
			<td><label for="comments"> Comments </label></td>
			<td><form:textarea cols="60" rows="5" path="comments" /></td>
		</tr>

	</table>

	<h3>Allowed Host Types</h3>
	Specify which host types can be created on this subnet. Defaults are inherited from the network class.
	<table>
		<thead>
			<tr>
				<th>Host type</th>
				<th>Default</th>
				<th>Denied</th>
				<th>Allowed</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach items="${hostClasses}" var="hc">
				<tr>
					<td>${hc.name }</td>
					<td><form:radiobutton path="hostClassPermissions[${hc.id}]"
						value="DEFAULT" /></td>
					<td class="denied"><form:radiobutton
						path="hostClassPermissions[${hc.id}]" value="DENIED" /></td>
					<td class="allowed"><form:radiobutton
						path="hostClassPermissions[${hc.id}]" value="ALLOWED" /></td>
			</c:forEach>
		</tbody>
	</table>
	<input type="submit" value="Save subnet" />
</form:form>

<%@ include file="/footer.include.jsp"%>
