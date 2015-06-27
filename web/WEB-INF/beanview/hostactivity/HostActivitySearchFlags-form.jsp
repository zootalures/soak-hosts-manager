<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1"
	prefix="string"%><%@ include file="/base.include.jsp"%>
<c:set var="gotFromDate">
	<fmt:formatDate type="date"
		value="${s.additionalSearchConstraints['HostActivitySeachFlags'].fromDate }"
		pattern="dd/MM/yyyy" />
</c:set>
<c:set var="gotToDate">
	<fmt:formatDate type="date"
		value="${s.additionalSearchConstraints['HostActivitySeachFlags'].toDate }"
		pattern="dd/MM/yyyy" />
</c:set>


<div><label for="fromDate">Last active from: </label> <input
	type="text" id="fromDate" autocomplete="off"
	name="additionalSearchConstraints[HostActivitySeachFlags].fromDate"
	value="${gotFromDate}" /> <form:errors cssClass="errorBox"
	path="additionalSearchConstraints[HostActivitySeachFlags].fromDate" />
<b> to </b> <input type="text" id="toDate" autocomplete="off"
	name="additionalSearchConstraints[HostActivitySeachFlags].toDate"
	value="${gotToDate}" /> <form:errors cssClass="errorBox"
	path="additionalSearchConstraints[HostActivitySeachFlags].toDate" />

<div style="display: none; position: absolute; z-index: 5"
	id="calendarContainer"></div>

</div>

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


<c:if
	test="${not empty s.additionalSearchConstraints['HostActivitySeachFlags'].fromDate}">
	<c:set scope="request" var="searchUrl"
		value="${searchUrl}&additionalSearchConstraints[HostActivitySeachFlags].fromDate=${gotFromDate}" />
	<c:set scope="request" var="starSearchURL"
		value="${starSearchURL}&additionalSearchConstraints[HostActivitySeachFlags].fromDate=${gotFromDate}" />
</c:if>

<c:if
	test="${not empty s.additionalSearchConstraints['HostActivitySeachFlags'].toDate}">
	<c:set scope="request" var="searchUrl"
		value="${searchUrl}&additionalSearchConstraints[HostActivitySeachFlags].toDate=${gotToDate}" />
	<c:set scope="request" var="starSearchURL"
		value="${starSearchURL}&additionalSearchConstraints[HostActivitySeachFlags].toDate=${gotToDate}" />
</c:if>