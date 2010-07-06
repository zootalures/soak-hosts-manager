<%@ include file="/base.include.jsp"%>
<h3>Hosts Summary</h3>
There are currently
<b> ${bean.totalHosts}</b>
stored in the hosts database.

<br />
Broken down by type:
<table class="listTable">
	<thead>
		<tr>
			<th>Type</th>
			<th>count</th>
			<th>%</th>
		</tr>
	</thead>
	<c:forEach items="${bean.hostClassesByNum }" var="hc">
		<tr>
			<td>${hc.name}</td>
			<td><a
				href="<c:url value="/host/search.do">
	<c:param name="hostClass" value="${hc.id }" />
</c:url>">${bean.hostsByType[hc]}</a></td>
			<td><fmt:formatNumber pattern="###"
				value="${ 100.0*( bean.hostsByType[hc] / bean.totalHosts) }" />%</td>
		</tr>
	</c:forEach>
</table>
<br>
