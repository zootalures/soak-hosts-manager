
<%
	pageContext
			.setAttribute("TITLE", "Edit permissions for Org. Unit ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

You are editing permissions to create hosts which belong to the
following organisational unit:
<soak:renderBean bean="${editOrgUnitAclsCommand.orgUnit}" />


<hr>
<c:set var="formUrl">
	<c:url value="/admin/editOrgUnitAcls.do" />
</c:set>
<form:form commandName="editOrgUnitAclsCommand" action="${formUrl}">
	<form:hidden path="id" />
	<form:hidden path="returnURL" />

	<div id="acltabs" class="yui-navset">
	<ul class="yui-nav">
		<li class="selected"><a href="#hostTypes"><em>Host types</em></a></li>
		<li><a href="#netClasses"><em>Network Classes</em></a></li>
		<li><a href="#subnets"><em>Subnets</em></a></li>
		<li><a href="#nameDomains"><em>Name Domains</em></a></li>
	</ul>

	<div class="yui-content">
	<div id="hostTypes">Admins of this organisational unit can create
	hosts of the following types:
	<table>
		<thead>
			<tr>
				<th>Type</th>
				<th>Denied</th>
				<th>Permitted</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="hc" items="${hostClasses}">
				<tr>
					<td>${hc.name }</td>
					<td class="denied"><form:radiobutton
						path="hostClassAcls[${hc.id}]" value="${null}" /></td>
					<td class="allowed"><form:radiobutton
						path="hostClassAcls[${hc.id}]" value="ALLOWED" /></td>
			</c:forEach>
			</tr>
		</tbody>
	</table>
	</div>

	<div id="netClasses">Admins of this organisational unit can
	create hosts on subnets belonging to the following network classes:

	<table>
		<thead>
			<tr>
				<th>Class</th>
				<th>Denied</th>
				<th>Permitted</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="nc" items="${networkClasses}">
				<tr>
					<td>${nc.name }</td>
					<td class="denied"><form:radiobutton
						path="networkClassAcls[${nc.id}]" value="${null}" /></td>
					<td class="allowed"><form:radiobutton
						path="networkClassAcls[${nc.id}]" value="ALLOWED" /></td>

				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>

	<div id="subnets">Admins of this organisational unit can create
	hosts on the following subnets:

	<table>
		<thead>
			<tr>
				<th>Subnet</th>
				<th>Default</th>
				<th>Denied</th>
				<th>Permitted</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach var="subnet" items="${subnets}">

				<tr>

					<td>${subnet.name } : ${subnet.minIP.hostAddress }</td>
					<c:set var="inheritedPerm"
						value="${soak:orgUnitCanUseEntity(editOrgUnitAclsCommand.orgUnit,subnet.networkClass)}" />

					<td class="${inheritedPerm?"allowed":"denied" }"><form:radiobutton
						path="subnetAcls[${subnet.id}]" value="${null}" /></td>
					<td class="denied"><form:radiobutton
						path="subnetAcls[${subnet.id}]" value="DENIED" /></td>
					<td class="allowed"><form:radiobutton
						path="subnetAcls[${subnet.id}]" value="ALLOWED" /></td>

				</tr>
			</c:forEach>

		</tbody>
	</table>
	</div>

	<div id="nameDomains">Admins of this organisational unit can
	create hosts with the following name suffixes:

	<table>
		<thead>
			<tr>
				<th>Suffix</th>
				<th>Denied</th>
				<th>Permitted</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="nd" items="${nameDomains}">
				<tr>
					<td>${nd.suffix }</td>
					<td class="denied"><form:radiobutton
						path="nameDomainAcls[${nd.suffix}]" value="${null}" /></td>
					<td class="allowed"><form:radiobutton
						path="nameDomainAcls[${nd.suffix}]" value="ALLOWED" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>

	</div>
	</div>
	<input type="submit" value="Save Permissions" />
</form:form>

<script type="text/javascript">
	YAHOO.util.Event.onContentReady('acltabs',
 function(){
    var tabView = new YAHOO.widget.TabView('acltabs');   
    });
</script>

<%@ include file="/footer.include.jsp"%>
