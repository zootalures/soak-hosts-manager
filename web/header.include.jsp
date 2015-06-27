<%@ include file="/base.include.jsp"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<c:set var="staticbase" value="/hosts_static" />
<%
	//Ahem. 
	Calendar calendar = new GregorianCalendar(Locale.UK);
	calendar.setTime(new Date());
	if (calendar.get(Calendar.DAY_OF_MONTH) == 1
			&& calendar.get(Calendar.MONTH) == Calendar.APRIL) {
		pageContext.setAttribute("rickRoll", true);
	} else if (calendar.get(Calendar.DAY_OF_MONTH) == 31
			&& calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
		pageContext.setAttribute("halloween", true);
	} else {
		pageContext.setAttribute("rickRoll", false);
	}
%>
<c:choose>
	<c:when test="${rickRoll}">
		<c:set var="homeURL"
			value="http://www.youtube.com/watch?v=oHg5SJYRHA0" />
		<c:set var="appIcon" value="${staticbase}/images/app_icon2.png" />
	</c:when>
	<c:when test="${halloween}">
		<c:set var="appIcon" value="${staticbase}/images/app_icon3.png" />
	</c:when>
	<c:otherwise>
		<c:set var="homeURL">
			<c:url value="/" />
			<c:set var="appIcon" value="${staticbase}/images/app_icon.png" />

		</c:set>
	</c:otherwise>
</c:choose>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Locale"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title><c:if test="${!empty TITLE}">
	<c:out value="${TITLE}" /> - 
</c:if>Hosts Manager</title>

<link rel="shortcut icon" href="${staticbase}/images/app_favicon.png"
	type="image/png" />
<link rel="icon" href="${staticbase}/images/app_favicon.png"
	type="image/png" />
<!-- core css -->
<link rel="stylesheet" type="text/css"
	href="${staticbase}/media/default.css" title="Default" media="screen" />
<link rel="stylesheet" type="text/css"
	href="${staticbase}/media/soak.css" media="screen" />

<!-- END core css -->

<link rel="stylesheet" type="text/css"
	href="${staticbase}/yui/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css"
	href="${staticbase}/yui/tabview/assets/skins/sam/tabview.css" />
<link rel="stylesheet" type="text/css"
	href="${staticbase}/yui/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css"
	href="${staticbase}/yui/calendar/assets/skins/sam/calendar.css" />


<!--  YUI scripts -->

<script type="text/javascript"
	src="${staticbase}/yui/yahoo/yahoo-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/container/container_core-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/element/element-beta-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/button/button-beta-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/utilities/utilities.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/tabview/tabview.js"></script>
<script type="text/javascript" src="${staticbase}/yui/menu/menu-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/connection/connection-min.js"></script>
<!--<script type="text/javascript"-->
<!--	src="${staticbase}/yui/dragdrop/dragdrop-min.js"></script>-->
<!--<script type="text/javascript"-->
<!--	src="${staticbase}/yui/logger/logger-min.js"></script>-->
<script type="text/javascript" src="${staticbase}/yui/dom/dom-min.js"></script>
<script type="text/javascript"
	src="${staticbase}/yui/calendar/calendar-min.js"></script>
<!--  END YUI scripts -->
<script>
var urlBase = "<c:url value="/" />";
</script>

<script type="text/javascript"
	src="${staticbase}/scripts/starredHosts.js"></script>

</head>

<body class="yui-skin-sam">
<div id="header"><a href="<c:url path='/'/>"> <img
	src="${staticbase}/images/site_logo.png" alt="Host manager site"
	border="0" style="float: right;" class="veneer inaccessible" /> </a> <a
	href="<c:url path='/'/>" style="float: right;"
	class="printonly accessible">Host Manager Site</a> <a
	href="${homeURL}"> <img id="appIcon" src="${appIcon}"
	alt="Hosts Manager" border="0" style="float: left;" /> </a>
<h1 id="sitemast"><a href="${homeURL}">Hosts Manager <c:forEach
	items="${titleBeans }" var="bean">
	<soak:renderBean view="title" bean="${bean}" />
</c:forEach></a></h1>
</div>


<%@ include file="/menu.jsp" %>



<div id="main"><c:if test="${!empty TITLE}">
	<h2>${TITLE}</h2>
	<c:forEach items="${startPageBeans }" var="bean">
		<soak:renderBean view="startPage" bean="${bean}" />
	</c:forEach>
</c:if> <c:if test="${!empty ERROR}">
	<div id="errorMessage">
	<p><c:out value="${ERROR}" /></p>
	</div>
</c:if> <c:if test="${!empty flashMessage}">
	<div id="flashMessage">
	<div class="infoBox"><c:out value="${flashMessage}" /></div>
	</div>
</c:if>