<%@ include file="/base.include.jsp"%>

<table>

	<tr>
		<td><label>Suffix</label></td>
		<td>${bean.domain}</td>
	</tr>
	<tr>
		<td><label>Name</label></td>
		<td>${bean.displayName}</td>
	</tr>
	<tr>
		<td><label>Description</label></td>
		<td>${bean.description}</td>
	</tr>
	<tr>
		<td><label>Server</label></td>
		<td>${bean.serverIP.hostAddress}</td>
	</tr>
	<tr>
		<td><label>Port</label></td>
		<td>${bean.serverPort}</td>
	</tr>
	<tr>
		<td><label>TSIG Key</label></td>
		<td><c:if test="${ null!=bean.sigKey}">
		set
		</c:if></td>
	</tr>
	<tr>
		<td><label>Use TCP</label></td>
		<td><c:if test="${ bean.useTCP}">
		Yes
		</c:if>
		<c:if test="${!bean.useTCP }">
		No
		</c:if>
		</td>
	</tr>
	<tr>
		<td><label>Default TTL</label></td>
		<td>${bean.defaultTTL  }
		</td>
	</tr>	<tr>
		<td><label>Stored Serial</label></td>
		<td>${bean.serial  }
		</td>
	</tr>
	<tr>
		<td><label>Last Updated</label></td>
		<td>${bean.lastUpdate }
		</td>
	</tr>
</table>