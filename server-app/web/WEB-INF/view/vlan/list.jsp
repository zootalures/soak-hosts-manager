
<%
	pageContext.setAttribute("TITLE", "List Vlans");
%>
<%@ include file="/base.include.jsp"%>
<%@ include file="/header.include.jsp"%>

<table class="listTable">
	<thead>
		<tr>
			<th>Vlan Number</th>
			<th>Name</th>
			<th>Description</th>
			<th>Subnets</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${vlans}" var="vlan" varStatus="st">
			<tr class="${st.index%2==0?'even':'odd' }">
				<td>${vlan.number }</td>
				<td>${vlan.name }</td>
				<td>${vlan.description }</td>
				<td><c:forEach items="${vlan.subnets}" var="subnet">
					<a
						href="<c:url value="/subnet/view.do">
				 <c:param name="id" value="${subnet.id }"/></c:url>">
					${subnet.minIP.hostAddress } : ${subnet.name }<br />
					</a>
				</c:forEach></td>
				<td><a class="showButton"
					href="<c:url value="/vlan/show.do" >
<c:param name="id">${vlan.id}</c:param>
</c:url>">
				<span>show</span></a> <authz:authorize ifAllGranted="ROLE_SUPERVISOR">
					<a class="editButton"
						href="<c:url value="/admin/vlan/edit.do" >
				<c:param name="id">${vlan.id}</c:param>
</c:url>">
					<span>edit</span></a>
					<a class="deleteButton"
						href="<c:url value="/admin/vlan/delete.do" >
				<c:param name="id">${vlan.id}</c:param>
</c:url>">
					<span>delete</span></a>
				</authz:authorize></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<authz:authorize ifAllGranted="ROLE_SUPERVISOR">
	<a class="actionlink" href="<c:url value="/admin/vlan/edit.do"/>"> New
	vlan</a>
</authz:authorize>
<%@ include file="/footer.include.jsp"%>
