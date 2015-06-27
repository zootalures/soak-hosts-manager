<%@ include file="/base.include.jsp"%>
<h4>DNS Status</h4>

The following DNS records are related to this host:
<c:choose>
	<c:when
		test="${bean.worstRecordState eq 'MISSING' or bean.worstRecordState eq 'SPURIOUS' }">
		<div class="errorBox">One or more records related to this host
		appear to be invalid or missing . See below for details.</div>
	</c:when>

	<c:when test="${bean.worstRecordState eq 'MINOR'}">
		<div class="warnBox">The DNS records for this host appear to be
		correct, however one or more records differs from policy . See below
		for details.</div>
	</c:when>

	<c:otherwise>
		<div class="infoBox">The DNS records for this host appear to be
		in order.</div>

	</c:otherwise>
</c:choose>
<table class="listTable">
	<thead>
		<th>Zone</th>
		<th>Name</th>
		<th>Type</th>
		<th>TTL</th>
		<th>Data</th>
	</thead>
	<tbody>
		<c:forEach items="${bean.records }" var="rec">
			<c:set var="state" value="${bean.recordState[rec]}" />

			<tr class="dnsRecordEndorsed dns_${state} ">
				<td>${rec.zone.displayName}</td>
				<td>${rec.hostName }</td>
				<td>${rec.type }</td>
				<td>${rec.ttl }</td>
				<td>${rec.target }</td>
				<td><c:choose>
					<c:when test="${state eq 'MISSING'}">Should be present but is not</c:when>
					<c:when test="${state eq 'MINOR'}">Is present but TTL differs from policy</c:when>
					<c:when test="${state eq 'SPURIOUS'}">Is present but may be incorrect</c:when>
				</c:choose></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<c:if
	test="${!empty hostView.host.configSettings['dns.advancedFlags'] }">
	<c:if
		test="${null!= hostView.host.configSettings['dns.advancedFlags'].hostTTL}">
Host has overridden TTL: <B>
		${hostView.host.configSettings['dns.advancedFlags'].hostTTL}</B>
		<br />
	</c:if>

	<c:if
		test="${hostView.host.configSettings['dns.advancedFlags'].neverUpdateDNS}">
		<b>DNS updates are disabled for this host.</b> To re-enable DNS updates edit the host and change the DNS updated mode  in the "Advanced" tab.	</c:if>
	<br />
</c:if>