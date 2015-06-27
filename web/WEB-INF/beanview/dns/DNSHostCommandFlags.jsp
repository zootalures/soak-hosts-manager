<%@ include file="/base.include.jsp"%>
<c:if test="${ bean.hasOptionsSet}">
	<h4>DNS options</h4>
	<table>
		<c:if test="${ !( bean.updateMode eq 'DNS_DEFAULT')}">
			<tr>
				<td><label for="updateMode">DNS update:</label></td>
				<td><c:choose>
					<c:when test="${bean.updateMode eq 'DNS_DEFAULT' }">
			Default (update DNS normally)
			</c:when>
					<c:when test="${bean.updateMode eq 'NO_DNS_EDITS' }">
			Do not update DNS for this edit only
			</c:when>
					<c:when test="${bean.updateMode eq 'NEVER_DNS_EDITS' }">
			Never update DNS for this host
			</c:when>
					<c:when test="${bean.updateMode eq 'DNS_REFRESH_ALL_DATA' }">
			Refresh all DNS records for this  host
			</c:when>
				</c:choose>
			</tr>
		</c:if>
		<c:if test="${null!=bean.hostTTL}">
			<tr>
				<td><label for="hostTTL">Use the following custom TTL
				for records belonging to this host: (leave blank for default TTL)</label></td>
				<td>${bean.hostTTL}</td>
			</tr>
		</c:if>

		<c:if test="${bean.forceDNSUpdates}">
			<tr>
				<td><label for="forceDNSUpdates">Remove conflicting DNS
				records on update</label></td>
				<td>${bean.forceDNSUpdates}</td>
			</tr>
		</c:if>
	</table>
</c:if>