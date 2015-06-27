
<%
	pageContext.setAttribute("TITLE", "Confirm/edit host data");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<p>Please check or edit any details of the hosts you wish to
create/edit:</p>
<spring:hasBindErrors name="command">
	<div class="errorBox">Fix all errors before continuing.</div>
</spring:hasBindErrors>
<form:form commandName="command">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}" />
	<table class="listTable">
		<thead>
			<tr>
				<th>Host Name</th>
				<th>IP</th>
				<th>MAC</th>
				<th>Type</th>
				<th>OU</th>
				<th>Building</th>
				<th>Room</th>
				<th>Description</th>
			</tr>
		</thead>
		<c:forEach items="${command.hosts}" var="host" varStatus="lstatus">

			<spring:bind path="command.hosts[${lstatus.index}]">
				<c:choose>
					<c:when test="${status.error}">
						<c:set var="hasErrors" value="${true}" />
					</c:when>
					<c:otherwise>
						<c:set var="hasErrors" value="${false}" />
					</c:otherwise>
				</c:choose>
			</spring:bind>

			<tr ${hasErrors?"class='errorHost'":"" }>
				<td><form:input path="hosts[${lstatus.index}].hostName.name" />
				<form:select path="hosts[${lstatus.index}].hostName.domain"
					items="${nameDomains}" /></td>
				<td><form:input size="15"
					path="hosts[${lstatus.index}].ipAddress" /></td>
				<td><form:input size="17"
					path="hosts[${lstatus.index}].macAddress" /></td>
				<td><form:select path="hosts[${lstatus.index}].hostClass"
					items="${hostClasses}" itemLabel="name" itemValue="id" /></td>

				<td><c:choose>
					<c:when test="${fn:length(orgUnits) > 1}">
						<form:select path="hosts[${lstatus.index}].ownership.orgUnit"
							items="${orgUnits }" itemValue="id" itemLabel="id" />
					</c:when>
					<c:otherwise>
						<input type="hidden"
							name="hosts[${lstatus.index}].ownership.orgUnit"
							value="${orgUnits[0].id}" />
					${orgUnits[0].name }
				</c:otherwise>
				</c:choose></td>
				<td><form:input size="10"
					path="hosts[${lstatus.index}].location.building" /></td>
				<td><form:input size="8"
					path="hosts[${lstatus.index}].location.room" /></td>
				<td><form:input path="hosts[${lstatus.index}].description" /></td>

			</tr>

			<c:if test="${hasErrors}">
				<tr class="errorHostErrors">
					<td colspan="8"><form:errors element="span"
						cssClass="tableErrorBox" path="hosts[${lstatus.index }]*" /></td>
				</tr>
			</c:if>

		</c:forEach>
	</table>

	<table>
		<tr>
			<td><label>Choose new IP addresses for hosts from the
			following Subnet:</label></td>
			<c:if test="${subnetFull }">
				<div class="errorBox">Either this subnet is full, or hosts of
				the requested type cannot be added to this subnet.</div>
			</c:if>
			<td><form:select path="newSubnet">
				<form:option value="">Select a subnet </form:option>
				<form:options items="${subnets}" itemLabel="displayString"
					itemValue="id" />
			</form:select> <form:errors path="newSubnet" cssClass="errorBox" /></td>
		</tr>
	</table>
	<br />
	Please note that selecting IP addresses may take some time. <br/>
	<input type="submit" name="_eventId_pickIps"
		value="Select unspecified IPs and Continue Editing" />
	<input type="submit" name="_eventId_submit" value="Preview" />
</form:form>

<%@ include file="/footer.include.jsp"%>