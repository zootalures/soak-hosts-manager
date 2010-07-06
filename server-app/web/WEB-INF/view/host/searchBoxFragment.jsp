<%@ include file="/base.include.jsp"%>
<form:form commandName="s" name="searchForm" method="GET"
	action="${baseSearchURL}">

	<div id="searchBox" class="searchBox"><label for="searchTerm">Search
	term:</label> <form:input size="40" path="searchTerm" /> <input type="submit"
		value="go" /> <b>Show only my hosts:</b> <form:checkbox
		path="onlyIncludeMyHosts" />  <a id="advancedOptionsButton" href="#"> advanced
	options</a> 

	<div style="padding-top: 0.5em;" id="advancedOptions"><label
		for="hostClass">Host type:</label> <form:select path="hostClass">
		<form:option value="" label="All types of host" />
		<form:options items="${hostClasses}" itemLabel="name" itemValue="id" />
	</form:select> <br />

	<label for="subnet">Subnet:</label> <form:select path="subnet"
		itemValue="id">
		<form:option value="" label="All subnets" />
		<form:options items="${subnets}" itemLabel="displayString"
			itemValue="id" />
	</form:select> <br />

	<label for="nameDomain">Domain:</label> <form:select path="nameDomain">
		<form:option value="" label="All domains" />
		<form:options items="${nameDomains}" itemLabel="suffix"
			itemValue="suffix" />
	</form:select><br />

	<label for="orgUnit">Org. Unit:</label> <form:select path="orgUnit">
		<form:option value="" label="All OUs" />
		<form:options items="${orgUnits}" itemLabel="name" itemValue="id" />
	</form:select> <br />

	<c:forEach var="extra" items="${s.additionalSearchConstraints}">
		<soak:renderBean bean="${extra.value }" view="form" />
	</c:forEach>
	</div>
	</div>
	<script type="text/javascript">

var shown = false;
function show(val){

	if(val){
		YAHOO.hostsearch.advancedOptions.show(YAHOO.hostsearch.advancedOptions, true); 
	}else{
		YAHOO.hostsearch.advancedOptions.hide(YAHOO.hostsearch.advancedOptions, false); 
	}
	
	shown = val;
}
function init() {
	YAHOO.namespace("hostsearch");
	shown = ${s.optionsSet}; 
	YAHOO.hostsearch.advancedOptions = new YAHOO.widget.Module("advancedOptions", { visible: shown });
	YAHOO.hostsearch.advancedOptions.render();
	YAHOO.hostsearch.advancedOptionsButton = new YAHOO.widget.Button("advancedOptionsButton");
	YAHOO.hostsearch.advancedOptionsButton.addListener("click",function (){ show(!shown);});
	
}

YAHOO.util.Event.onContentReady("searchBox",init);
	
	</script>
</form:form>