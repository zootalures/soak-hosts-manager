
<%
	pageContext.setAttribute("TITLE", "Define hosts using range");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<soak:helpLink path="How to create multiple hosts using ranges"
	title="How to create multiple hosts" />

<form:form commandName="rangeCmd">

	<spring:hasBindErrors name="rangeCmd">
		<div class="errorBox">Please fix all errors before continuing</div>
	</spring:hasBindErrors>

	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<h3>Default values</h3>
	The following values will be applied to all hosts.
	<table>

		<tr>
			<td><label for="defaultHostData.hostClass">Host Type:</label></td>
			<td><c:choose>
				<c:when test="${fn:length(hostClasses)>1}">
					<form:select path="defaultHostData.hostClass">
						<form:options items="${hostClasses}" itemLabel="name"
							itemValue="id" />
					</form:select>
				</c:when>
				<c:otherwise>
				${hostClasses[0].name }
					<input type="hidden" name="defaultHostData.hostClass"
						value="${hostClasses[0].id }" />
				</c:otherwise>
			</c:choose></td>
		</tr>

		<tr>
			<td><label for="defaultHostData.ownership.orgUnit">Organisational
			Unit:</label></td>
			<td><c:choose>
				<c:when test="${fn:length(orgUnits)>1}">
					<form:select path="defaultHostData.ownership.orgUnit">
						<form:options items="${orgUnits}" itemLabel="name" itemValue="id" />
					</form:select>
				</c:when>
				<c:otherwise>
				${orgUnits[0].name }
					<input type="hidden" name="defaultHostData.ownership.orgUnit"
						value="${orgUnits[0].id }" />
				</c:otherwise>
			</c:choose></td>
		</tr>
		<tr>
			<td><label for="defaultHostData.location.building">Building</label></td>
			<td><form:input path="defaultHostData.location.building" /></td>
		</tr>
		<tr>
			<td><label for="defaultHostData.location.building">Room</label></td>
			<td><form:input path="defaultHostData.location.room" /></td>
		</tr>
	</table>

	<h3>Specify host name range</h3>
	Enter one or two ranges to use when generating host names and IPs. If you specify a second range, each value will be applied for each value of the first range.
	
	<table>
		<tr>
			<td><label for="range1">Range 1</label></td>
			<td><form:input size="3" path="range1.min" /> - <form:input
				size="3" path="range1.max" /><form:errors path="range1*"
				cssClass="errorBox" element="span" /> <br />
			e.g. 1-10</td>
		</tr>

		<tr>
			<td><label for="range1">Pad range 1 values with zeros to
			length:</label></td>
			<td><form:select path="range1.numDigits">
				<form:option value="0">Don't pad</form:option>
				<form:option value="2">2</form:option>
				<form:option value="3">3</form:option>
				<form:option value="4">4</form:option>
				<form:option value="5">5</form:option>
			</form:select><br>
			e.g. padding to 3 digits will create numbers 001,002...</td>
		</tr>
		<tr>
			<td><label for="range2">Range 2 (optional)</label></td>
			<td><form:input size="3" path="range2.min" /> - <form:input
				size="3" path="range2.max" /><form:errors path="range2*"
				cssClass="errorBox" element="span" /></td>
		</tr>
		<tr>
			<td><label for="range2">Pad range 2 values with zeros to
			length:</label></td>
			<td><form:select path="range2.numDigits">
				<form:option value="0">Don't pad</form:option>
				<form:option value="2">2</form:option>
				<form:option value="3">3</form:option>
				<form:option value="4">4</form:option>
				<form:option value="5">5</form:option>
			</form:select></td>
		</tr>

	</table>

	<table>
		<tr>
			<td><label for="hostnameTemplate">Host name template </label></td>
			<td><form:input path="hostnameTemplate" /> <form:errors
				cssClass="errorBox" path="hostnameTemplate" element="span" /><br>
			e.g. <i>hostname-$1</i> or <i>hostname-$1-$2.campus</i> where <b>$1</b>
			and <b>$2</b> are derived from the ranges above.</td>
		</tr>
		<tr>
			<td><label for="ipAddressTemplate">IP address template
			(optional) </label></td>
			<td><form:input path="ipAddressTemplate" /> <form:errors
				cssClass="errorBox" path="ipAddressTemplate" element="span" /><br>
			e.g. <i>172.10.19.$1</i> or <i>138.38.1$1.2$2</i></td>
		</tr>
	</table>
	<br />
	<input type="submit" name="_eventId_submit" value="Continue" />
</form:form>

<%@ include file="/footer.include.jsp"%>