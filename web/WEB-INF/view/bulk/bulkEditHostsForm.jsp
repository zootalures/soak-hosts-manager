
<%
	pageContext.setAttribute("TITLE", "Create/Edit multiple hosts");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Either change all hosts in bulk using the forms below or click
<b>Edit host details individually</b>
to make individual changes.

<c:if test="${subnetFull }">
	<div class="errorBox">Either this subnet is full, or hosts of the
	requested type cannot be added to this subnet.</div>
</c:if>
<spring:hasBindErrors name="command">
	<div style="" class="errorBox">One or more hosts has errors,
	please fix them before continuing. Click <b>validate</b> to re-check
	hosts.
	<div
		style="border: 1px solid black; overflow-x: hidden; max-height: 150px; overflow: auto;"><form:form
		commandName="command">


		<form:errors path="*" />
	</form:form></div>
	</div>

</spring:hasBindErrors>



<div id="hosttabs" class="yui-navset">
<ul class="yui-nav">
	<li class="selected"><a href="#details"><em>Host Details</em></a></li>
	<li><a href="#ipaddresses"><em>IP addresses</em></a></li>
	<c:if test="${not empty dataSources }">

		<li><a href="#import"><em>Import</em></a></li>
	</c:if>
</ul>

<div class="yui-content">
<div id="details"><form:form commandName="setDetailsCmd">

	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<table>
		<tr>
			<td><label for="newHostClass">Change host type</label></td>
			<td><form:select path="newHostClass">
				<form:option value="">Leave unchanged</form:option>
				<form:options items="${hostClasses}" itemLabel="name" itemValue="id" />
			</form:select><form:errors path="newHostClass" /></td>
		</tr>
		<tr>
			<td><label for="newNameDomain">Change host domain</label></td>
			<td><form:select path="newNameDomain">
				<form:option value="">Leave unchanged</form:option>
				<form:options items="${nameDomains}" itemLabel="suffix"
					itemValue="suffix" />
			</form:select><form:errors path="newNameDomain" /></td>
		</tr>
		<c:if test="${fn:length(orgUnits)>1}">
			<tr>
				<td><label for="newOrgUnit">Change org. unit</label></td>
				<td><form:select path="newOrgUnit">
					<form:option value="">Leave unchanged</form:option>
					<form:options items="${orgUnits}" itemLabel="name" itemValue="id" />
				</form:select> <form:errors path="newOrgUnit" /></td>
			</tr>
		</c:if>
		<tr>
			<td><label for="newHostBuilding">Change building</label></td>
			<td><form:checkbox path="doChangeBuilding" /><form:input
				path="newHostBuilding" /></td>
		</tr>
		<tr>
			<td><label for="newHostRoom">Change room</label></td>
			<td><form:checkbox path="doChangeRoom" /><form:input
				path="newHostRoom" /></td>
		</tr>


	</table>
	<br />
	<input type="submit" name="_eventId_applyBulkChanges"
		value="Apply changes" />
</form:form></div>
<div id="ipaddresses"><form:form commandName="setIPsCmd">
	<table>
		<tr>
			<td><label>Clear existing IPs </label></td>
			<td><form:checkbox path="clearIPs" /></td>
		</tr>

		<tr>
			<td><label>Choose new IPs from:</label></td>
			<td><form:select path="newSubnet">
				<form:option value="">Select a subnet </form:option>
				<form:options items="${subnets}" itemLabel="displayString"
					itemValue="id" />
			</form:select> <form:errors path="newSubnet" cssClass="errorBox" /></td>
		</tr>
	</table>
	<br/>
	Please note that selecting IP addresses may take some time. <br/>
	<input type="submit" name="_eventId_assignIps"
		value="Select IP addresses" />

</form:form></div>
<c:if test="${not empty dataSources }">
	<div id="import">
	<p><form:form commandName="applyFilterCmd">
		<table>
			<tr>
				<td><label>Import existing data for hosts using a
				filter: </label></td>
				<td><form:select path="filter" items="${dataSources}"
					itemLabel="sourceDescription" itemValue="id" /></td>
			</tr>
			<tr>
				<td><label>Overwrite existing values: </label></td>
				<td><form:checkbox path="overwrite" /></td>
			</tr>
		</table>
		<input type="submit" name="_eventId_applyFilter"
			value="Import Data for hosts" />

	</form:form></p>
	</div>
</c:if></div>


<script type="text/javascript">
	function init (){
	    var tabView = new YAHOO.widget.TabView('hosttabs');
    };
    
    YAHOO.util.Event.onContentReady("hosttabs",init);
function changePage(start){
	document.getElementById('extraEvent').name="_eventId_changePage";
	document.getElementById('extraEvent').value="1";
	document.getElementById('startIndex').value=start; 
    document.editForm.submit();
};
</script> <c:set var="lastIndex"
	value="${(startIndex+numPerPage-1<fn:length(command.hosts))?startIndex+numPerPage-1:fn:length(command.hosts)-1}" />

Showing hosts ${startIndex+1} - ${lastIndex +1} of
${fn:length(command.hosts) } <br>

<form:form commandName="command" name="editForm">

	<a name="page"></a> Page <c:forEach var="page" varStatus="vstatus"
		items="${pages}">
		<c:choose>
			<c:when test="${page eq startIndex}">
		${vstatus.index +1 }
		</c:when>
			<c:otherwise>
				<a href="#page" onClick="changePage(${page});">${vstatus.index
				+1 } </a>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<br />
	<input type="submit" name="_eventId_editIndividual"
		value="Edit individual hosts" />
	<input type="submit" name="_eventId_validate"
		value="Validate and continue editing" />
	<input type="submit" name="_eventId_submit"
		value="Validate and preview changes" />
	<br />
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
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
				<td><soak:trimSuffix value="${host.hostName }" /></td>
				<td>${host.ipAddress.hostAddress }</td>
				<td>${host.macAddress }</td>
				<td>${host.hostClass.id }</td>
				<td>${host.ownership.orgUnit.id }</td>
				<td>${host.location.building }</td>
				<td>${host.location.room }</td>
				<td><soak:trimText value="${host.description }" maxLength="30" /></td>

			</tr>


			<tr class="errorHostErrors">
				<td colspan="8"><form:errors element="span"
					cssClass="tableErrorBox" path="hosts[${lstatus.index }]*" /></td>
			</tr>

		</c:forEach>
	</table>

	<c:if test="${!empty command.renderableOptions}">
		<a id="advancedOptionsButton" href="#advancedOptions"> Advanced
		options</a>
		<br />
		<div id="advancedOptions"><a name="advancedOptions" /> <c:forEach
			items="${command.renderableOptions}" var="flagset">

			<div id="editHost_advanced_${flagset.key}"><soak:renderBean
				objectBase="optionData['${flagset.key}']" view="form"
				bean="${flagset.value}" /></div>
		</c:forEach></div>

		<script type="text/javascript">
		var shown = false;
function show(val){

	if(val){
		YAHOO.bulkedit.advancedOptions.show(YAHOO.bulkedit.advancedOptions, true); 
	}else{
		YAHOO.bulkedit.advancedOptions.hide(YAHOO.bulkedit.advancedOptions, false); 
	}
	
	shown = val;
}
function init() {
	YAHOO.namespace("bulkedit");
	shown = false; 
	YAHOO.bulkedit.advancedOptions = new YAHOO.widget.Module("advancedOptions", { visible: shown });
	YAHOO.bulkedit.advancedOptions.render();
	YAHOO.bulkedit.advancedOptionsButton = new YAHOO.widget.Button("advancedOptionsButton");
	YAHOO.bulkedit.advancedOptionsButton.addListener("click",function (){ show(!shown); return true;});
	
}

YAHOO.util.Event.onContentReady("advancedOptions",init);
</script>

	</c:if>

	<br />
	<input type="submit" name="_eventId_editIndividual"
		value="Edit individual hosts" />

	<input type="submit" name="_eventId_validate"
		value="Validate and continue editing" />

	<input type="submit" name="_eventId_submit"
		value="Validate and preview changes" />
	<c:if test="${not isCreation}">
		<input type="submit" name="_eventId_reset" value="Reset" />
	</c:if>

</form:form> <%@ include file="/footer.include.jsp"%>