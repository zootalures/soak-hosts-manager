<%@page import="edu.bath.soak.net.cmd.AlterHostCmd"%>
<%
	String title;
	AlterHostCmd cmd = (AlterHostCmd) pageContext
			.findAttribute("editHostCmd");
	if (cmd.isCreation())
		title = "Create Host";
	else
		title = "Edit Host";
	pageContext.setAttribute("TITLE", title);
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<script type="text/javascript">
function onChangeHostType(){
	document.getElementById('extraEvent').name="_eventId_updateHostType";
	document.getElementById('extraEvent').value="1";
    document.hostForm.submit();
}
</script>
<form:form name="hostForm" commandName="editHostCmd" method="post">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<spring:hasBindErrors name="editHostCmd">
Please fix all issues before continuing
</spring:hasBindErrors>
	<form:errors id="allErrors" path="*" element="div" cssClass="errorBox">
	</form:errors>
	<div id="hosttabs" class="yui-navset">
	<ul class="yui-nav">
		<li class="selected"><a href="#details"><em>Host Details</em></a></li>
		<c:if test="${showAliases }">
			<li><a href="#aliases"><em>Host Aliases</em></a></li>
		</c:if>
		<c:if test="${!empty renderableOptions}">
			<li><a href="#advanced"><em>Advanced</em></a></li>
		</c:if>
	</ul>
	<div class="yui-content">
	<div id="details">
	<p>
	<table>
		<tr>
			<td><label for="newHost.hostClass">Host Type</label></td>
			<td></td>
			<td><form:select path="newHost.hostClass" items="${hostClasses}"
				itemLabel="name" itemValue="id"
				onchange="javascript:onChangeHostType();" /><soak:helpLink
				cssClass="helpButton" path="Host Types" /><form:errors
				path="newHost.hostClass" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="newHost.hostName.name"> Host Name </label></td>
			<td></td>
			<td><form:input path="newHost.hostName.name" /> <c:if
				test="${fn:length(domains) > 1}">
				<form:select path="newHost.hostName.domain" items="${domains}"
					itemLabel="suffix" itemValue="suffix" />
			</c:if> <c:if test="${fn:length(domains) ==1}">
				${domains[0].suffix}
				<input type="hidden" name="newHost.hostName.domain"
					value="${domains[0].suffix}" />
			</c:if><soak:helpLink cssClass="helpButton" path="Hosts#Host Name" /> <form:errors
				path="newHost.hostName.*" cssClass="errorBox" /><br />
			e.g. <i>${editHostCmd.newHost.hostClass.exampleName }</i></td>
		</tr>
		<tr>
			<td><label for="newHost.description">Description </label></td>
			<td></td>
			<td><form:textarea path="newHost.description" cols="60" /> <form:errors
				path="newHost.description" cssClass="errorBox" /></td>
		</tr>

		<tr>
			<td><label for="newHost.macAddress">MAC address </label></td>
			<td></td>
			<td><form:input path="newHost.macAddress" /> <soak:helpLink
				cssClass="helpButton" path="Hosts#MAC Address" /><form:errors
				path="newHost.macAddress" cssClass="errorBox" /></td>
		</tr>

		<tr>
			<td><label for="specifyIp">Use this IP:</label></td>
			<td><form:radiobutton path="specifyIp" value="true" /></td>
			<td><form:input path="newHost.ipAddress" /><soak:helpLink
				cssClass="helpButton" path="Hosts#IP Address" /><form:errors
				path="newHost.ipAddress" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="specifyIp">Choose new IP on subnet:</label></td>
			<td><form:radiobutton path="specifyIp" value="false" /></td>
			<td><form:select path="subnet" items="${subnets}"
				itemLabel="displayString" itemValue="id" /> <form:errors
				path="subnet" cssClass="errorBox" />
		</tr>
		<tr>
			<td><label for="newHost.location.building">Building </label></td>
			<td></td>
			<td><form:input path="newHost.location.building" /> <form:errors
				path="newHost.location.building" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="newHost.location.room">Room </label></td>
			<td></td>
			<td><form:input path="newHost.location.room" /> <form:errors
				path="newHost.location.room" cssClass="errorBox" /></td>
		</tr>
		<tr>
			<td><label for="newHost.ownership.orgUnit">Org. unit </label></td>
			<td></td>
			<td><c:choose>
				<c:when test="${fn:length(allowedOUs) > 1}">
					<form:select path="newHost.ownership.orgUnit"
						items="${allowedOUs }" itemValue="id" itemLabel="name" />
				</c:when>
				<c:otherwise>
					<input type="hidden" name="newHost.ownership.orgUnit"
						value="${allowedOUs[0].id}" />
					${allowedOUs[0].name }
				</c:otherwise>
			</c:choose> <form:errors path="newHost.ownership.orgUnit" cssClass="errorBox" /></td>
		</tr>

	</table>

	<soak:helpLink path="How to add a host record"
		title="HOWTO: Add a host record" /></div>
	<c:if test="${showAliases }">
		<div id="aliases"><c:if
			test="${empty editHostCmd.newHost.hostAliases }">
No aliases defined 
</c:if>
		<table>
			<thead>
				<tr>
					<th>Alias</th>
					<th>Type</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${editHostCmd.newHost.hostAliases}" var="alias"
					varStatus="status">
					<form:hidden path="newHost.hostAliases[${status.index}].id"/>
					<tr>
						<td><form:input
							path="newHost.hostAliases[${status.index}].alias.name" /> <c:choose>
							<c:when test="${fn:length(domains) > 1}">
								<form:select
									path="newHost.hostAliases[${status.index}].alias.domain"
									items="${domains}" itemLabel="suffix" itemValue="suffix" />
							</c:when>
							<c:otherwise>
								${domains[0].suffix}
								<input type="hidden"
									name="newHost.hostAliases[${status.index}].alias.domain"
									value="${domains[0].suffix}" />
							</c:otherwise>
						</c:choose> <form:errors path="newHost.hostAliases[${status.index}].*"
							cssClass="errorBox" /></td>
						<td><form:select
							path="newHost.hostAliases[${status.index}].type"
							items="${hostAliasTypes}">
						</form:select></td>
					</tr>
				</c:forEach>

				<tr>
					<c:set scope="page" var="addidx"
						value="${fn:length(editHostCmd.newHost.hostAliases)}" />
					<td>+ <input name="newHost.hostAliases[${addidx}].alias.name" />
					<c:choose>
						<c:when test="${fn:length(domains) > 1}">
							<select name="newHost.hostAliases[${addidx}].alias.domain">
								<c:forEach items="${domains}" var="domain">
									<option value="${domain.suffix }">${domain.suffix}</option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
								${domains[0].suffix}
								<input type="hidden"
								name="newHost.hostAliases[${addidx}].alias.domain"
								value="${domains[0].suffix}" />
						</c:otherwise>
					</c:choose></td>
					<td><select name="newHost.hostAliases[${addidx}].type">
						<c:forEach items="${hostAliasTypes}" var="type">
							<option value="${type}">${type}</option>
						</c:forEach>
					</select></td>
				</tr>

			</tbody>
		</table>
		
		To remove aliases make the alias name blank and then click <b>Add/Update</b>  
		
		<input type="submit" name="_eventId_updateAliases" value="Add/Update" />
		<soak:helpLink path="Host Aliases" title="About host aliases" /></div>

	</c:if> <c:if test="${!empty renderableOptions}">
		<div id="advanced"><c:forEach items="${renderableOptions}"
			var="flagset">

			<div id="editHost_advanced_${flagset.key}"><soak:renderBean
				objectBase="optionData['${flagset.key}']" view="form"
				bean="${flagset.value}" /></div>
		</c:forEach></div>
	</c:if></div>
	</div>


	<input type="submit" name="_eventId_preview" value="Preview changes" />

	<form:hidden id="lastTab" path="optionData['gui.lastTab']" />
	<input type="hidden" id="extraEvent" name="extraEvent" />
</form:form>
<script type="text/javascript">
	function init (){
    var lastTab = document.getElementById("lastTab");
    var tabView = new YAHOO.widget.TabView('hosttabs',	{ activeIndex: lastTab.value });
   
    var listener = function(e) {
    	lastTab.value = "" + tabView.getTabIndex(e.newValue); 
    };
    tabView.addListener('activeTabChange', listener);
        tabView.activeIndex = lastTab.value;
    };
    
    	YAHOO.util.Event.onContentReady("hosttabs",init);
    
</script>



<%@ include file="/footer.include.jsp"%>
