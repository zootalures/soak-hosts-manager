
<%
	pageContext.setAttribute("TITLE", "Search changes");
	pageContext.setAttribute("cmdMenu", "host-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<!--  Expanded search URL  -->
<c:set scope="request" var="searchUrl">
	<c:url value="/changes/search.do">
		<c:param name="searchTerm" value="${s.searchTerm }" />
		<c:param name="showMine" value="${s.showMine }" />
		<c:param name="orgUnit" value="${s.orgUnit.id }" />
		<c:param name="fromDate" value="${s.fromDate }" />
		<c:param name="toDate" value="${s.toDate }" />
	</c:url>
</c:set>
<!--  Local Search URL  URL  -->
<c:set scope="request" var="baseSearchUrl" value="/changes/search.do" />
<c:set var="gotFromDate">
	<fmt:formatDate type="date" value="${s.fromDate }" pattern="dd/MM/yyyy" />
</c:set>
<c:set var="gotToDate">
	<fmt:formatDate type="date" value="${s.toDate }" pattern="dd/MM/yyyy" />
</c:set>
<form:form commandName="s" name="searchForm" method="GET"
	action="${baseSearchURL}">

	<div id="searchBox" class="searchBox"><label for="searchTerm">Search
	term:</label> <form:input size="40" path="searchTerm" /> <input type="submit"
		value="go" /> <authz:authorize ifAllGranted="ROLE_SUPERVISOR">
		<b>Only show changes for my org units</b>
		<form:checkbox path="showMine" />
	</authz:authorize> <br />

	<label for="orgUnit">Org. Unit:</label> <form:select path="orgUnit">
		<form:option value="" label="All OUs you administer" />
		<form:options items="${orgUnits}" itemLabel="name" itemValue="id" />
	</form:select> <br />

	<label for="fromDate">From </label> <input id="fromDate"
		name="fromDate" value="${gotFromDate }" autocomplete="off" />  <form:errors
		cssClass="errorBox" path="toDate" /> <b> to </b> <input id="toDate"
		name="toDate" autocomplete="off" value="${gotToDate}" /> <form:errors
		cssClass="errorBox" path="toDate" /></div>

	<div style="display: none; position: absolute; z-index: 5"
		id="calendarContainer"></div>
</form:form>

<script type="text/javascript">
var calendar;
var over_cal = false;
var currentCal;
function initCalendars() {
    calendar = new YAHOO.widget.Calendar("calendar","calendarContainer");
    calendar.selectEvent.subscribe(getDate, calendar, true);
    calendar.renderEvent.subscribe(setupListeners, calendar, true);
    YAHOO.util.Event.addListener('fromDate', 'focus', function(){showCal("fromDate");});
    YAHOO.util.Event.addListener('fromDate', 'blur', hideCal);
    YAHOO.util.Event.addListener('toDate', 'focus', function(){showCal("toDate");});
    YAHOO.util.Event.addListener('toDate', 'blur', hideCal);
    calendar.render();
}

function setupListeners() {
    YAHOO.util.Event.addListener('calendarContainer', 'mouseover', overCal);
    YAHOO.util.Event.addListener('calendarContainer', 'mouseout', outCal);
}

function getDate() {
        var calDate = this.getSelectedDates()[0];
        calDate = calDate.getDate() + '/' + (calDate.getMonth() + 1) + '/' + calDate.getFullYear();
        YAHOO.util.Dom.get(currentCal).value = calDate;
        over_cal = false;
        hideCal();
}

function showCal( inputBox ) {
	currentCal = inputBox;
    var xy = YAHOO.util.Dom.getXY(inputBox);
    var date = YAHOO.util.Dom.get(inputBox).value;
    if (date) {
    	var parts = date.split("/");
    	if(parts.length == 3){
    		var usdate = parts[1] + "/" +  parts[0] + "/" + parts[2];
    		
	        calendar.cfg.setProperty('selected', usdate);
   	     	calendar.cfg.setProperty('pagedate', new Date(usdate), true);
    	    calendar.render();
        }
    }
    YAHOO.util.Dom.setStyle('calendarContainer', 'display', 'block');
    xy[1] = xy[1] + 20;
    YAHOO.util.Dom.setXY('calendarContainer', xy);
}

function hideCal() {
    if (!over_cal) {
        YAHOO.util.Dom.setStyle('calendarContainer', 'display', 'none');
    }
}

function overCal() {
    over_cal = true;
}

function outCal() {
    over_cal = false;
}

//YAHOO.util.Event.addListener(, 'load', init);

YAHOO.util.Event.onContentReady("searchBox",initCalendars);
</script>
<jsp:include page="../host/searchPagesFragment.jsp" />
<c:if test="${empty  results.results }">
No results found for search. 
</c:if>
<c:if test="${!empty  results.results }">
	<table class="listTable">
		<thead>
			<tr>
				<th>Date</th>
				<th>Type</th>
				<th>Host name</th>
				<th>IP</th>
				<th>OU</th>
				
				<th>User</th>
				<th>Command</th>
				<th>Comment</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${results.results}" var="hostChange">
				<tr>
					<td><fmt:formatDate timeStyle="short" type="both"
						value="${hostChange.changeDate}" /></td>
					</td>
					<td><c:choose>
						<c:when test="${hostChange.changeType eq 'ADD' }">
							Create
						</c:when>
						<c:when test="${hostChange.changeType eq 'DELETE' }">
						Delete
						</c:when>
						<c:when test="${hostChange.changeType eq 'CHANGE' }">
							<a
								href="<c:url value="/host/showChange.do"><c:param name="id" value="${hostChange.id }"/></c:url>">
							Edit</a>
						</c:when>
					</c:choose></td>
					<td><a
						href="<c:url value="/host/show.do"> <c:param name="id" value="${hostChange.hostId }"/></c:url>"><soak:displayChange
						before="${hostBefore[hostChange].hostName}"
						after="${hostChange.hostName}" /></a></td>
					<td><soak:displayChange
						before="${hostBefore[hostChange].ipAddress.hostAddress}"
						after="${hostChange.ipAddress.hostAddress }" /></td>
					<td><soak:displayChange
						before="${hostBefore[hostChange].ownership.orgUnit.id}"
						after="${hostChange.orgUnit.id }" /></td>
				

					<td>${hostChange.userId }</td>
					<td><c:if test="${not empty hostChange.commandId}">
						<a
							href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${hostChange.commandId}"/></c:url>">
						${hostChange.commandDescription }</a>
					</c:if></td>
					<td>${hostChange.changeComments }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<%@ include file="/footer.include.jsp"%>