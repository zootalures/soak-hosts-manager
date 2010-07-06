
<%
	pageContext.setAttribute("TITLE", "View Host Change ");
	pageContext.setAttribute("cmdMenu", "host-cmds");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>
<table>
	<tr>
		<th><c:choose>
			<c:when test="${null!=previous}">

				<a
					href="<c:url value="/host/showChange.do"><c:param name="id" value="${previous.id }"/></c:url>">
				&lt;&lt; Previous Version</a>
			</c:when>
			<c:otherwise>
			&lt;&lt; Previous Version
			</c:otherwise>
		</c:choose></th>
		<th>Version after this change</th>
		<th><c:choose>
			<c:when test="${null!=next}">

				<a
					href="<c:url value="/host/showChange.do"><c:param name="id" value="${next.id }"/></c:url>">
				Next Version &gt;&gt;</a>
			</c:when>
			<c:otherwise>
			Next Version &gt;&gt;
			</c:otherwise>
		</c:choose></th>
	</tr>
	<tr>
		<td><c:if test="${null!=previous}">
			<a
				href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${previous.commandId }"/></c:url>">${previous.commandDescription}</a>
		</c:if></td>
		<td><a
			href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${change.commandId }"/></c:url>">${change.commandDescription}</a></td>
		<td><c:if test="${null!=next}">
			<a
				href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${next.commandId }"/></c:url>">${next.commandDescription
			}<a>
		</c:if></td>
	</tr>
	<tr>
		<td>${previous.changeComments}</td>
		<td>${change.changeComments}</td>
		<td>${next.changeComments}</td>
	</tr>
	<tr>
		<td><c:choose>
			<c:when test="${null!=previous}">
				Version as of ${previous.changeDate } (changed by ${previous.userId })
				<soak:renderBean bean="${previousHost}" />
			</c:when>
			<c:otherwise>
			No previous version
			</c:otherwise>
		</c:choose></td>
		<td>Version as of ${change.changeDate } (changed by
		${change.userId }) <c:if test="${null!=change.hostXml}">
			<a
				href="<c:url value="/flow/update-host-flow.flow"> 
			<c:param name="id" value="${change.hostId}" /> <c:param name="restoreId" value="${change.id}" />></c:url>">Restore
			to version before this change.</a>
		</c:if>

		<table>
			<tr ${previous==null ||!(changeHost.hostName eq
				previousHost.hostName)? "class='changed'":"" }>
				<td><label> Host name</label></td>
				<td>${changeHost.hostName }</td>
			</tr>
			<tr ${previous==null ||!(changeHost.description eq
				previousHost.description)? "class='changed'":"" }>
				<td><label> Description</label></td>
				<td>${changeHost.description }</td>
			</tr>
			<c:if
				test="${!empty changeHost.hostAliases || (previous!= null && !empty previousHost.hostAliases)}">
				<tr ${previous==null ||!(changeHost.hostAliases eq
					previousHost.hostAliases)? "class='changed'":"" }>
					<td><label>Aliases</label></td>
					<td><c:forEach items="${changeHost.hostAliases}" var="alias">
				${alias.alias} ( ${alias.type}  )<br />
					</c:forEach></td>
				</tr>
			</c:if>
			<tr ${previous==null ||!(changeHost.hostClass eq
				previousHost.hostClass)? "class='changed'":"" }>
				<td><label> Host type</label></td>
				<td>${changeHost.hostClass.name }</td>
			</tr>
			<tr ${previous==null ||!(changeHost.ipAddress eq
				previousHost.ipAddress)? "class='changed'":"" }>
				<td><label> IP address</label></td>
				<td>${changeHost.ipAddress.hostAddress }</td>
			</tr>
			<tr ${previous==null ||!(changeHost.macAddress eq
				previousHost.macAddress)? "class='changed'":"" }>
				<td><label> MAC address</label></td>
				<td>${changeHost.macAddress }</td>
			</tr>
			<tr ${previous==null ||!(changeHost.ownership.orgUnit eq
				previousHost.ownership.orgUnit)? "class='changed'":"" }>
				<td><label> Owner</label></td>
				<td>${changeHost.ownership.orgUnit.name }</td>
			</tr>

			<tr ${previous==null ||!(changeHost.location eq
				previousHost.location)? "class='changed'":"" }>
				<td><label> Location</label></td>
				<td>${changeHost.location }</td>
			</tr>
		</table>
		<td><c:choose>
			<c:when test="${null!=nextHost}">
							Version as of ${next.changeDate } (changed by ${next.userId })
			
				<soak:renderBean bean="${nextHost}" />
			</c:when>
			<c:otherwise>
			No subsequent version
			</c:otherwise>
		</c:choose></td>

	</tr>
</table>


<%@ include file="/footer.include.jsp"%>