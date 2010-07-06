
<%
	pageContext.setAttribute("TITLE", "List Subnets");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink" href='<c:url value="/admin/subnet/edit.do" />'>
	New subnet </a>
	</li>
</authz:authorize>
<form:form commandName="s" method="get">

	Filter subnets: <form:input path="searchTerm" size="30" />
	<input type="submit" value="Refine" />

</form:form>
<c:choose>
	<c:when test="${ empty search.results}">
	No subnets found for this search.
	</c:when>
	<c:otherwise>
		<table class="listTable">
			<thead>
				<tr>
					<th>
					<div style="float: left;">Vlan</div>
					<div style="float: right; width: 34px;"><a class="upButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="vlan.number" />
						<c:param name="ascending" value="false" />
					</c:url>"><span>up</span></a>
					<a class="downButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="vlan.number" />
						<c:param name="ascending" value="true" />
					</c:url>"><span>down</span></a>
					</div>
					</th>
					<th>
					<div style="float: left;">Name</div>
					<div style="float: right; width: 34px;"><a class="upButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="name" />
						<c:param name="ascending" value="false" />
					</c:url>"><span>up</span></a>
					<a class="downButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="name" />
						<c:param name="ascending" value="true" />
					</c:url>"><span>down</span></a>
					</div>
					</th>
					<th>
					<div style="float: left;">Type</div>
					<div style="float: right; width: 34px;"><a class="upButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="networkClass.name" />
						<c:param name="ascending" value="false" />
					</c:url>"><span>up</span></a>
					<a class="downButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="networkClass.name" />
						<c:param name="ascending" value="true" />
					</c:url>"><span>down</span></a>
					</div>
					</th>
					<th>
					<div style="float: left;">Range</div>
					<div style="float: right; width: 34px;"><a class="upButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="minIP" />
						<c:param name="ascending" value="false" />
					</c:url>"><span>up</span></a>
					<a class="downButton"
						href="<c:url value="/subnet/list.do">
						<c:param name="searchTerm" value="${s.searchTerm }" />
						<c:param name="orderBy" value="minIP" />
						<c:param name="ascending" value="true" />
					</c:url>"><span>down</span></a>
					</div>
					</th>
					<th>Default Gateway</th>
					<th>Hosts</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${search.results}" var="r" varStatus="st">
					<tr class="${st.index%2==0?'even':'odd' }">
						<td><c:if test="${ !empty r.vlan}">
							<a
								href="<c:url value="/vlan/show.do"><c:param name="id" value="${r.vlan.id}"/></c:url>"><B>${r.vlan.number}</B></a>
						</c:if></td>
						<td><a
							href="<c:url value="/subnet/view.do" >
				<c:param name="id">${r.id}</c:param>
</c:url>">
						${r.name}</a></td>
						<td>${r.networkClass.name}</td>
						<td>${r.minIP.hostAddress} - ${r.maxIP.hostAddress }<br>
						${r.minIP.hostAddress }/${r.maskBits}</td>

						<td><c:set var="gatewayHost" value="${gateways[r]}" /> <c:if
							test="${null!=gatewayHost}">
							<a
								href="<c:url value="/host/show.do"><c:param name="id" value="${gatewayHost.id}"/> </c:url>">
							${r.gateway.hostAddress} </a>
						</c:if> <c:if test="${null==gatewayHost}">
			${r.gateway.hostAddress}
			</c:if></td>
						<td><a
							href="<c:url value="/host/search.do" >
<c:param name="subnet" value="${r.id}"/>
</c:url>">
						${usage[r]}/${r.numUseableAddresses} (<fmt:formatNumber
							pattern="###" value="${ 100*(usage[r] / r.numUseableAddresses) }" />%)
						</a></td>
						<td>${r.description}</td>
						<td><a class="showButton" title="Show subnet '${r.name}'"
							href="<c:url value="/subnet/view.do" >
<c:param name="id">${r.id}</c:param>
</c:url>">
						<span>show</span></a> <authz:authorize ifAllGranted="ROLE_SUPERVISOR">
							<a title="Edit subnet '${r.name}'" class="editButton"
								href="<c:url value="/admin/subnet/edit.do" >
				<c:param name="id">${r.id}</c:param>
</c:url>">
							<span>edit</span></a>

							<a title="Edit permissions on subnet '${r.name}'"
								class="editPermsButton"
								href="<c:url value="/admin/editOrgUnitAclEntity.do" >
							<c:param name="id">${r.id}</c:param>
							<c:param name="type">subnet</c:param>
							<c:param name="returnURL">/subnet/list.do</c:param>
</c:url>">
							<span>edit permission</span></a>

						</authz:authorize> <c:if test="${soak:canAddToSubnet(r) }">
							<a class="addButton"
								href="<c:url value="/flow/update-host-flow.flow" >
<c:param name="subnetId" value="${r.id}"/>
</c:url>">
							<span>add Host</span> </a>
						</c:if></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>
<%@ include file="/footer.include.jsp"%>
