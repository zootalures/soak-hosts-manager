<%@ include file="/base.include.jsp"%>
<div class="cmdListEntry">
<div class="cmdListTitle">DNS system</div>
<div>
<ul>
	<c:forEach items="${bean.changes}" var="change">
		<li><c:if test="${change.addition}">
			<b>Addition</b> of record 
</c:if> <c:if test="${!change.addition}">
			<b>Deletion</b> of DNS record
</c:if> ${change.record} <c:if test="${change.addition}">
to
</c:if> <c:if test="${!change.addition}">
from 
</c:if> <B>${change.record.zone.displayName}</B></li>
	</c:forEach>

</ul>
</div>
</div>