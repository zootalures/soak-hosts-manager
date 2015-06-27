
<%
	pageContext.setAttribute("TITLE", "Upload CSV file");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<p>Either specify a file containing CSV data, or copy and paste the
data into the box below</p>
<soak:helpLink path="How to create multiple hosts"
	title="How to create multiple hosts" />
<br/>
<form:form commandName="csvCmd" enctype="multipart/form-data">
	<form:errors path="*" cssClass="errorBox">
	</form:errors>
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">

	<h3>Default values</h3>
	The following values will be applied to host properties when they are <b>
	not </b> set in the CSV data (i.e. when they are left blank).
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

	<h3>Upload hosts</h3>
	<p>enter one host per line with comma seperated fields containing: 
	<ul>
		<li> host name (mandatory)</li>
		<li> MAC address</li>
		<li> IP address</li>
		<li> Host type (short code)</li>
		<li> Org Unit (short code)</li>
		<li> Building</li>
		<li> Room</li>
		<li> Description</li>
	</ul>
	e.g. </p>
	<pre>
host1.campus,001122334455,138.38.56.19,PC,lm,2S,0.22,test PC
	</pre>
	<table>
		<tr>
			<td><label for="hostCSVData">Upload Host data from file</label></td>
			<td><form:radiobutton path="uploadType" value="FILE" /></td>
			<td><input type="file" name="hostCSVData" />
		</tr>
		<tr>
			<td><label for="hostCSVData">Copy and paste CSV data </label></td>
			<td><form:radiobutton path="uploadType" value="STRING" /></td>
			<td><form:textarea rows="10" cols="60" path="hostCSVDataString" /></td>
		</tr>

	</table>
	<br />
	<input type="submit" name="_eventId_submit" value="Continue" />
</form:form>

<%@ include file="/footer.include.jsp"%>