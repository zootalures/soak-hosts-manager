<%@ include file="/base.include.jsp"%>

Undo command :
<a href="<c:url value="/undo/showCommand.do"><c:param name="id" value="${bean.storedCommand.id}"/></c:url>"><b>${bean.storedCommand.commandDescription }</b></a>
<c:if test="${!(empty cmd.storedCommand.changeComments)}">(${cmd.changeComments})</c:if>
performed by user
<b>${bean.storedCommand.user}</b>
