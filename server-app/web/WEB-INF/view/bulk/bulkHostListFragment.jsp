<%@ include file="/base.include.jsp"%>
<table class="listTable">
	<thead>
		<tr>
			<th>
			<div style="float: left">Name</div>
			</th>
			<th>
			<div style="float: left">Type</div>
			</th>
			<th>
			<div style="float: left">IP</div>
			</th>
			<th>
			<div style="float: left">MAC</div>
			</th>
			<th>
			<div style="float: left">OU</div>
			</th>
			<th>Description</th>
			<th>Location</th>

		</tr>
	</thead>
	<tbody>
		<c:forEach items="${hostList}" var="host" end="20">
			<tr id="hostrow_${host.id }">
				<td><c:if test="${not empty host.id }">
					<a
						href="<c:url value="/host/show.do">
							<c:param name="id" value="${host.id }" />
						</c:url>">
				</c:if> <soak:trimSuffix value="${host.hostName}" /> <c:if
					test="${not empty  host.id}">
					</a>
				</c:if> <c:forEach items="${host.hostAliases}" var="ha">
					<br />
					<span class="listAlias"><soak:trimSuffix value="${ha.alias}"
						suffix=".bath.ac.uk." /></span>
				</c:forEach></td>
				<td>${ host.hostClass.name}&nbsp;</td>
				<td>${ host.ipAddress.hostAddress }&nbsp;</td>
				<td>${ host.macAddress }&nbsp;</td>
				<td>${ host.ownership.orgUnit.id }&nbsp;</td>
				<td><soak:trimText maxLength="20" value="${ host.description }" />&nbsp;</td>
				<td>${ host.location }&nbsp;</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<c:if test="${fn:length(hostList)> 20}">
... ${fn:length(hostList) - 20 } more hosts. 
</c:if>