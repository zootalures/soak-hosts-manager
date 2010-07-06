
<%
	pageContext.setAttribute("TITLE",
			"Move hosts to a different subnet");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

Please select which parts of the selected hosts you wish to edit:

<form:form commandName="command">
	<spring:hasBindErrors name="command">
		<div class="errorBox">Please fix all issues before continuing</div>
		<form:errors cssClass="errorBox" />
	</spring:hasBindErrors>
	<c:if test="${subnetFull}">
		<div class="errorBox">One or more hosts cannot be moved to the
		selected subnet. The host type may not be permitted on that subnet or
		the subnet may be full.</div>
	</c:if>

	<input type="hidden" name="_flowExecutionKey"
		value="${flowExecutionKey}">
	<table>
		<tr>
			<td><label for="newSubnet">Move hosts to subnet: </label></td>
			<td><form:select path="newSubnet">
				<form:option value="">Select a subnet </form:option>
				<form:options items="${subnets}" itemLabel="displayString"
					itemValue="id" />
			</form:select><form:errors cssClass="errorBox" path="newSubnet" /></td>
		</tr>
	</table>
	<br />
	<input type="submit" name="_eventId_submit"
		value="Edit/choose addresses" />
</form:form>


<%@ include file="/footer.include.jsp"%>