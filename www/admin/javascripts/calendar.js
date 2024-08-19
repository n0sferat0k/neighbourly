var calendar = null;
var appointments_list=null;
var invalids_list=null;
var reserves_list=null;
var new_appointment=null;
var lastSel=null;
var calendarCallback;
var min_date;	
var max_date;

function tmp()
{
 	var cur_d = new Date(Date.parse("1/16/2009"));
	var min_d = new Date(Date.parse("1/1/2005"));	
	var max_d = new Date(Date.parse("1/1/2010"));

 	var appointments = [];
 	appointments[0]=[];
 	appointments[0]['text']="Mara Martic";
	appointments[0]['date'] = new Date(Date.parse("2/16/2009 15:30:00")); 	
	appointments[0]['duration']=120;
	for(i=1;i<15;i++)
	{
	 	appointments[i]=[];
	 	appointments[i]['text']="Pero Peric";
		appointments[i]['date'] = new Date(Date.parse("1/16/2009 " + (5 + i) + ":" + (i*3) + ":00")); 	
		appointments[i]['duration']=40;
	}
	var new_text = "Vanca Vanovici";
	
	var invalids=[];
	invalids[0]=[];
	invalids[0]['text']="";
	invalids[0]['date'] = new Date(Date.parse("1/1/1980 0:00:00")); 	
	invalids[0]['duration']=360;

	invalids[1]=[];
	invalids[1]['text']="";
	invalids[1]['date'] = new Date(Date.parse("1/1/1980 20:00:00")); 	
	invalids[1]['duration']=240;
	
	invalids[2]=[];
	invalids[2]['text']="";
	invalids[2]['date'] = new Date(Date.parse("1/2/1980 0:00:00")); 	
	invalids[2]['duration']=360;

	invalids[3]=[];
	invalids[3]['text']="";
	invalids[3]['date'] = new Date(Date.parse("1/2/1980 20:00:00")); 	
	invalids[3]['duration']=240;
	
	invalids[4]=[];
	invalids[4]['text']="";
	invalids[4]['date'] = new Date(Date.parse("1/3/1980 0:00:00")); 	
	invalids[4]['duration']=360;

	invalids[5]=[];
	invalids[5]['text']="";
	invalids[5]['date'] = new Date(Date.parse("1/3/1980 20:00:00")); 	
	invalids[5]['duration']=240;
	
	invalids[6]=[];
	invalids[6]['text']="";
	invalids[6]['date'] = new Date(Date.parse("1/4/1980 0:00:00")); 	
	invalids[6]['duration']=360;

	invalids[7]=[];
	invalids[7]['text']="";
	invalids[7]['date'] = new Date(Date.parse("1/4/1980 18:00:00")); 	
	invalids[7]['duration']=360;
	
	invalids[8]=[];
	invalids[8]['text']="";
	invalids[8]['date'] = new Date(Date.parse("1/5/1980 0:00:00")); 	
	invalids[8]['duration']=1440;
							
	invalids[9]=[];
	invalids[9]['text']="";
	invalids[9]['date'] = new Date(Date.parse("1/6/1980 0:00:00")); 	
	invalids[9]['duration']=1440;
	
	invalids[10]=[];
	invalids[10]['text']="";
	invalids[10]['date'] = new Date(Date.parse("1/7/1980 0:00:00")); 	
	invalids[10]['duration']=360;

	invalids[11]=[];
	invalids[11]['text']="";
	invalids[11]['date'] = new Date(Date.parse("1/7/1980 20:00:00")); 	
	invalids[11]['duration']=240;
	
	calendarGenerate("EN",cur_d,60,min_d,max_d,appointments,invalids,new_text,tmp2);
}


function tmp2(appointment)
{
	if(appointment != null)
		alert("ok pressed, date=" + appointment['date']);
	else
		alert("cancel pressed");
}

function calendarGenerate(cur_lang,cur_date,defduration,min_d,max_d,appointments,invalids,reserves,new_text,callbackFunction)
{
 	//only allow one calendar at any givven tyme (functions may interfere other wise)
 	if(calendar)
 	{
		calendar.parentNode.removeChild(calendar);
		calendar.innerHTML = "";
		calendar=null;
 	}
	//if new_text is set that means we are in insert mode (not in wiew only mode) must initialize new_appointment so it will appearaa
	if(new_text != false)
	{
		new_appointment = [];
	}
	//store the callback function
	calendarCallback = callbackFunction;
	//store the calendaristic date limits
	min_date = min_d;
	max_date = max_d;
	//store language to be used
	language = cur_lang;	
	//store list of existing appointments and common dayly invalid regions
	appointments_list = appointments;
	invalids_list = invalids;
	reserves_list = reserves;
	
	calendar = document.createElement('DIV');
	calendar.className = "calendar_cont";
	HTML = "";
	HTML+=				'<div class="calendar_frame">' + 
							'<div class="frame_title">' + 
								lang[language]['events'] + 
							'</div>' + 
							'<div class="calendar_applist_cont">' + 
								'<div class="calendar_applist" id="calendar_applist"></div>' + 
							'</div>' + 
						'</div>' + 
						'<div class="calendar_middle">' + 
							'<div class="calendar_middle_lable">' + 
								lang[language]['year'] + 
							'</div>' + 
							'<select class="calendar_dropdown" id="dd_year" onchange="updateCalendar(event)"></select>' + 
							'<div class="calendar_middle_lable">' + 
								lang[language]['mon'] + 
							'</div>' + 
							'<select class="calendar_dropdown" id="dd_mon" onchange="updateCalendar(event)"></select>' + 
							'<div class="calendar_middle_lable">' + 
								lang[language]['mday'] + 
							'</div>' + 
							'<input type="hidden" id="dd_mday" value=""  onchange="updateCalendar(event)"/>' + 
							'<div class="calendar_daylist" id="sel_mday"></div>' + 
							'<div class="calendar_middle_lable">';
							
	if(new_appointment != null)						
		HTML+=					lang[language]['insert'];
		HTML+=				'</div>' + 
							'<div class="insertion_params_cont">';
							
	if(new_appointment != null)
		HTML+=					'<div class="insert_params_entry">' + 
									'<div class="insert_params_entry_lable">' + 
										lang[language]['hours']  + 
									'</div>' + 
									'<input type="text" id="dd_hours" value="' + cur_date.getHours() + '" class="insert_params_entry_input" onchange="updateCalendar()"/>' + 
								'</div>' +
								'<div class="insert_params_entry">' +  								
									'<div class="insert_params_entry_lable">' + 
										lang[language]['minutes']  + 
									'</div>' + 
									'<input type="text" id="dd_minutes" value="' + cur_date.getMinutes() + '" class="insert_params_entry_input" onchange="updateCalendar()" />' + 
								'</div>' + 	
								'<div class="insert_params_entry">' + 														
									'<div class="insert_params_entry_lable">' + 
										lang[language]['duration']  + 
									'</div>' + 
									'<input type="text" id="dd_duration" value="' + defduration + '" class="insert_params_entry_input" onchange="updateCalendar()" />' + 
								'</div>' + 
								'<div class="insert_params_entry">' + 														
									'<div class="insert_params_entry_lable">' + 
										lang[language]['textcontent']  + 
									'</div>' + 
								'</div>' + 
								'<textarea id="dd_text" class="insert_params_entry_text" onchange="updateCalendar()">' + new_text + '</textarea>';
								
	HTML+=					'</div>'; 
							
							
							
	HTML+=				'<div class="buttons_cont">';
	if(new_appointment != null)	
		HTML+=					'<div class="insert_params_button" onclick="ok()">' +
									'<div class="ok_icon"></div>' +
									lang[language]['save']  +
								'</div>';
								
	HTML+=						'<div class="insert_params_button" onclick="cancel()">' +
									'<div class="cancel_icon"></div>' +								
									lang[language]['cancel']  +
								'</div>' +
							'</div>' + 
						'</div>' + 	
						'<div class="calendar_frame">' + 
							'<div class="frame_title">' + 
								lang[language]['timetable'] + 
							'</div>' + 
							'<div class="timetable_content">' + 
								'<div class="timetable_hours" id="timetable_hours_id"></div>' + 
								'<div class="timetable_appointments_cont">' + 
									'<div class="timetable_appointments" id="timetable_appointments_id"></div>' + 
								'</div>' + 
							'</div>' + 
						'</div>';
	calendar.innerHTML=HTML;
	document.body.appendChild(calendar);	
		
	fillApplist();	
	fillYear(cur_date);
	fillMonth(cur_date);
	fillDay(cur_date);
	if(new_appointment != null)
		updateNewInsertion();
	fillTimeTabel();
	fillAppointments(cur_date,-1);	
	return calendar;
}
//check is new appointment overlaps with invalid zones
function checkValidityOfNewAppointment()
{
	var cont;
 	var min1,min2;
 	var min_s = new_appointment['date'].getHours()*60 + new_appointment['date'].getMinutes();	
 	var min_e = parseInt(min_s) + parseInt(new_appointment['duration']);
 	
 	for(i=0;i<invalids_list.length;i++)
 	{
		if(invalids_list[i]['date'].getDay() == new_appointment['date'].getDay())
		{
 	 		min1 = invalids_list[i]['date'].getHours()*60 + invalids_list[i]['date'].getMinutes();	
			min2 = parseInt(min1) + parseInt(invalids_list[i]['duration']);
						
			if((min1<min_s)&&(min_s<min2))
			{			 	
				cont = confirm(lang[language]['overlap_invalid']);
				return cont;		
			}
			if((min1<min_e)&&(min_e<min2))
			{
				cont = confirm(lang[language]['overlap_invalid']);				
				return cont;
			}							
			if((min_s<=min1)&&(min2<=min_e))
			{				
				cont = confirm(lang[language]['overlap_invalid']);
				return cont;	
			}
		}
	}
	return true;
}

//check if new appointment overlaps with old appointmets  
function checkOverlappingOfNewAppointment()
{ 
 	var cont = true;
 	var min1,min2;
 	var min_s = new_appointment['date'].getHours()*60 + new_appointment['date'].getMinutes();	
 	var min_e = min_s + parseInt(new_appointment['duration']);
 	
 	for(i=0;i<appointments_list.length;i++)
 	{
 	 	if((appointments_list[i]['date'].getYear() == new_appointment['date'].getYear())&&(appointments_list[i]['date'].getMonth() == new_appointment['date'].getMonth())&&(appointments_list[i]['date'].getDate() == new_appointment['date'].getDate()))
 	 	{
			min1 = appointments_list[i]['date'].getHours()*60 + appointments_list[i]['date'].getMinutes();	
			min2 = parseInt(min1) + parseInt(appointments_list[i]['duration']);
			
			if((min1<min_s)&&(min_s<min2))
			{
				cont = confirm(lang[language]['overlap_appointment']);	
				return cont;	
			}			
			if((min1<min_e)&&(min_e<min2))
			{
				cont = confirm(lang[language]['overlap_appointment']);				
				return cont;
			}	
			if((min_s<=min1)&&(min2<=min_e))
			{
				cont = confirm(lang[language]['overlap_appointment']);				
				return cont;	
			}
		}
	}
	return true;
}
//check if new appointment falls to invalid date
function checkReservesAtNewAppointment()
{
 	var date1; 
 	var date2 = new_appointment['date'];
	for(j=0;j<reserves_list.length;j++)
	{ 	 	
	 	date1 = reserves_list[j]['date'];
	 	if((date1.getMonth() == date2.getMonth()) && (date1.getYear() == date2.getYear())&& (date1.getDate() == date2.Date()))
	 	{	 	 	
	 	 	cont = confirm(lang[language]['reserved_appointment']);				
			return cont;
		}
	}
	return true;
}
function ok()
{
  	if(checkValidityOfNewAppointment()&&checkOverlappingOfNewAppointment()&&checkReservesAtNewAppointment())
 	{
	 	calendar.parentNode.removeChild(calendar);
		calendar.innerHTML = "";
		calendar=null;
		calendarCallback(new_appointment);
	}
}
function cancel()
{
 	calendar.parentNode.removeChild(calendar);
	calendar.innerHTML = "";
	calendar=null;
	calendarCallback();	
}
function updateCalendar()
{	
	var dd_year = document.getElementById('dd_year');
	var dd_mon = document.getElementById('dd_mon');
	var dd_mday = document.getElementById("dd_mday");

	var year = dd_year.options[dd_year.options.selectedIndex].value;
	var mon = dd_mon.options[dd_mon.options.selectedIndex].value;
	var day = dd_mday.value;

	var str =  mon + '/' + day + '/' + year;
	var date = new Date(Date.parse(str)); 

	if(date.getDate() != day)	//if february the 30'th error
	{
		day=1;	
		var str =  mon + '/' + day + '/' + year;
		var date = new Date(Date.parse(str)); 
	}			
	forceDate(date);
}
function updateNewInsertion()
{
	var dd_year = document.getElementById('dd_year');
	var dd_mon = document.getElementById('dd_mon');
	var dd_mday = document.getElementById("dd_mday");
	var dd_hours = document.getElementById("dd_hours");
	var dd_minutes = document.getElementById("dd_minutes");
	var dd_duration = document.getElementById("dd_duration");
	var dd_text = document.getElementById("dd_text");

	var year = dd_year.options[dd_year.options.selectedIndex].value;
	var mon = dd_mon.options[dd_mon.options.selectedIndex].value;
	var day = dd_mday.value;
	var hours = dd_hours.value;
	var minutes = dd_minutes.value;	
	
	var str =  mon + '/' + day + '/' + year + ' ' + hours + ":" + minutes;
	var date = new Date(Date.parse(str));
	
	new_appointment['date'] = date;
	new_appointment['text'] = dd_text.value;
	new_appointment['duration'] = dd_duration.value;
}
function forceDate(date)
{
 	var dd_year = document.getElementById('dd_year');
	var dd_mon = document.getElementById('dd_mon');
	var dd_mday = document.getElementById("dd_mday");

	var year = dd_year.options[dd_year.options.selectedIndex].value;
	var mon = dd_mon.options[dd_mon.options.selectedIndex].value;
	var day = dd_mday.value;
	
	if(year != date.getFullYear())
	{
	 	fillYear(date);
		fillMonth(date);
		fillDay(date);		
	}
	else if(mon != (date.getMonth()+1))
	{
		fillMonth(date);
		fillDay(date);	
	}
	else
	{
		fillDay(date);	
	}
	if(new_appointment != null)
		updateNewInsertion();
	fillAppointments(date,-1);	
	fillApplist();	
}
function selectApp(obj,sel)
{
 	lastSel = sel;
	forceDate(appointments_list[sel]['date']);
	fillAppointments(appointments_list[sel]['date'],sel);
}
function fillApplist()
{
 	var calendar_applist = document.getElementById("calendar_applist");
	var date_tmp;
	var str;
	calendar_applist.innerHTML="";
	for(i=0;i<appointments_list.length;i++)
	{
	 	date_tmp = appointments_list[i]['date'];
	 	str="";
	 	str+= monthslist[language][date_tmp.getMonth()] + "/" + date_tmp.getDate() + "/" + date_tmp.getFullYear();
		str+= " " + date_tmp.getHours() + ":" + date_tmp.getMinutes() + " (" + appointments_list[i]['duration'] + " min)";
		if(i==lastSel)
		{
			calendar_applist.innerHTML +='<div class="calendar_applist_entry_selected" onclick="selectApp(this,' + i + ')"><div class="calendar_applist_entry_tag">' + (i+1) + '</div><strong>' + appointments_list[i]['text'] + '</strong><br />' + str + '</div>';					 	
			lastSel=null;
		}
		else
		{
			calendar_applist.innerHTML +='<div class="calendar_applist_entry" onclick="selectApp(this,' + i + ')"><div class="calendar_applist_entry_tag">' + (i+1) + '</div><strong>' + appointments_list[i]['text'] + '</strong><br />' + str + '</div>';				
		}
	}
}
function fillYear(date)
{
	var dd_year = document.getElementById('dd_year');
	var op;
	dd_year.innerHTML = "";
 	for(i=min_date.getFullYear();i<=max_date.getFullYear();i++)
	{
	 	op =document.createElement('option');
		op.value=i;
	 	if(date.getFullYear() == i)
		 	op.selected="selected";
		op.innerHTML = i;
		dd_year.appendChild(op);
	}	
}
function fillMonth(date)
{
 	var dd_mon = document.getElementById('dd_mon');
 	var month = date.getMonth() + 1;
	var op; 	
 	dd_mon.innerHTML = "";
	for(i=1;i<=12;i++)
	{
	 	op =document.createElement('option');
		op.value=i;
	 	if(month == i)
			op.selected="selected";
		op.innerHTML = monthslist[language][i-1];
		dd_mon.appendChild(op);
	}	
}
function fillDay(date)
{
 	var tmp_date;
	var dd_mday = document.getElementById("dd_mday");
 	var sel_mday = document.getElementById("sel_mday");
 	dd_mday.value=date.getDate();
	var browser=navigator.appName;	
	 	
	sel_mday.innerHTML="";
	for(i=0;i<7;i++)
	{		
		sel_mday.innerHTML += '<div class="calendar_day_entry">' + dayslist[language][i] + '</div>';		
	} 
	var list = getListOfDays(date);
	for(j=0;j<reserves_list.length;j++)
	{ 	 	
	 	tmp_date = reserves_list[j]['date'];
	 	if((tmp_date.getMonth() == date.getMonth()) && (tmp_date.getYear() == date.getYear()))
	 	{	 	 	
	 	 	d = tmp_date.getDate();
			for(i=0;i<list.length;i++)
			{ 	 	
				if(list[i]["day"] == d)
				{				 
					list[i]["res"]=true;
				}
			}
		}
	}
	for(i=0;i<list.length;i++)
	{	 	
		if(list[i])
		{	
 	   		if(list[i]["res"]==true)
 	   		{
				if(list[i]["sel"]==true)
				{
					sel_mday.innerHTML += '<div class="calendar_day_entry calendar_day_entry_ressel">' + list[i]["day"] + '</div>';
				}
				else
				{
	 				sel_mday.innerHTML += '<div class="calendar_day_entry calendar_day_entry_resunsel" onclick="document.getElementById(\'dd_mday\').value=\'' + list[i]["day"] + '\';updateCalendar(event);">' + list[i]["day"] + '</div>';
				}		
			}
			else
			{
				if(list[i]["sel"]==true)
				{
					sel_mday.innerHTML += '<div class="calendar_day_entry calendar_day_entry_sel">' + list[i]["day"] + '</div>';
				}
				else
				{
	 				sel_mday.innerHTML += '<div class="calendar_day_entry calendar_day_entry_unsel" onclick="document.getElementById(\'dd_mday\').value=\'' + list[i]["day"] + '\';updateCalendar(event);">' + list[i]["day"] + '</div>';
				}
			}
		}
		else
		{
			sel_mday.innerHTML += '<div class="calendar_day_entry"></div>';		
		}
	}
}
function fillTimeTabel()
{
 	var timetable_hours = document.getElementById("timetable_hours_id");
 	timetable_hours.innerHTML="";
	for(i=0;i<24;i++)
 	{
	 	timetable_hours.innerHTML+= '<div class="timetable_hours_entry">' + i + "-" + (i+1) + '</div>';
	}
}
function fillAppointments(date,sel)
{
 	clearAppointments();
 	var date_tmp;
	var res = []; 	
 	for(i=0;i<reserves_list.length;i++)
	{ 	 	
	 	tmp_date = reserves_list[i]['date'];
	 	if((tmp_date.getMonth() == date.getMonth()) && (tmp_date.getYear() == date.getYear())&& (tmp_date.getDate() == date.getDate()))
	 	{	 	 	
			res['date'] = tmp_date;
			res['text'] = reserves_list[i]['text'];
			res['duration'] = 1440;			
	 	 	addAppointmentToTimeTabel(-1,res,"invalid");	
	 	 	return;
		}
	}
 	
 	//make invalid periods appear 
	for(i=0;i<invalids_list.length;i++)
 	{
 	 	if(invalids_list[i]['date'].getDay() == date.getDay())
 	 	{
 	 		addAppointmentToTimeTabel(-1,invalids_list[i],"invalid");					
 	 	}
	}
 	//make existing appointments appear if their date is the day viewed
	for(i=0;i<appointments_list.length;i++)
 	{
 	 	date_tmp = appointments_list[i]['date'];
 	 	if((date_tmp.getYear() == date.getYear())&&(date_tmp.getMonth() == date.getMonth())&&(date_tmp.getDate() == date.getDate()))
		{
		 	if(sel == i)
			 	addAppointmentToTimeTabel(i,appointments_list[i],"selected");		
			else
				addAppointmentToTimeTabel(i,appointments_list[i],false);
			
		}
	}
	//make new appointment appear if its date is the day viewed
	if(new_appointment != null)
	{
		date_tmp = new_appointment['date'];
	 	if((date_tmp.getYear() == date.getYear())&&(date_tmp.getMonth() == date.getMonth())&&(date_tmp.getDate() == date.getDate()))
		{	 	
		 	addAppointmentToTimeTabel(-1,new_appointment,"marked");		
		}
	}
}
function clearAppointments()
{
	var timetable_appointments = document.getElementById("timetable_appointments_id"); 
	timetable_appointments.innerHTML = "";
}
function addAppointmentToTimeTabel(pos,appointment,type)
{
 	var timetable_appointments = document.getElementById("timetable_appointments_id"); 		
 	var point = Math.floor(15 * ((appointment['date'].getHours() * 60) + appointment['date'].getMinutes()) /60);
	var length = Math.floor(15 * appointment['duration'] / 60); 	
	
 	switch(type)
 	{
		case "selected":
							timetable_appointments.innerHTML+= '<div style="top:' + point + 'px;height:' + length + 'px;" class="timetable_appointments_entry_selected"><div class="timetable_appointments_entry_tag">' + (pos+1) + '</div>' + appointment['text'] + '</div>';
							break;
		case "marked":
							timetable_appointments.innerHTML+= '<div style="top:' + point + 'px;height:' + length + 'px;" class="timetable_appointments_entry_marked"><div class="timetable_appointments_entry_tag">' + (pos+1) + '</div>' + appointment['text'] + '</div>';
							break;
		case "invalid":
							timetable_appointments.innerHTML+= '<div style="top:' + point + 'px;height:' + length + 'px;" class="timetable_appointments_entry_invalid">' + appointment['text'] + '</div>';
							break;							
		default:
							timetable_appointments.innerHTML+= '<div style="top:' + point + 'px;height:' + length + 'px;" class="timetable_appointments_entry"><div class="timetable_appointments_entry_tag">' + (pos+1) + '</div>' + appointment['text'] + '</div>';
	}	 	
}