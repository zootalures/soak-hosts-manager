<%@ include file="/base.include.jsp"%>
<div id="menubar" class="yuimenubar">
<div class="bd">
<ul class="first-of-type">
	<authz:authorize ifAllGranted="ROLE_SOAK_USER">
		<li class="yuimenubaritem first-of-type"><a
			class="yuimenubaritemlabel" href="#networkmenu"> Hosts </a>
		<div id="networkmenu" class="yuimenu">
		<div class="bd">
		<ul>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/host/search.do" />'> Search </a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/flow/update-host-flow.flow" />'>
			Create host </a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/flow/create-hosts-flow.flow" />'>
			Create multiple hosts </a></li>

			<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
				<li class="yuimenuitem"><a class="yuimenuitemlabel"
					href='<c:url value="/admin/subnet/edit.do" />'> New subnet </a></li>
				<li class="yuimenuitem"><a class="yuimenuitemlabel"
					href='<c:url value="/admin/vlan/edit.do" />'> New vlan </a></li>
			</authz:authorize>




		</ul>
		</div>
		</div>
		</li>
	</authz:authorize>

	<li class="yuimenubaritem first-of-type"><a
		class="yuimenubaritemlabel" href='<c:url value="/subnet/list.do" />'>
	Subnets </a></li>


	<li class="yuimenubaritem first-of-type"><a
		class="yuimenubaritemlabel" href='<c:url value="/vlan/list.do" />'>
	Vlans </a></li>


	<authz:authorize ifAllGranted="ROLE_SUPERVISOR">

		<li class="yuimenubaritem first-of-type"><a
			class="yuimenubaritemlabel" href="#adminmenu"> Administration</a>

		<div id="adminmenu" class="yuimenu">
		<div class="bd">
		<ul>
			<li class="yuimenuitem"><a class="yuimenuitemlabelk"
				href="<c:url value="/admin/index.do"/>">Overview</a></li>

			<li class="yuimenuitem"><a class="yuimenuitemlabelk"
				href="<c:url value="/admin/hostClasses.do"/>">Host Types</a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href="<c:url value="/admin/orgUnits.do"/>">Organisational
			Units/Users</a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href="<c:url value="/admin/nameDomains.do"/>">Name Domains</a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href="<c:url value="/admin/netClasses.do"/>">Network classes</a></li>

			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/dns/list.do" />'> DNS </a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/dhcp/listServers.do" />'> DHCP </a></li>

			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/admin/plugins.do" />'> Plugins </a></li>

		</ul>
		</div>
		</div>
		</li>
	</authz:authorize>

	<authz:authorize ifAllGranted="ROLE_SOAK_USER">
		<li class="yuimenubaritem first-of-type"><a
			id="starredHostsHolder" class="yuimenubaritemlabel"
			href="#starredmenu"> <c:import
			url="/host/starredHostsFragment.do" /> </a>
		<div id="starredmenu" class="yuimenu">
		<div class="bd">
		<ul>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/host/showStarred.do" />'> Show </a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/flow/starred-actions-flow.flow" ><c:param name="action" value="edit"/></c:url>'>
			Edit </a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/flow/starred-actions-flow.flow" ><c:param name="action" value="move"/></c:url>'>
			Move to new subnet</a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel"
				href='<c:url value="/flow/starred-actions-flow.flow" ><c:param name="action" value="delete"/></c:url>'>
			Delete all</a></li>
			<li class="yuimenuitem"><a class="yuimenuitemlabel" href='#'
				onClick="SoakStarred.clearSelection()"> Clear Selection </a></li>

		</ul>
		</div>
		</div>
		</li>
	</authz:authorize>

</ul>
</div>
<c:if test="${!hideLogin}">

	<authz:authorize ifAllGranted="ROLE_SOAK_USER">
		<div id="righttopmenu"><span id="loginarea"> Logged in as
		<a href="<c:url value="/user/details.do"/>"> <strong>${userDetails.friendlyName}</strong></a>
		</span> 
		<form id="logoutform" method="get"
			action="<c:url value="/j_acegi_logout"/>"><input type="submit"
			value="Logout"/></form>
		| <a href="<c:url value="/changes/search.do"/>"> Recent changes</a> |


		<form method="get" action="<c:url value="/host/search.do" />"><input
			type="text" name="searchTerm" /> <input type="submit" value="go" /></form>
		</div>
	</authz:authorize>

	<authz:authorize ifNotGranted="ROLE_SOAK_USER">
		<div id="righttopmenu"><span id="loginarea"> Not logged
		in <a href="<c:url value="/host/search.do" />"> log in</a></span></div>
	</authz:authorize>
</c:if></div>

</div>

<script type="text/javascript">
YAHOO.util.Event.onDOMReady(function () { 

YAHOO.namespace("menu");
var onMenuBar = new YAHOO.widget.MenuBar("menubar"); 
YAHOO.menu.menubar = onMenuBar;
onMenuBar.render();


}
);
</script>