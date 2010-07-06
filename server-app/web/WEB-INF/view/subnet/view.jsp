<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<h3>Subnet Details</h3>
<soak:renderBean bean="${subnet}" />

<h3>Usage Information</h3>

This subnet contains
<b>${numHosts}</b>
hosts out of of a possible
<b>${bean.numUseableAddresses}</b>
(
<fmt:formatNumber pattern="###"
	value="${ 100*(numHosts / subnet.numUseableAddresses) }" />
% full).

<c:if test="${0!=numHosts}">
	<table>
		<thead>
			<tr>
				<th>Host type</th>
				<th>#</th>
				<th>%</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${usedHostClasses }" var="hc">
				<tr>
					<td>
					${hc.name }</td>
					<td><a
						href="<c:url value="/host/search.do" >
<c:param name="subnet" value="${subnet.id}"/>
<c:param name="hostClass" value="${hc.id}"/>
</c:url>">${hostClassUsage[hc] }</a></td>
					<td><fmt:formatNumber pattern="###"
						value="${ 100.0*( hostClassUsage[hc] / numHosts) }" />%</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<h3>Host types</h3>
The following host types can be created on this subnet: 
<ul>
		<c:forEach var="hc" items="${subnet.allowedHostClasses }">
			<li>${hc.name}</li>
		</c:forEach>
	</ul>

	<h3>Permissions</h3>
Permissions on this subnet:<br />
	<soak:renderBean bean="${subnet.orgUnitAcl }" />
	<br />
Permissions inherited from network class:<br />
	<soak:renderBean bean="${subnet.networkClass.orgUnitAcl }" />
	<br />
</authz:authorize>


<a class="actionlink"
	href="<c:url value="/host/search.do" >
<c:param name="subnet" value="${subnet.id}"/>
</c:url>">
View Hosts on this subnet</a>

<authz:authorize ifAllGranted="ROLE_SUPERVISOR">

	<a class="actionlink"
		href="<c:url value="/admin/subnet/edit.do" >
<c:param name="id">${subnet.id}</c:param>
</c:url>">
	Edit subnet</a>
	<a class="actionlink"
		href="<c:url value="/admin/editOrgUnitAclEntity.do" >
<c:param name="id">${subnet.id}</c:param>
<c:param name="type">subnet</c:param>
</c:url>">
	Edit subnet permissions</a>

	<a class="actionlink"
		href="<c:url value="/admin/subnet/delete.do" >
<c:param name="id">${subnet.id}</c:param>
</c:url>">
	Delete subnet</a>

</authz:authorize>


<c:if test="${soak:canAddToSubnet(subnet) }">
	<a class="actionlink"
		href="<c:url value="/flow/update-host-flow.flow" >
<c:param name="subnetId" value="${subnet.id}"/>
</c:url>">
	Add host to this subnet </a>
</c:if>

<%@ include file="/footer.include.jsp"%>