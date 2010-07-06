
<%
	pageContext.setAttribute("TITLE", "Delete Vlan ");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<form:form commandName="deleteVlanCommand" method="post">
	<form:hidden path="vlan" />

	<spring:hasBindErrors name="deleteVlanCommand">
Please fix all errors before continuing.
</spring:hasBindErrors>
	<form:errors>
	</form:errors>


You are about to delete the following vlan: 
<soak:renderBean bean="${deleteVlanCommand.vlan}" />

	<c:if test="${!(empty deleteVlanCommand.vlan.subnets)  }">
This vlan is being used by the following subnets:
<ul>
			<c:forEach items="${deleteVlanCommand.vlan.subnets }" var="subnet">
				<li>${subnet.name}, ${subnet.minIP.hostAddress }</li>
			</c:forEach>
		</ul>
Move these subnets to the following vlan: 
<form:select path="moveToVlan" itemValue="id">
			<form:option value="" label="No vlan" />
			<form:options itemLabel="stringRep" items="${vlans}"
				itemValue="id" />
		</form:select>
	</c:if>
	<br/>
	<input type="submit" value="delete vlan" />
</form:form>



<%@ include file="/footer.include.jsp"%>
