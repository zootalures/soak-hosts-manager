<%@ include file="/base.include.jsp"%>
<a id="showHideChangesButton" href="#">Show/hide ${fn:length(changes)} secondary change${fn:length(changes)>1?"s":""}</a>
<div id="secondaryChanges"><c:forEach items="${changes}"
	var="change">
	<soak:renderBean bean="${change}" />
</c:forEach></div>

<script type="text/javascript">
var shown = false;
function show(val){
	if(val){
		YAHOO.preview.seconaryChagnes.show(YAHOO.preview.seconaryChagnes, true); 
	}else{
		YAHOO.preview.seconaryChagnes.hide(YAHOO.preview.seconaryChagnes, false); 
	}
	
	shown = val;
}

function init() {
	YAHOO.namespace("preview");
	YAHOO.preview.seconaryChagnes = new YAHOO.widget.Module("secondaryChanges", { visible: false });
	YAHOO.preview.seconaryChagnes.render();
	YAHOO.preview.showHideChangesButton = new YAHOO.widget.Button("showHideChangesButton");
	YAHOO.preview.showHideChangesButton.addListener("click",function (){ show(!shown);});
	
}
YAHOO.util.Event.onDOMReady(init);
  
</script>