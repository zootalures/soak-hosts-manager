
<%
	pageContext.setAttribute("TITLE", "Select Host Type");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>


<form:form commandName="editHostCmd" method="post">
	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<form:errors path="newHost.hostClass" cssClass="errorBox" />
	<table>
		<c:forEach items="${hostClasses}" var="hc">
			<tr>
				<td><form:radiobutton path="newHost.hostClass" value="${hc.id}" />
				${hc.name}</td>
				<td>${hc.description}</td><td><soak:helpLink cssClass="helpButton"
					path="Host Types#${hc.id}" title="Help about host type ${hc.name }" /></td>
			</tr>
		</c:forEach>
	</table>
	<input type="hidden" name="_eventId" value="submit">
	<input type="submit" value="Continue" />
</form:form>

<soak:helpLink path="How to add a host record"
	title="HOWTO: Add a host record" />
<soak:helpLink path="Host Types" title="About host types" />

<%@ include file="/footer.include.jsp"%>
