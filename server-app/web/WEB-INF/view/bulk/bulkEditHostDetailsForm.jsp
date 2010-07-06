
<%
	pageContext.setAttribute("TITLE", "Create/Edit multiple hosts");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Please edit individual host details and then click
<b>Apply edits and return to bulk editor</b>
.

<spring:hasBindErrors name="command">
	<div style="" class="errorBox">One or more hosts has errors, please fix
	them before continuing.
	<div style="border:1px solid black;overflow-x: hidden; max-height: 150px; overflow: auto;"><form:form
		commandName="command">
		<form:errors path="*" />
	</form:form></div>
	</div>

</spring:hasBindErrors>



<script type="text/javascript">

function changePage(start){
	document.getElementById('extraEvent').name="_eventId_changePage";
	document.getElementById('extraEvent').value="1";
	document.getElementById('startIndex').value=start; 
    document.editForm.submit();
};
</script>
<c:set var="lastIndex"
	value="${(startIndex+numPerPage-1<fn:length(command.hosts))?startIndex+numPerPage-1:fn:length(command.hosts)-1}" />

Showing hosts ${startIndex+1} - ${lastIndex +1} of
${fn:length(command.hosts) }
<br>
Page
<c:forEach var="page" varStatus="vstatus" items="${pages}">
	<c:choose>
		<c:when test="${page eq startIndex}">
		${vstatus.index +1 }
		</c:when>
		<c:otherwise>
			<a href="#" onClick="changePage(${page});">${vstatus.index +1 } </a>
		</c:otherwise>
	</c:choose>
</c:forEach>
<form:form commandName="command" name="editForm">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}"/>
	<input type="hidden" id="startIndex" name="startIndex"
		value="${startIndex}">
	<input type="hidden" id="extraEvent" name="extraEvent">

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
		<c:forEach items="${command.hosts}" var="host" begin="${startIndex}"
			end="${lastIndex}" varStatus="lstatus">

			<tr>
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


			<tr class="errorHostErrors">
				<td colspan="8"><form:errors element="span"
					cssClass="tableErrorBox" path="hosts[${lstatus.index }]*" /></td>
			</tr>

		</c:forEach>
	</table>

	<br />
	<input type="submit" name="_eventId_validate"
		value="Validate all hosts" />

	<input type="submit" name="_eventId_applyIndividual"
		value="Apply edits and return to bulk editor" />
</form:form>

<%@ include file="/footer.include.jsp"%>