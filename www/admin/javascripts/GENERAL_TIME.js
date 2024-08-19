

var monthslist = ['RO','EN'];
monthslist['RO'] = ['Ianuarie','Februarie','Martie','Aprilie','Mai','Iunie','Iulie','August','Septembrie','Octombrie','Noiembrie','Decembrie'];
monthslist['EN'] = ['January','February','March','April','Mai','June','July','August','September','October','November','December'];
 
var dayslist = ['RO','EN'];
dayslist['RO'] = ['D','L','M','M','J','V','S'];
dayslist['EN'] = ['S','M','T','W','T','F','S'];

var language = "EN";

var lang = [];
lang["EN"] =[];
 	lang["EN"]['events'] = "Events and appointments";
 	lang["EN"]['timetable'] = "Time table";
	lang["EN"]['year'] = "Year";
	lang["EN"]['mon'] = "Month";
	lang["EN"]['mday'] = "Day of the month";
	lang["EN"]['insert'] = "Insert new appointment";
	lang["EN"]['hours'] = "Hr:";
	lang["EN"]['minutes'] = "Min:";
	lang["EN"]['duration'] = "Duration(min):";
	lang["EN"]['textcontent'] = "Text content:";
	lang["EN"]['save'] = "Insert";
	lang["EN"]['cancel'] = "Cancel";	
	lang["EN"]['h'] = "Hr:";
	lang["EN"]['m'] = "Min:";
	lang["EN"]['s'] = "Sec:";
	lang["EN"]['overlap_invalid'] = "The appointment you are inserting overlaps with an invalid zone. Do you wish to continue ?";
	lang["EN"]['overlap_appointment'] = "The appointment you are inserting overlaps with an existing appointment. Do you wish to continue ?";
	lang["EN"]['reserved_appointment'] = "The appointment you are inserting falls to an invalid date. Do you wish to continue ?";	
	

lang["RO"] =[];
 	lang["RO"]['events'] = "Events and appointments";
 	lang["RO"]['timetable'] = "Time table";
	lang["RO"]['year'] = "Year";
	lang["RO"]['mon'] = "Month";
	lang["RO"]['mday'] = "Day of the month";
	lang["RO"]['insert'] = "Insert new appointment";
	lang["RO"]['hours'] = "Hr:";
	lang["RO"]['minutes'] = "Min:";
	lang["RO"]['duration'] = "Duration(min):";
	lang["RO"]['textcontent'] = "Text content:";
	lang["RO"]['save'] = "Insert";
	lang["RO"]['cancel'] = "Cancel";	
	lang["RO"]['h'] = "Ora:";
	lang["RO"]['m'] = "Min:";
	lang["RO"]['s'] = "Sec:"; 
	lang["RO"]['overlap_invalid'] = "Programarea pe care vreti sa o introduceti se suprapune peste o zona invalida. Doriti sa continuati ?";
	lang["RO"]['overlap_appointment'] = "Programarea pe care vreti sa o introduceti se suprapune peste o programare existenta. Doriti sa continuati ?";
	lang["RO"]['reserved_appointment'] = "Programarea pe care vreti sa o introduceti pica pe o data invalida.Doriti sa continuati ?";		
//get days of month in weekday order (if the 1'st is tuesday returns 2 empty eleemnts as placeholders for Sunday and Monday from last month)
function getListOfDays(date)
{ 	
 	var d;
	var m;
	var ret = [];
 	var cnt=0;
	var tmpdate = new Date(Date.parse((date.getMonth()+1)+"/1/"+date.getFullYear()));
	var the1stIsDayOfWeek = tmpdate.getDay();

	
	var year = date.getFullYear();
	var month = date.getMonth();
	var day = date.getDate();
	
	//PLACE EMPTY ENTRYS IF NECESSARY
	for(i=0;i<the1stIsDayOfWeek;i++)
	{
 		ret[cnt]=null;
 		cnt++;
	}
	//PLACE ENTRYS FOR DAYS OF MONTH
	m=month;
	d=1;
	do
	{		
	 	ret[cnt]=[];
	 	ret[cnt]["day"]=d;
	 	if((d==day))
		 	ret[cnt]["sel"]=true;		 	
		else
		 	ret[cnt]["sel"]=false;
 		cnt++;
		 		
		d++;
		tmpdate = new Date(Date.parse((parseInt(month)+1)+"/"+parseInt(d)+"/"+parseInt(year))); 	
		m = tmpdate.getMonth();
	}	
	while(m==month)		
	return ret;
}