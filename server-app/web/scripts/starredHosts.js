

var setStarredURL= urlBase + "/host/setStarredAjax.do";
var clearAllURL= urlBase + "/host/clearAllStarredAjax.do";
var fragURL= urlBase + "/host/starredHostsFragment.do";
function _setHTMLElementBody(theelementId,text) {	
   var el = document.getElementById(theelementId);
	if(el){
		el.innerHTML =text;
		
	}

}

function _renderMenuElementCallback(data){
		   
		 	SoakStarred.setHTMLElementBody("starredHostsHolder",data.responseText); 
		   
		 

}
function _updateStarredElement(){
	var url = fragURL + "?r=" + Math.random();
	YAHOO.log("updating starred fragment from url "+ url);
	
	YAHOO.util.Connect.asyncRequest('POST',url,
		{success: _renderMenuElementCallback,
		failure:
			function(ob){
				alert("Update failed");
			}
		});
	
}

// SoakStarred Object 
var SoakStarred = {
	// Select all hosts from the given search URL, updates all host rows on the current page to the respective value 
	selectSearch :function (searchUrl,value){
        SoakStarred.startUpdate();
        var elements = YAHOO.util.Dom.getElementsByClassName('hostRow', 'tr');
   				for(var i = 0; i < elements.length;i++){
   					if(value){
	       				YAHOO.util.Dom.addClass(elements[i],"selected");		
		       		}else{
		       			YAHOO.util.Dom.removeClass(elements[i],"selected");
	    	   		}
   				}
		var callbackObj = { 
			success:function(obj){
				
   				SoakStarred.updateStarredElement();
				
			}
		};
		
		YAHOO.util.Connect.asyncRequest('POST',searchUrl,callbackObj);
		return false;
		
   	},
	// Starts an update (currently changes the text in the selected hosts menu) 
	startUpdate: function(){
	 	    SoakStarred.setHTMLElementBody("starredHostsHolder","<img class='star' src='" + urlBase + "images/starredanim.gif'/> Updating ..."); 
	 
    },
    
	// changes the starred status of one or more hosts  
    flipStarred: function (hostIds,value){
		var url = setStarredURL + "?value="+ value ;
		
		for (i =0; i < hostIds.length;i++){
			var hostId = hostIds[i];
	       	url+= "&hostId=" + hostIds[i];
	       	var el = document.getElementById("hostrow_"+hostId);
	       	if(el){
	       		if(value){
	       			YAHOO.util.Dom.addClass(el,"selected");
	       			
	       		}else{
	       			YAHOO.util.Dom.removeClass(el,"selected");
	       		}
	       	}
	       	
		}
		url+="&r=" + Math.random();
		
		var callbackObj = { success:function (obj){SoakStarred.updateStarredElement();} ,
		failure: function(obj){alert("Request failed");}};
		YAHOO.log("Calling" + url);
	    YAHOO.util.Connect.asyncRequest('POST',url,callbackObj);
      }
	,
	
	// helper to over-write the body of an element with some given text   
	setHTMLElementBody:_setHTMLElementBody,
	
	// Updates the menu item    
	updateStarredElement:_updateStarredElement,
	
	//Determine if a given host ID is checked 
	isChecked: function(hostId){
		var el = document.getElementById("hostrow_"+hostId);
		
		if(el  && YAHOO.util.Dom.hasClass(el,"selected")){
			return true;
		}
		return false
	}
	,
	//clears the current selection  
	clearSelection: function(){
		var url = clearAllURL + "?r="+ Math.random() ;
					var elements = YAHOO.util.Dom.getElementsByClassName('hostRow', 'tr');
   				for(var i = 0; i < elements.length;i++){
   			   	  YAHOO.util.Dom.removeClass(elements[i],"selected");
	    		}
		
		var callbackObj = { success:function (obj){SoakStarred.updateStarredElement();} ,
   	
		failure: function(obj){alert("Request failed");}};
		YAHOO.log("Calling" + url);
	    YAHOO.util.Connect.asyncRequest('POST',url,callbackObj);
      
	
	}
 };
 