// function that gets the target from an event
function getTargetFromEvent(event)
{
	var obj;
	try
	{
		if (!event)
	  	{
	  		var event = window.event;
	  	}
		if (event.target)
	  	{
	  		obj = event.target;
	  	}
		else if (event.srcElement)
	  	{
	  		obj = event.srcElement;
	  	}
		if (obj.nodeType == 3) // defeat Safari bug
	  	{
	  		obj = obj.parentNode;
	  	}
	}catch(e){return null}
	return obj;	
}
// function that gets the related target from an event
function getRelatedTargetFromEvent(event)
{
	var obj;
	if (!event)
  	{
  		var event = window.event;
  	}
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
	return obj;
}
// function that determines if an object is the parent of another object 
function is_child_of(parent, child) 
{
	if(child != null ) 
	{
		while(child.parentNode != null)
		{
			child = child.parentNode;
			if(child == parent) 
			{
				return true;
			}
		}
	}
	return false;
}

// function that gets the mouse coordonates from an event
function getMouseCoords(event)
{
 	var ret=[];
 	if(!event)
 		event=window.event;
	if(!event.pageX) //IE
 	{
		x = event.clientX;
		y = event.clientY + document.body.scrollTop;		
	}
	else		
	{
		x = event.pageX;
		y = event.pageY;	
	}
	return [x,y]; 
}


// function that gets the absolute size of the page
function getAbsolutePageSize()
{
 	var w = document.body.offsetWidth;
	var h = document.body.offsetHeight;	
	
    if(window.innerWidth)
    {
		if(w<window.innerWidth)
			w=window.innerWidth;	
	}
    
    if(window.innerHeight)
    {
		if(h<window.innerHeight)
			h=window.innerHeight;	
	}
	
	return [w,h];
}

// function that gets the absolute size of an object
function getObjectSize(obj)
{
 	var parent = obj.parentNode;
	var oldposition = obj.style.position;
	var olddisplay = obj.style.display;
	var oldvisibility = obj.style.visibility;
	var w,h;
	
	obj.style.position="absolute";
	document.body.appendChild(obj);
	obj.style.visibility = "hidden";
	obj.style.display="block";
		
		w = obj.offsetWidth;
		h = obj.offsetHeight;	
	
	parent.appendChild(obj);
	obj.style.display=olddisplay;
	obj.style.position=oldposition;
	obj.style.visibility = oldvisibility;
		
	return [w,h];
}


// function that finds the position of an object relative to the document body
// ATENTION !!! does not work if the obj is inside a div that has overflow-scroll, and the scroll of the div is not in the 0 position
function findPos(obj) 
{
	var curleft = curtop = 0;
	if (obj.offsetParent) 
	{
		do 
		{
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		} 
		while (obj = obj.offsetParent);
	}
	return [curleft,curtop];
}

// function that checks if an element is inside an array, and returns the element position (or -1 if the element is not found)
function findInArray(el,arr)
{
	for(i=0;i<arr.length;i++)
	{
		if(el == arr[i])
			return i;
	}	
	return -1;
}

// function that removes an element from an array
function removePosFromArray(pos,arr)
{
	for(i=pos;i<(arr.length-1);i++)
	{
		arr[i] = arr[i+1];
	}	
	arr.pop();
	return arr;
}

// function that removes an element from an array
function removeObjFromArray(el,arr)
{
	var pos = findInArray(el,arr);
	return removePosFromArray(pos,arr);
}


//javascript equivalent of php urldecode (replaces url special chars with normal characters)
function urldecode( str ) 
{
    // http://kevin.vanzonneveld.net
    // +   original by: Philip Peterson
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +      input by: AJ
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Brett Zamir (http://brettz9.blogspot.com)
    // +      input by: travc
    // +      input by: Brett Zamir (http://brettz9.blogspot.com)
    // +   bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Lars Fischer
    // %          note 1: info on what encoding functions to use from: http://xkr.us/articles/javascript/encode-compare/
    // *     example 1: urldecode('Kevin+van+Zonneveld%21');
    // *     returns 1: 'Kevin van Zonneveld!'
    // *     example 2: urldecode('http%3A%2F%2Fkevin.vanzonneveld.net%2F');
    // *     returns 2: 'http://kevin.vanzonneveld.net/'
    // *     example 3: urldecode('http%3A%2F%2Fwww.google.nl%2Fsearch%3Fq%3Dphp.js%26ie%3Dutf-8%26oe%3Dutf-8%26aq%3Dt%26rls%3Dcom.ubuntu%3Aen-US%3Aunofficial%26client%3Dfirefox-a');
    // *     returns 3: 'http://www.google.nl/search?q=php.js&ie=utf-8&oe=utf-8&aq=t&rls=com.ubuntu:en-US:unofficial&client=firefox-a'
    
    var histogram = {}, ret = str.toString(), unicodeStr='', hexEscStr='';
    
    var replacer = function(search, replace, str) {
        var tmp_arr = [];
        tmp_arr = str.split(search);
        return tmp_arr.join(replace);
    };
    
    // The histogram is identical to the one in urlencode.
    histogram["'"]   = '%27';
    histogram['(']   = '%28';
    histogram[')']   = '%29';
    histogram['*']   = '%2A';
    histogram['~']   = '%7E';
    histogram['!']   = '%21';
    histogram['%20'] = '+';
    histogram['\u00DC'] = '%DC';
    histogram['\u00FC'] = '%FC';
    histogram['\u00C4'] = '%D4';
    histogram['\u00E4'] = '%E4';
    histogram['\u00D6'] = '%D6';
    histogram['\u00F6'] = '%F6';
    histogram['\u00DF'] = '%DF'; 
    histogram['\u20AC'] = '%80';
    histogram['\u0081'] = '%81';
    histogram['\u201A'] = '%82';
    histogram['\u0192'] = '%83';
    histogram['\u201E'] = '%84';
    histogram['\u2026'] = '%85';
    histogram['\u2020'] = '%86';
    histogram['\u2021'] = '%87';
    histogram['\u02C6'] = '%88';
    histogram['\u2030'] = '%89';
    histogram['\u0160'] = '%8A';
    histogram['\u2039'] = '%8B';
    histogram['\u0152'] = '%8C';
    histogram['\u008D'] = '%8D';
    histogram['\u017D'] = '%8E';
    histogram['\u008F'] = '%8F';
    histogram['\u0090'] = '%90';
    histogram['\u2018'] = '%91';
    histogram['\u2019'] = '%92';
    histogram['\u201C'] = '%93';
    histogram['\u201D'] = '%94';
    histogram['\u2022'] = '%95';
    histogram['\u2013'] = '%96';
    histogram['\u2014'] = '%97';
    histogram['\u02DC'] = '%98';
    histogram['\u2122'] = '%99';
    histogram['\u0161'] = '%9A';
    histogram['\u203A'] = '%9B';
    histogram['\u0153'] = '%9C';
    histogram['\u009D'] = '%9D';
    histogram['\u017E'] = '%9E';
    histogram['\u0178'] = '%9F';
 
    for (unicodeStr in histogram) {
        hexEscStr = histogram[unicodeStr]; // Switch order when decoding
        ret = replacer(hexEscStr, unicodeStr, ret); // Custom replace. No regexing
    }
    
    // End with decodeURIComponent, which most resembles PHP's encoding functions
    ret = decodeURIComponent(ret);
 
    return ret;
}

//javascript equivalent of php urlencode (replaces normal characters with url special chars)
function urlencode( str ) 
{
    // http://kevin.vanzonneveld.net
    // +   original by: Philip Peterson
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +      input by: AJ
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Brett Zamir (http://brettz9.blogspot.com)
    // +   bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +      input by: travc
    // +      input by: Brett Zamir (http://brettz9.blogspot.com)
    // +   bugfixed by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Lars Fischer
    // %          note 1: info on what encoding functions to use from: http://xkr.us/articles/javascript/encode-compare/
    // *     example 1: urlencode('Kevin van Zonneveld!');
    // *     returns 1: 'Kevin+van+Zonneveld%21'
    // *     example 2: urlencode('http://kevin.vanzonneveld.net/');
    // *     returns 2: 'http%3A%2F%2Fkevin.vanzonneveld.net%2F'
    // *     example 3: urlencode('http://www.google.nl/search?q=php.js&ie=utf-8&oe=utf-8&aq=t&rls=com.ubuntu:en-US:unofficial&client=firefox-a');
    // *     returns 3: 'http%3A%2F%2Fwww.google.nl%2Fsearch%3Fq%3Dphp.js%26ie%3Dutf-8%26oe%3Dutf-8%26aq%3Dt%26rls%3Dcom.ubuntu%3Aen-US%3Aunofficial%26client%3Dfirefox-a'
                             
    var histogram = {}, tmp_arr = [], unicodeStr='', hexEscStr='';
    var ret = (str+'').toString();
    
    var replacer = function(search, replace, str) {
        var tmp_arr = [];
        tmp_arr = str.split(search);
        return tmp_arr.join(replace);
    };
    
    // The histogram is identical to the one in urldecode.
    histogram["'"]   = '%27';
    histogram['(']   = '%28';
    histogram[')']   = '%29';
    histogram['*']   = '%2A';
    histogram['~']   = '%7E';
    histogram['!']   = '%21';
    histogram['%20'] = '+';
    histogram['\u00DC'] = '%DC';
    histogram['\u00FC'] = '%FC';
    histogram['\u00C4'] = '%D4';
    histogram['\u00E4'] = '%E4';
    histogram['\u00D6'] = '%D6';
    histogram['\u00F6'] = '%F6';
    histogram['\u00DF'] = '%DF';
    histogram['\u20AC'] = '%80';
    histogram['\u0081'] = '%81';
    histogram['\u201A'] = '%82';
    histogram['\u0192'] = '%83';
    histogram['\u201E'] = '%84';
    histogram['\u2026'] = '%85';
    histogram['\u2020'] = '%86';
    histogram['\u2021'] = '%87';
    histogram['\u02C6'] = '%88';
    histogram['\u2030'] = '%89';
    histogram['\u0160'] = '%8A';
    histogram['\u2039'] = '%8B';
    histogram['\u0152'] = '%8C';
    histogram['\u008D'] = '%8D';
    histogram['\u017D'] = '%8E';
    histogram['\u008F'] = '%8F';
    histogram['\u0090'] = '%90';
    histogram['\u2018'] = '%91';
    histogram['\u2019'] = '%92';
    histogram['\u201C'] = '%93';
    histogram['\u201D'] = '%94';
    histogram['\u2022'] = '%95';
    histogram['\u2013'] = '%96';
    histogram['\u2014'] = '%97';
    histogram['\u02DC'] = '%98';
    histogram['\u2122'] = '%99';
    histogram['\u0161'] = '%9A';
    histogram['\u203A'] = '%9B';
    histogram['\u0153'] = '%9C';
    histogram['\u009D'] = '%9D';
    histogram['\u017E'] = '%9E';
    histogram['\u0178'] = '%9F';
    
    // Begin with encodeURIComponent, which most resembles PHP's encoding functions
    ret = encodeURIComponent(ret);
 
    for (unicodeStr in histogram) {
        hexEscStr = histogram[unicodeStr];
        ret = replacer(unicodeStr, hexEscStr, ret); // Custom replace. No regexing
    }
    
    // Uppercase for full PHP compatibility
    return ret.replace(/(\%([a-z0-9]{2}))/g, function(full, m1, m2) {
        return "%"+m2.toUpperCase();
    });
    
    return ret;
}


//function that creates a cookie with the givven name, value and time to live 
function createCookie(name,value,days)
{
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

//function that reads a cookie
function readCookie(name) 
{
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

//function that erases the value of a cookie
function eraseCookie(name) 
{
	createCookie(name,"",-1);
}

function trim(str)
{
	return str.replace(/^\s+|\s+$/g,"");
}
function trim(str)
{
 	return str.replace(/^\s+/,"");
}
function trim(str)
{
 	return str.replace(/\s+$/,"");
}


function strstr (haystack, needle, bool) 
{
    var pos = 0;
    
    haystack += '';
    pos = haystack.indexOf(needle);
    if(pos == -1) 
	{
        return false;
    } 
	else
	{
        if (bool)
		{
            return haystack.substr( 0, pos );
        } 
		else
		{
            return haystack.slice( pos );
        }
    }
}

