var POPUP_DIV = document.createElement("DIV");
var ODL_DRAGGING = null;
POPUP_DIV.style.zIndex = "1000";
POPUP_DIV.noob = true;
var EXPIRED_CONTENT = 0;
var MOUSEOUT_DESTROY=false;
var countdown = 0;
var destroydelay = 100;
var docountdown = false;
var transbox = document.createElement("DIV");
	transbox.style.display="block";
	transbox.style.position = "absolute";
	transbox.style.top = 0;
	transbox.style.left = 0;
	transbox.style.backgroundColor="black"
	transbox.style.opacity = ".7";
	transbox.style.filter = "alpha(opacity=70)";	
	transbox.style.zIndex = 99;//calendar.style.zIndex - 1;	
	transbox.style.width ="100%";//calendar.style.zIndex - 1;
    transbox.style.height ="100%";//calendar.style.zIndex - 1;
var transboxon = false;

var onLoadArray = new Array();
var onUnloadArray = new Array();
var cleanupFunctions = new Array();

var blinking = false;
var blinkcounter = 0;
var blinked = new Array();
	blinked['objects'] = new Array();
	blinked['class1'] = new Array();
	blinked['class2'] = new Array();	
	blinked['delay'] = new Array();

function blinkObject(id,class1,class2,delay)
{
 	var obj,pos;
 	try
 	{
		obj = document.getElementById(id);	
		pos = findInArray(obj,blinked['objects']);
		if(pos == -1)
			pos = blinked['objects'].length;

		blinked['objects'][pos] = obj;
		blinked['class1'][pos] = class1;
		blinked['class2'][pos] = class2;	
		blinked['delay'][pos] = delay;
		
		if(!blinking)
			executeBlinks();
	}
	catch(e)
	{}	
}
function unblinkObject(id)
{
 	var obj,pos;
 	try
 	{
		obj = document.getElementById(id);	
		pos = findInArray(obj,blinked['objects']);
		if(pos != -1)
		{
			blinked['objects'] = removePosFromArray(pos,blinked['objects']);
			blinked['class1'] = removePosFromArray(pos,blinked['class1']);
			blinked['class2'] = removePosFromArray(pos,blinked['class2']);
			blinked['delay'] = removePosFromArray(pos,blinked['delay']);
		}		
	}
	catch(e)
	{}	
}

function executeBlinks()
{
  	var i;
 	
	if(blinkcounter < 65535)
		blinkcounter ++;
	else
		blinkcounter = 0;
	
	for(i=0;i<blinked['objects'].length;i++)
	{
		if((blinkcounter % blinked['delay'][i]) == 0)
		{
		 	try
		 	{			
				if(blinked['objects'][i].className == blinked['class1'][i])
					blinked['objects'][i].className = blinked['class2'][i];
				else
					blinked['objects'][i].className = blinked['class1'][i];
			}
			catch(e)
			{
			 	blinked['objects'] = removePosFromArray(i,blinked['objects']);
				blinked['class1'] = removePosFromArray(i,blinked['class1']);
				blinked['class2'] = removePosFromArray(i,blinked['class2']);
				blinked['delay'] = removePosFromArray(i,blinked['delay']);				
			}			
		}			
	}
	
	if(blinked['objects'].length > 0)
	{
	 	blinking=true;
		window.setTimeout(executeBlinks,100);	
	}
	else
		blinking = false;
}
			
function executeOnPageLoad(func)
{
	onLoadArray[onLoadArray.length]	= func;				
}
function executeOnPageUnload(func)
{
	onUnloadArray[onUnloadArray.length]	= func;
}
function doExecuteOnPageLoad(event)
{
 	var i=0;
	for(i=0;i<onLoadArray.length;i++)
	{
		onLoadArray[i](event);
	}
}
function doExecuteOnPageUnload(event)
{
	var i=0;
	for(i=0;i<onUnloadArray.length;i++)
	{
		onUnloadArray[i](event);
	}
}
window.onload = doExecuteOnPageLoad;
window.onunload = doExecuteOnPageUnload;

function doTransBoxEffect()
{
	var dim = getAbsolutePageSize();
	
	if(transboxon)
		undoTransBoxEffect();
		
 	transbox.style.width = dim[0];
	transbox.style.height = dim[1];		
	document.body.appendChild(transbox);
	transboxon=true;   
}
function undoTransBoxEffect()
{
    document.body.removeChild(transbox);
    transboxon=false;   
}
function popupRegisterOnDestroyCleanupFunction(funct)
{
	cleanupFunctions[cleanupFunctions.length] = funct;
}
function popupUnregisterOnDestroyCleanupFunction(funct)
{
 	var i=0,j=0,k=0;
 	k = cleanupFunctions.length;
 	
	for(i=0;i<k;i++)
	{
		if(cleanupFunctions[i] == funct)
		for(j=i;j<(k-1);j++)
		{
			cleanupFunctions[j] = cleanupFunctions[j+1];
		}
		cleanupFunctions.pop();
	}
}
function popupIsCreatedFrom(obj)
{
	if(POPUP_DIV.firstChild == obj)
		return true;
	else
		return false;
}
function destroyPopup()
{
 	var i=0;
 	var funct;
 	if(EXPIRED_CONTENT != 0)
	{
		POPUP_DIV.removeChild(EXPIRED_CONTENT);
		EXPIRED_CONTENT.style.display="none";
		document.body.appendChild(EXPIRED_CONTENT);		
		EXPIRED_CONTENT = 0;	
	}
	else
	{
		removeTextPopupFormatting();	
		POPUP_DIV.innerHTML="";
	}
	
	POPUP_DIV.style.display = "none";
    
    if(transboxon)
        undoTransBoxEffect();
        
    while(cleanupFunctions.length > 0)
	{
		funct = cleanupFunctions[0];
		funct();		
		popupUnregisterOnDestroyCleanupFunction(funct);
	}
}
function createPopup()
{
	if(POPUP_DIV.noob == true)
	{
		document.body.appendChild(POPUP_DIV);
		POPUP_DIV.noob=false;
	}
	POPUP_DIV.style.display = "block";
	POPUP_DIV.style.position = "absolute";
}
function makePopupDraggableBy(dragger_id)
{
 	if(ODL_DRAGGING != null)
 	{
		delete ODL_DRAGGING;
	}
	new Draggable(POPUP_DIV,{revert: false, handle:dragger_id, starteffect:null, endeffect:null});
}

function destroyPopupCountDown()
{
 	if(docountdown)
 	{
		if(countdown < destroydelay)
		{
		 	countdown++;
			window.setTimeout(destroyPopupCountDown,10);
		}
		else
		{
			destroyPopup();
		}
	}
}
function cancelDestroyPopupCountDown()
{
 	docountdown = false;	
}
function cancelSingleMouseoutDisengage()
{
 	MOUSEOUT_DESTROY = false;	
}
function forceStartDelayedMouseoutDestroy()
{
    docountdown = true;
 	countdown = 0;
	destroyPopupCountDown();
}
function delayedMouseoutDestroy(event)
{
	var obj = getRelatedTargetFromEvent(event);
	
	if((obj != null)&&(!is_child_of(POPUP_DIV,obj)))
	{	 
	 	docountdown = true;
	 	countdown = 0;
		destroyPopupCountDown();
	}
}
function MouseOutDestroy(event)
{
 	var obj = getRelatedTargetFromEvent(event);
	
	if((obj != null)&&(!is_child_of(POPUP_DIV,obj)))
	{	 
		  destroyPopup();
	}
}
function MouseOutDestroyOnce(event)
{
 	var obj = null;
	if(MOUSEOUT_DESTROY)
	{
		try
		{
			if(event.toElement)
			{				
				obj = event.toElement;	
			}
			else
			{ 
				if(event.relatedTarget)
				{				
					obj = event.relatedTarget;		
				}
			}
		}catch(e){}
		
		if((obj != null)&&(!is_child_of(POPUP_DIV,obj)))
		{	 
			destroyPopup();
			MOUSEOUT_DESTROY=false;
		}		
	}
}
function initPopupForDelayedMouseoutDisengage()
{
	var browser=navigator.appName;	
	if((browser == "Netscape")||(browser == "Opera"))
	{
	 	POPUP_DIV.setAttribute('onmouseover',"cancelDestroyPopupCountDown(event)");
	 	POPUP_DIV.setAttribute('onmouseout',"MouseOutDestroy(event)");
	}
	else
	{
		POPUP_DIV.attachEvent('onmouseover',cancelDestroyPopupCountDown);
		POPUP_DIV.attachEvent('onmouseout',MouseOutDestroy);
	}	
}
function initPopupForSingleDelayedMouseoutDisengage()
{
    MOUSEOUT_DESTROY=true;
	var browser=navigator.appName;	
	if((browser == "Netscape")||(browser == "Opera"))
	{
	 	POPUP_DIV.setAttribute('onmouseover',"cancelDestroyPopupCountDown(event)");
	 	POPUP_DIV.setAttribute('onmouseout',"MouseOutDestroyOnce(event)");
	}
	else
	{
		POPUP_DIV.attachEvent('onmouseover',cancelDestroyPopupCountDown);
		POPUP_DIV.attachEvent('onmouseout',MouseOutDestroyOnce);
	}	
}
function initPopupForMouseoutDisengage()
{
 	var browser=navigator.appName;	
	if((browser == "Netscape")||(browser == "Opera"))
	{
	 	POPUP_DIV.setAttribute('onmouseout',"MouseOutDestroy(event)");
	}
	else
	{
		POPUP_DIV.attachEvent('onmouseout',MouseOutDestroy);
	}
}
function initPopupForSingleMouseoutDisengage()
{
 	MOUSEOUT_DESTROY=true;
	var browser=navigator.appName;	
	
	if((browser == "Netscape")||(browser == "Opera"))
	{
	 	POPUP_DIV.setAttribute('onmouseout',"MouseOutDestroyOnce(event)");
	}
	else
	{
		POPUP_DIV.attachEvent('onmouseout',MouseOutDestroyOnce);
	}	
}
function destroyPopupIfNotMouseoverEvent(event)
{
	var obj = getRelatedTargetFromEvent(event);
	if((obj != POPUP_DIV)&&(!is_child_of(POPUP_DIV,obj)))
		destroyPopup();
}
 


function createIdSourcedPopupCentered(id)
{
	var obj = document.getElementById(id);
	createObjectSourcedPopupCentered(obj);
}

function createObjectSourcedPopupCentered(obj)
{
	var dim_page = getAbsolutePageSize();
	var dim_obj	= getObjectSize(obj);

	var x = parseInt((dim_page[0] - dim_obj[0])/2);
	var y = parseInt((dim_page[1] - dim_obj[1])/2);
	
	createObjectSourcedPopupFromPosition(x,y,obj);	
}



function createPopupFromCursor(event,offsX,offsY,text)
{ 
 	if(event.pageX)
 	{
		x = event.pageX;
		y = event.pageY;
	}
	else		//IE
	{
		x = event.clientX + document.body.scrollLeft;
		y = event.clientY + document.body.scrollTop;	
	}
	x-= offsX;
	y-= offsY;
		 
	createPopupFromPosition(x,y,text)	
}
function createPopupFromObject(obj,text)
{
	var pos = findPos(obj);
	var CornerRightBottomX = pos[0] + obj.offsetWidth;
	var CornerRightBottomY = pos[1] + obj.offsetHeight;	
	createPopupFromPosition(CornerRightBottomX,CornerRightBottomY,text)	
}
function createPopupFromObjectOverlapped(obj,text)
{
	var pos = findPos(obj);
	var CornerRightBottomX = pos[0];
	var CornerRightBottomY = pos[1];			
	createPopupFromPosition(CornerRightBottomX,CornerRightBottomY,text)	
}

function createPopupFromPosition(x,y,text)
{
 	destroyPopup();
 	
	doTextPopupFormatting();
	
	POPUP_DIV.style.top = y + "px";	
	POPUP_DIV.style.left = x + "px";	
	POPUP_DIV.innerHTML = text;			
	createPopup();
}
function doTextPopupFormatting()
{
	POPUP_DIV.style.padding = "2px";
	POPUP_DIV.style.backgroundColor = "rgb(255,255,225)";
	POPUP_DIV.style.border = "1px solid black";
	POPUP_DIV.style.overflow = "hidden";
	POPUP_DIV.style.fontFamily = "arial, verdana, tahoma";
	POPUP_DIV.style.fontSize = "11px";
}
function removeTextPopupFormatting()
{
	POPUP_DIV.style.padding = "0px";
	POPUP_DIV.style.backgroundColor = "transparent";
	POPUP_DIV.style.border = "0px";
	POPUP_DIV.style.overflow = "hidden";
	POPUP_DIV.style.fontFamily = "times new roman, verdana, tahoma";
	POPUP_DIV.style.fontSize = "12px";	
}
function createObjectSourcedPopupFromId(what,obj_id)
{
    var obj = document.getElementById(obj_id);
    createObjectSourcedPopupFromObject(what,obj);
}
function createObjectSourcedPopupFromIdOverlapped(what,obj_id,x,y)
{
    var obj = document.getElementById(obj_id);
    createObjectSourcedPopupFromObjectOverlapped(what,obj,x,y);
}
function createObjectSourcedPopupFromObject(what,obj)
{
	var pos = findPos(obj);
	var CornerRightBottomX = pos[0] + obj.offsetWidth;
	var CornerRightBottomY = pos[1] + obj.offsetHeight;	
	createObjectSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,what)
}

function createObjectSourcedPopupFromObjectOverlapped(what,obj,x,y)
{
	var pos = findPos(obj);	
	var CornerRightBottomX = pos[0]-x;
	var CornerRightBottomY = pos[1]-y;	
	
	createObjectSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,what)
}
function createObjectSourcedPopupFromPosition(x,y,obj)
{
	destroyPopup();
	EXPIRED_CONTENT = obj;		 	
	POPUP_DIV.appendChild(obj);
	obj.style.display="block";	

	POPUP_DIV.style.position = "absolute";
	POPUP_DIV.style.top = y + "px";	
	POPUP_DIV.style.left = x + "px";
	POPUP_DIV.style.overflow = "hidden";
	POPUP_DIV.appendChild(obj);	
		
	createPopup();
}

function createIdSourcedPopupCentered(src_id)
{
     var obj = document.getElementById(src_id);
     var dim = getObjectSize(obj);
     
     var x = (document.body.offsetWidth - dim[0])/2;
     var y = (document.body.offsetHeight - dim[1])/2;
     
     createObjectSourcedPopupFromPosition(x,y,obj);
}
function createIdSourcedPopupFromId(src_id,obj_id)
{
    var obj = document.getElementById(obj_id);
    createIdSourcedPopupFromObject(src_id,obj);
}
function createIdSourcedPopupFromIdOverlapped(src_id,obj_id,x,y)
{
    var obj = document.getElementById(obj_id);
    createIdSourcedPopupFromObjectOverlapped(src_id,obj,x,y);
}
function createIdSourcedPopupFromObject(id,obj)
{
	var pos = findPos(obj);
	var CornerRightBottomX = pos[0] + obj.offsetWidth;
	var CornerRightBottomY = pos[1] + obj.offsetHeight;	
	createIdSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,id);
}
function createIdSourcedPopupFromId_SAFE(src_id,obj_id)
{
    var obj = document.getElementById(obj_id);
    createIdSourcedPopupFromObject_SAFE(src_id,obj);
}
function createIdSourcedPopupFromObject_SAFE(id,obj)
{
	var winW = 0, winH = 0;

	var pos = findPos(obj);
	var CornerRightBottomX = pos[0] + obj.offsetWidth;
	var CornerRightBottomY = pos[1] + obj.offsetHeight;
	
	var src = document.getElementById(id);	
	
	if (parseInt(navigator.appVersion)>3) 
	{
 		if (navigator.appName=="Netscape") 
 		{
  			winW = window.innerWidth;
  			winH = window.innerHeight;
 		}
 		if (navigator.appName.indexOf("Microsoft")!=-1) 
	 	{
  			winW = document.body.offsetWidth;
  			winH = document.body.offsetHeight;
 		}
	}
	
	POPUP_DIV.style.visibility="hidden";
			
	createIdSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,id);	
	
	if((pos[0] + src.offsetWidth) > winW)
	{		
		CornerRightBottomX = pos[0] - obj.offsetWidth - src.offsetWidth;		
	}	
	if((pos[1] + src.offsetHeight) > winH)
	{
		CornerRightBottomY = pos[1] - obj.offsetHeight - src.offsetHeight;		
	}
	
	createIdSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,id);	
	
	POPUP_DIV.style.visibility="visible";
	
}

function createIdSourcedPopupFromObjectOverlapped(id,obj,x,y)
{
	var pos = findPos(obj);
	var CornerRightBottomX = pos[0] - x;
	var CornerRightBottomY = pos[1] - y;	
	createIdSourcedPopupFromPosition(CornerRightBottomX,CornerRightBottomY,id);
}
function createIdSourcedPopupFromPosition(x,y,id)
{
 	var obj = document.getElementById(id);
	createObjectSourcedPopupFromPosition(x,y,obj)
}

function createIdSourcedPopupFromCursorOverlapped(obj,id,event,offsX,offsY)
{ 
 	if(event.pageX)
 	{
		x = event.pageX;
		y = event.pageY;
	}
	else		//IE
	{
		x = event.clientX;
		y = event.clientY + document.body.scrollTop;	
	}
	x-= offsX;
	y-= offsY;
		 
	var obj2 = document.getElementById(id); 
	if(obj2)	
		createObjectSourcedPopupFromPosition(x,y,obj2);
}