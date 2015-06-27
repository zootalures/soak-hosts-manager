<%@ include file="/base.include.jsp"%>


<table class="listTable">
	<thead>
		<tr>
			<td />
			<th>
			<div style="float: left">Name</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=hostName.FQDN&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=hostName.FQDN&ascending=true"><span>down</span></a>
			</div>
			</th>
			<th>
			<div style="float: left">Type</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=hostClass&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=hostClass&ascending=true"><span>down</span></a>
			</div>

			</th>

			<th>
			<div style="float: left">Org. Unit</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=ownership.orgUnit&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=ownership.orgUnit&ascending=true"><span>down</span></a>
			</div>
			</th>

			<th>
			<div style="float: left">IP</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=ipAddress&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=ipAddress&ascending=true"><span>down</span></a></div>
			</th>

			<th>
			<div style="float: left">MAC</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=macAddress&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=macAddress&ascending=true"><span>down</span></a>
			</div>
			</th>
			<c:if test="${showLIU}">
				<th>
				<div style="float: left">LIU</div>
				<div style="float: right; width: 34px;"><a class="upButton"
					href="${searchUrl}&orderBy=liu.changedAt&ascending=false"><span>up</span></a>
				<a class="downButton"
					href="${searchUrl}&orderBy=liu.changedAt&ascending=true"><span>down</span></a>
				</div>
				</th>
			</c:if>

			<th>Description</th>

			<th>
			<div style="float: left">Location</div>
			<div style="float: right; width: 34px;"><a class="upButton"
				href="${searchUrl}&orderBy=location&ascending=false"><span>up</span></a>
			<a class="downButton"
				href="${searchUrl}&orderBy=location&ascending=true"><span>down</span></a></div>
			</th>
		</tr>
	</thead>
	<tbody>

		<c:forEach items="${results.results}" var="host" varStatus="lstatus">
			<!-- hosts[${lstatus.index}] -->
			<tr id="hostrow_${host.id }"
				class="hostRow ${soak:contains(starredHostIds,host.id)?"selected":"" }">
				<td><span class="starSelect"
					onclick="SoakStarred.flipStarred([${host.id}],!SoakStarred.isChecked(${host.id}))">
				</span></td>
				<td><a
					href="<c:url value="/host/show.do">
							<c:param name="id" value="${host.id }" />
						</c:url>">
				<soak:trimSuffix value="${host.hostName}" /></a> <c:forEach
					items="${host.hostAliases}" var="ha">
					<br />
					<span class="listAlias"><soak:trimSuffix value="${ha.alias}" /></span>
				</c:forEach></td>

				<td>${ host.hostClass.name}&nbsp;</td>
				<td><soak:trimText maxLength="16"
					value="${ host.ownership.orgUnit.name}" /></td>

				<td>${ host.ipAddress.hostAddress }&nbsp;</td>
				<td>${ host.macAddress }&nbsp;</td>
				<c:if test="${showLIU}">
					<td>${soak:relativeTime(host.lastUsageInfo.changedAt)}</td>
				</c:if>
				<td><soak:trimText maxLength="20" value="${ host.description }" />&nbsp;</td>
				<td>${ host.location }&nbsp;</td>
				<td><a class="showButton"
					href="<c:url value="/host/show.do">
							<c:param name="id" value="${host.id }" />
						</c:url>">
				<span>show</span> </a> <c:if test="${soak:canEdit( host.ownership)}">
					<a class="editButton"
						href="<c:url value="/flow/update-host-flow.flow">
							<c:param name="id" value="${host.id }" />
						</c:url>">
					<span>edit</span> </a>
				</c:if></td>
			</tr>
		</c:forEach>
	</tbody>
</table>