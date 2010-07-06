
<%
	pageContext.setAttribute("TITLE", "Edit DNS records ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<form:form commandName="s">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<input type="hidden" name="_eventId" value="search">

	<div id="searchBox" class="searchBox"><label for="searchTerm">Search
	term:</label> <form:input size="40" path="searchTerm" /> <input type="submit"
		value="go" /> <br />

	<label for="orgUnit">DNS Zone:</label> <form:select path="dnsZone">
		<form:option value="" label="All zones" />
		<form:options items="${dnsZones}" itemLabel="displayName"
			itemValue="id" />
	</form:select> <br />
	<label for="orgUnit">Record Type:</label> <form:select path="recordType">
		<form:option value="" label="All types" />
		<form:option value="A" label="A" />
		<form:option value="AAAA" label="AAAA" />
		<form:option value="CNAME" label="CNAME" />
		<form:option value="DNAME" label="DNAME" />
		<form:option value="DS" label="DS" />
		<form:option value="LOC" label="LOC" />
		<form:option value="MX" label="MX" />
		<form:option value="NS" label="NS" />
		<form:option value="PTR" label="PTR" />
		<form:option value="SRV" label="SRV" />
		<form:option value="TXT" label="TXT" />
	</form:select> <br />
	</div>
</form:form>


<c:if test="${not empty results }">
	<form:form commandName="deleteRecordsCmd">
		<input type="hidden" name="_flowExecutionKey"
			value="${flowExecutionKey}">
		<input type="hidden" name="_eventId" value="deleteSelected">
		<table class="listTable">
			<thead>
				<tr>
					<td></td>
					<th>Zone</th>
					<th>Name</th>
					<th>TTL</th>
					<th>Type</th>
					<th>Target</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${results.results }" var="rec">
					<tr class="${hasEdit[rec]?'editedRec':''}">
						<td style="margin: 0px; padding: 0px;"><c:if
							test="${hasHost[rec] }">
							<a style="margin: 0px; padding: 0px;"
								href="<c:url value="/host/search.do?searchTerm=${rec.target}"/>">
							<img style="margin: 0px; padding: 0px;"
								src="<c:url value="/images/host.png"/>" alt="host exists".>
							</a>
						</c:if></td>
						<td>${rec.zone.displayName }</td>
						<td>${rec.hostName }</td>
						<td>${rec.ttl }</td>
						<td>${rec.type }</td>
						<td>${rec.target }</td>
						<td><input type="checkbox" name="records" value="${rec.id }" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<input type="submit" value="delete selected">
	</form:form>


</c:if>
<%@ include file="/footer.include.jsp"%>
