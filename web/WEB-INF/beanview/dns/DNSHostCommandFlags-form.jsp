<%@ include file="/base.include.jsp"%>
<h4>DNS Options</h4>
<table>
	<tr>

		<td><label for="updateMode">DNS Settings:</label></td>
		<td><form:select path="${objectBase}.updateMode">
			<form:option value="DNS_DEFAULT">
			Default (update DNS normally)
			</form:option>
			<form:option value="NO_DNS_EDITS">
			Do not update DNS for this edit only
			</form:option>
			<form:option value="NEVER_DNS_EDITS">
			Never update DNS for this host
			</form:option>
			<form:option value="DNS_REFRESH_ALL_DATA">
			Refresh all DNS records for this  host
			</form:option>
		</form:select></td>
	</tr>
	<tr>
		<td><label for="hostTTL">Use the following TTL for
		records belonging to this host: (leave blank for default TTL)</label></td>
		<td><form:input path="${objectBase}.hostTTL" /></td>
	</tr>

	<tr>
		<td><label for="forceUpdates">Remove conflicting DNS
		records on update</label></td>
		<td><form:checkbox path="${objectBase}.forceDNSUpdates" /></td>
	</tr>
</table>