<%@ include file="/base.include.jsp"%>

<c:if test="${null==bean.searchTerm}">
	<b>all hosts</b>
</c:if>
<c:if test="${null!=bean.searchTerm}">
hosts matching "<b>${bean.searchTerm}</b>"
</c:if>

<c:if test="${null!=bean.hostClass}">
 of type  <B>${bean.hostClass.name }</B>
</c:if>

<c:if test="${null!=bean.subnet}">
 in subnet <B>${bean.subnet.displayString }</B>
</c:if>


<c:if test="${null!=bean.nameDomain}">
 in the domain <B>${bean.nameDomain.suffix }</B>
</c:if>
<c:if test="${bean.onlyIncludeMyHosts}">
 which belong to  <B>you</B>
</c:if>
.
