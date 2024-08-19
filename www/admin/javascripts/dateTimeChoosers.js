var DT_div;
var DT_day;
var DT_mon;
var DT_year;
var DT_time;
var DT_close;
var DT_save;
var DT_cont;
var DT_selDay;
var DT_hour;
var DT_minute;
var DT_second;
var destinationINP;
var destinationTXT;
var type=0;

function commonCreate(ts,obj,id_destINP,id_destTXT,cur_lang)
{
 	language = cur_lang;
 	
	destinationINP = document.getElementById(id_destINP);
 	destinationTXT = document.getElementById(id_destTXT); 	
	
	DT_cont=document.createElement("DIV");
	
	DT_div=document.createElement("DIV");
 	DT_div.className="datetime_choose_cont";
	 
	  
	DT_close = document.createElement("DIV");	
 	DT_close.className="datetime_close"; 
 	
	DT_save = document.createElement("DIV");
 	DT_save.className="datetime_save"; 
 	
 	var browser=navigator.appName;	
	  	
	if((browser == "Netscape")||(browser == "Opera"))
	{
		DT_close.setAttribute('onclick',"datetimePopupCancel(event)");
		DT_save.setAttribute('onclick',"datetimePopupSubmit(event)");
	}
	else
	{
	 	DT_close.attachEvent('onclick',datetimePopupCancel); 
		DT_save.attachEvent('onclick',datetimePopupSubmit); 
	}
	DT_cont.appendChild(DT_close);
	DT_cont.appendChild(DT_save);
	
 	return DT_cont		
}

function datePopupCreate(ts,obj,id_destINP,id_destTXT,cur_lang)
{
 	type=1;
 	
 	var d = new Date(ts*1000); 
 	var now = new Date(); 
 	
	var DT_cont = commonCreate(ts,obj,id_destINP,id_destTXT,cur_lang)

	DT_selDay=d.getDate();
	DT_day = generateDays(d.getDate(),d.getFullYear(),d.getMonth()); 		
	DT_mon = generateMonths(d.getMonth()); 	
	DT_year = generateYears(d.getFullYear(),parseInt(now.getFullYear())+10); 	
	
	DT_div.appendChild(DT_year);
	DT_div.appendChild(DT_mon);
	DT_div.appendChild(DT_day);	
	
	DT_cont.insertBefore(DT_div,DT_cont.firstChild);	
 		
	createObjectSourcedPopupFromObjectOverlapped(DT_cont,obj);
}


function datetimePopupCreate(ts,obj,id_destINP,id_destTXT,cur_lang)
{
 	type = 3;
 	
 	var d = new Date(ts*1000); 
 	var now = new Date(); 
 	
	var DT_cont = commonCreate(ts,obj,id_destINP,id_destTXT,cur_lang)

	DT_time = generateTime(d.getHours(),d.getMinutes(),d.getSeconds());
	DT_selDay=d.getDate();
	DT_day = generateDays(d.getDate(),d.getFullYear(),d.getMonth()); 		
	DT_mon = generateMonths(d.getMonth()); 	
	DT_year = generateYears(d.getFullYear(),now.getFullYear()); 	

	
	DT_div.appendChild(DT_year);
	DT_div.appendChild(DT_mon);
	DT_div.appendChild(DT_day);	
	DT_div.appendChild(DT_time);
	
	DT_cont.insertBefore(DT_div,DT_cont.firstChild);	
 		
	createObjectSourcedPopupFromObjectOverlapped(DT_cont,obj);
}
function datetimePopupCancel(event)
{
	destroyPopup();
}
function datetimePopupSubmit(event)
{
	var ye = parseInt(DT_year.options[DT_year.options.selectedIndex].value);
	var mo = parseInt(DT_mon.options[DT_mon.options.selectedIndex].value);
	var da = DT_selDay;
	if((type == 2)||(type == 3))
	{
		var ho = parseInt(DT_hour.value);
		var mi = parseInt(DT_minute.value);
		var se = parseInt(DT_second.value);
	}
	else
	{
		var ho = 0;
		var mi = 0;
		var se = 0;
	}
	
	
	var newTS = Date.parse((mo + 1 ) + "/" + da + "/" + ye + " " + ho + ":" + mi + ":" + se);	
	destinationINP.value="" + (newTS/1000);
	if((type == 2)||(type == 3))
	{
		destinationTXT.innerHTML = (mo + 1 ) + "/" + da + "/" + ye + " " + ho + ":" + mi + ":" + se;
	}
	else
	{
		destinationTXT.innerHTML = (mo + 1 ) + "/" + da + "/" + ye;
	}
	destroyPopup();
}
function regenerateDays(e)
{
 	var year = parseInt(DT_year.options[DT_year.options.selectedIndex].value);
 	var month = parseInt(DT_mon.options[DT_mon.options.selectedIndex].value);
 	DT_div.removeChild(DT_day);
	DT_day = generateDays(DT_selDay,year,month); 
	DT_div.insertBefore(DT_day,DT_time);
}
function dayselect(event)
{
	var obj = getTargetFromEvent(event);
	var oldobj = document.getElementById("dayselect_" + DT_selDay);

	obj.className="days_entry days_entry_filled_selected";
	obj.oldclassName="days_entry days_entry_filled_selected";
	if(oldobj)
		oldobj.className="days_entry days_entry_filled";
	var i=obj.id.split("_");
	DT_selDay=parseInt(i[1]);
}
function dayfocus(event)
{
	var obj = getTargetFromEvent(event);	
	obj.oldclassName=obj.className;
	obj.className="days_entry days_entry_filled_focus";
}
function dayblur(event)
{
	var obj = getTargetFromEvent(event);
	obj.className=obj.oldclassName;
}

function generateYears(curyear,maxyear)
{
 	var sel = document.createElement("select");
	var op;
	for(i=1970;i<=maxyear;i++)
	{
		op = document.createElement("option");	
		op.value="" + i;
		op.innerHTML = i;
		if(i==curyear)
			op.selected="selected";
		sel.appendChild(op);
	}
	var browser=navigator.appName;	
	if((browser == "Netscape")||(browser == "Opera"))
		sel.setAttribute('onchange',"regenerateDays(event)");
	else
	 	sel.attachEvent('onchange',regenerateDays);
	 	
	return sel;
}
function generateMonths(curmonth)
{	
	var sel = document.createElement("select");
	var op;
	for(i=0;i<12;i++)
	{
		op = document.createElement("option");	
		op.value="" + i;
		op.innerHTML = monthslist[language][i];
		if(i==curmonth)
			op.selected="selected";
		sel.appendChild(op);
	}
	
	var browser=navigator.appName;	
	if((browser == "Netscape")||(browser == "Opera"))
		sel.setAttribute('onchange',"regenerateDays(event)");
	else
	 	sel.attachEvent('onchange',regenerateDays);
	
	return sel;
}
function generateDays(curday,year,month)
{ 
 	var outer = document.createElement("DIV");
 	var inner = document.createElement("DIV");
 	var browser=navigator.appName;	
 	
 	outer.className="days_pos";
 	inner.className="days_cont";
 	
 	outer.appendChild(inner);
	var op;
	for(i=0;i<7;i++)
	{
		op = document.createElement("DIV");
		op.className="days_entry";
		op.innerHTML = dayslist[language][i];
		inner.appendChild(op);
	}
	
	d = new Date(Date.parse((month+1)+"/" + curday + "/"+year)); 	
	var list = getListOfDays(d);
	for(i=0;i<list.length;i++)
	{
	 	op = document.createElement("DIV");
		if(list[i])
		{		 
			if(list[i]["sel"]==true)
			{
				op.className="days_entry days_entry_filled_selected";
			}
			else
			{
				op.className="days_entry days_entry_filled";
			}
			
			op.innerHTML = list[i]["day"];
			op.id = "dayselect_" + list[i]["day"];
			if((browser == "Netscape")||(browser == "Opera"))
			{
				op.setAttribute('onmouseover',"dayfocus(event)");
				op.setAttribute('onmouseout',"dayblur(event)");
				op.setAttribute('onclick',"dayselect(event)");
			}
			else
			{
			 	op.attachEvent('onmouseover',dayfocus);
				op.attachEvent('onmouseout',dayblur);		 	
				op.attachEvent('onclick',dayselect);
			}			
		}
		else
		{			
			op.className="days_entry";			
		}
		inner.appendChild(op);
	}	
	return outer;
}
function generateTime(h,m,s)
{
 	var ret = document.createElement("DIV");
 	ret.className="datetime_time_cont";
	DT_hour = document.createElement("input");
	DT_minute = document.createElement("input");
	DT_second = document.createElement("input");
	var lab1 = document.createElement("span");
	var lab2 = document.createElement("span");
	var lab3 = document.createElement("span");
	
	lab1.innerHTML=lang[language]['h'];
	lab2.innerHTML=lang[language]['m'];
	lab3.innerHTML=lang[language]['s'];		
	
	DT_hour.value=h;
	DT_minute.value=m;
	DT_second.value=s;
	
	ret.appendChild(lab1);
	ret.appendChild(DT_hour);
	ret.appendChild(lab2);
	ret.appendChild(DT_minute);
	ret.appendChild(lab3);	
	ret.appendChild(DT_second);		
	
	return ret;
}