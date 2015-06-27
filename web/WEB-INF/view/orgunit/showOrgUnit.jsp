
<%
	pageContext.setAttribute("TITLE", "Show Org Unit");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<soak:renderBean bean="${orgUnit}" />

<c:if
	test="${empty allowedNameDomains or empty allowedHostClasses or empty allowedSubnets}">
	<div class="errorBox">Admins in this OU do not have permission to
	create hosts. To allow them to create hosts they must have permission
	to use at least one host type, subnet and name domain.</div>
</c:if>
<h2>Hosts</h2>

This organisational unit owns
<a
	href="<c:url value="/host/search.do">
	<c:param name="orgUnit" value="${orgUnit.id }" />
</c:url>"><b>${
hostsCount}</b> hosts </a>
.

<h3>Hosts by type</h3>
<table class="listTable">
	<thead>
		<tr>
			<th>Type</th>
			<th>count</th>
			<th>%</th>
		</tr>
	</thead>
	<c:forEach items="${hostClassesByNum }" var="hc">
		<tr>
			<td>${hc.name}</td>
			<td><a
				href="<c:url value="/host/search.do">
	<c:param name="orgUnit" value="${orgUnit.id }" />
	<c:param name="hostClass" value="${hc.id }" />
</c:url>">${hostsByType[hc]}</a></td>
			<td><fmt:formatNumber pattern="###"
				value="${ 100.0*( hostsByType[hc] / hostsCount) }" />%</td>

		</tr>

	</c:forEach>
</table>


<h3>Hosts by subnet</h3>
<table class="listTable">
	<thead>
		<tr>
			<th colspan="3">Subnet</th>
			<th>count</th>
			<th>%</th>
		</tr>
	</thead>
	<c:forEach items="${subnetsByNum }" var="s">
		<tr>
			<td><a
				href="<c:url value="/subnet/view.do"><c:param name="id" value="${s.id }"/></c:url>">${s.name}</a></td>
			<td>${s.minIP.hostAddress}</td>
			<td>${s.maxIP.hostAddress}</td>
			<td><a
				href="<c:url value="/host/search.do">
	<c:param name="orgUnit" value="${orgUnit.id }" />
	<c:param name="subnet" value="${s.id }" />
</c:url>">${hostsBySubnet[s]}</a></td>
			<td><fmt:formatNumber pattern="###"
				value="${ 100.0*( hostsBySubnet[s] / hostsCount) }" />%</td>

		</tr>

	</c:forEach>
</table>

<h2>Administrators</h2>
<c:choose>
	<c:when test="${empty orgUnitMappings }">
	No administrators or groups are configured to administer this org unit.
	</c:when>
	<c:otherwise>
The following people can administer this org unit:
<ul>
			<c:forEach items="${ orgUnitMappings}" var="item">
				<li><c:choose>
					<c:when test="${item.type eq 'GROUP'}">
					Members of group <b>${item.principal}</b>
					</c:when>
					<c:otherwise>
						<b>${item.principal}</b>
					</c:otherwise>

				</c:choose></li>

			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
<h2>Permissions</h2>
<h4>Host Types</h4>
<c:choose>
	<c:when test="${empty allowedHostClasses }">
	Admins of this OU are <b>not permitted</b> to create any hosts.
	
	</c:when>
	<c:otherwise>
Admins of this OU are permitted to create the following types of host:

<ul>
			<c:forEach items="${allowedHostClasses}" var="hc">
				<li>${hc.name}</li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
<h4>Subnets</h4>
<c:choose>
	<c:when test="${empty allowedSubnets }">
	Admins of this OU are <b>not permitted</b> to create hosts on any subnet.
	
	</c:when>
	<c:otherwise>
Admins of this OU are permitted to create hosts on the following
subnets:
<ul>
			<c:forEach items="${allowedSubnets}" var="s">
				<li><a
					href="<c:url value="/subnet/view.do" >
				<c:param name="id">${s.id}</c:param>
</c:url>">
				${s.name}: ${s.minIP.hostAddress} - ${s.maxIP.hostAddress}</a></li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>

<h4>Name domains</h4>
<c:choose>
	<c:when test="${empty allowedNameDomains }">
	Admins of this OU are <b>not permitted</b> to create hosts in any name domain.
	
	</c:when>
	<c:otherwise>
	
Admins on this OU are permitted to create hosts in the following
domains:
<ul>
			<c:forEach items="${allowedNameDomains}" var="nd">
				<li>${nd.suffix}</li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>



<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<c:set var="returnURL">/orgunit/showOrgUnit.do?id=${orgUnit.id}</c:set>
	<a class="actionlink"
		href="<c:url value="/admin/editOU.do"><c:param name="id">${orgUnit.id}</c:param></c:url>">
	Edit org unit</a>
	<a class="actionlink"
		href="<c:url value="/admin/editOrgUnitAcls.do"><c:param name="id">${orgUnit.id}</c:param><c:param name="returnURL">${returnURL }</c:param></c:url>">
	Edit permissions</a>
	<a class="actionlink" href="<c:url value="/admin/editOU.do"/>">New
	org unit</a>
</authz:authorize>
</div>

<%@ include file="/footer.include.jsp"%>