
<%
	pageContext.setAttribute("TITLE", "Move  multple hosts ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Hosts will be moved to the following IPs on the subnet
${command.newSubnet.name } (${command.newSubnet.minIP.hostAddress
}/${command.newSubnet.networkBits })
<br>
You can review and/or modify the allocated addresses below, when you are
finished click
<b>preview</b>
<form:form commandName="command">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<table class="listTable">
		<table>
			<thead>
				<tr>
				<td></td>
					<th>
					<div style="float: left">Name</div>
					</th>
					<th>
					<div style="float: left">Type</div>
					</th>
					<th>
					<div style="float: left">MAC</div>
					</th>
					<th>Description</th>
					<th>Location</th>
					<th>
					<div style="float: left">Existing IP</div>
					</th>
					<th>
					<div style="float: left">New IP</div>
					</th>

				</tr>
			</thead>
			<tbody>
				<c:forEach items="${command.hosts}" var="host" varStatus="status">

					<tr id="hostrow_${host.id }">
					<td>${status.index+1}</td>
						<td><a
							href="<c:url value="/host/show.do">
							<c:param name="id" value="${host.id }" />
						</c:url>">
						<soak:trimSuffix value="${host.hostName}" /></a> <c:forEach
							items="${host.hostAliases}" var="ha">
							<br />
							<span class="listAlias"><soak:trimSuffix
								value="${ha.alias}" suffix=".bath.ac.uk." /></span>
						</c:forEach></td>
						<td>${ host.hostClass.name}&nbsp;</td>
						<td>${ host.macAddress }&nbsp;</td>
						<td><soak:trimText maxLength="20"
							value="${ host.description }" />&nbsp;</td>
						<td>${ host.location }&nbsp;</td>
						<td>${ host.ipAddress.hostAddress }&nbsp;</td>
						<td><form:input path="hostAddresses[${host.id }]" /></td>
						<td><form:errors  element="span" cssClass="tableErrorBox"
							path="hosts[${status.index }].*" /></td>
					</tr>

				</c:forEach>
			</tbody>
		</table>


		<br />
		<input type="submit" name="_eventId_back" value="Go back" />
		<input type="submit" name="_eventId_submit" value="Preview" />
		</form:form>

		<%@ include file="/footer.include.jsp"%>