<?php 
session_start();
define('IN_ADMIN',1);
/**///*****************************************ESSENTIAL PHP COMPONENTS, DO NOT DELETE 
/**/	include_once("ADMIN_INCLUDE/connect_db.inc.php");
/**/	include_once("ADMIN_INCLUDE/Template.php");
/**/	include_once("ADMIN_INCLUDE/INITINSTANCE.php");
/**/	include_once("ADMIN_LANGUAGE/languagesTexts.php");
/**/	include_once("ADMIN_INCLUDE/AUX_FUNCTIONS.php");
//************************************************************************************

/**///**************************************ADMIN COMPONENT PHP CLASSES, DO NOT DELETE
/**/	include_once("ADMIN_INCLUDE/combinedObjectTTDI.php");
/**/	include_once("ADMIN_INCLUDE/customPageInclude.php");
/**/	include_once("ADMIN_INCLUDE/treeObject.php");
/**/	include_once("ADMIN_INCLUDE/menuWrapper.php");
//************************************************************************************


/**///*****************CLASELE GENERALE DE MANAGEMENT PAGINA (IN FISIERUL INITINSTANCE) 
/**/$classManagger = new classManagger();
/**/$loginManager = new loginManager();
//************************************************************************************


/**///******************VARIABILA PRINCIPALA CARE IMI ZICE DACA ADMINUL E LOGAT SAU NU 
/**/$LOGGED_IN = $loginManager->doLoginCheck();
//************************************************************************************



/**///**************************************************OBTINEREA VARIABILEI DE PAGINA 
/**/if(isset($_REQUEST['page']))
/**/	$KEY = $_REQUEST['page'];
/**/else
/**/	$KEY = "intro";
//************************************************************************************




/**///________________________________________________________________________________
/**///________________________________________________________________________________
/**///________________________________________________________________________________
/**///%%%%%%%%%%%%%%%%INSTANTIATE ADMIN COMPONENT PAGE CLASSES HERE %%%%%%%%%%%%%%%%%%%

//$O1 = new combinedObjectTTDI();
//$O2 = new customPageInclude();
//$O3 = new treeObject();
//$O4 = new menuWrapper();

/*********************************************************************************INTRO*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="intro";
$p['MENULABLE']="Intro";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=true;
$p['MULTIPIC']=true;
$p['DATE']=false;
$p['TEXT']=true;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=false;

$p['TEXT_modif']=true;
$p['TITLE_modif']=true;

$p['SINGULAR']=true;
$p['DELETABLE']=false;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$intro = new combinedObjectTTDI();
$intro->setParams($p);
$classManagger->add($intro);


/*********************************************************************************USERS*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="users";
$p['MENULABLE']="Users";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=true;
$p['PIC_del']=true;
$p['PICHASNAME']=false;
$p['MULTIPIC']=false;
$p['ORDER']="titlu_EN";
$p['ORDERDIR']="ASC";

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['DATE']=true;
$p['DATE_modif']=true;

$p['TEXT']=true;
$p['TEXT_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_STRINGS'] = array(		
	array("EN" => "Username"),	
	array("EN" => "Passhash"),
	array("EN" => "Phone"),
	array("EN" => "Email"),
);
$p['ADD_STRINGS_modif'] = array(
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true),	
);

$p['ADD_NUMERICS'] = array(
	array("EN" => "Houoehold"),	
);
$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),	
);

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/users_single.php";

$users = new combinedObjectTTDI();
$users->setParams($p);
$classManagger->add($users);

/*********************************************************************************HOUSEHOLDS*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="households";
$p['MENULABLE']="Households";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=true;
$p['PIC_del']=true;
$p['PICHASNAME']=false;
$p['MULTIPIC']=false;
$p['ORDER']="titlu_EN";
$p['ORDERDIR']="ASC";

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['TEXT']=true;
$p['TEXT_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_STRINGS'] = array(		
	array("EN" => "Address"),	
);
$p['ADD_STRINGS_modif'] = array(
	array(0 => true,1 => true),	
);

$p['ADD_NUMERICS'] = array(
	array("EN" => "Head ID"),
	array("EN" => "Latitude"),
	array("EN" => "Longitude"),
);
$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true)
);

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/single_household.php";

$users = new combinedObjectTTDI();
$users->setParams($p);
$classManagger->add($users);

/*********************************************************************************NEIGHBOURHOODS*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="neighbourhoods";
$p['MENULABLE']="Neighbourhoods";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=false;
$p['PICHASNAME']=false;
$p['MULTIPIC']=false;

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['TEXT']=true;
$p['TEXT_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/single_neighbourhood.php";

$users = new combinedObjectTTDI();
$users->setParams($p);
$classManagger->add($users);

/*********************************************************************************TOKENS*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="tokens";
$p['MENULABLE']="Tokens";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=false;
$p['PIC_del']=false;
$p['PICHASNAME']=false;
$p['MULTIPIC']=false;
$p['ORDER']="titlu_EN";
$p['ORDERDIR']="ASC";

$p['DATE']=true;
$p['DATE_modif']=true;

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['TEXT']=false;
$p['TEXT_modif']=false;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['ADD_NUMERICS'] = array(
	array("EN" => "User ID"),	
);
$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),	
);

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/single.php";

$users = new combinedObjectTTDI();
$users->setParams($p);
$classManagger->add($users);


/*********************************************************************************COORDINATES*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="coordinates";
$p['MENULABLE']="Coordinates";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=false;
$p['PIC_del']=false;
$p['PICHASNAME']=false;
$p['MULTIPIC']=false;
$p['SORTINDEX']=false;
$p['ORDER']="add_numerics_0";
$p['ORDERDIR']="ASC";

$p['TITLE']=false;
$p['TITLE_modif']=false;

$p['TEXT']=false;
$p['TEXT_modif']=false;

$p['DATE']=true;
$p['DATE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_NUMERICS'] = array(
	array("EN" => "User ID"),
	array("EN" => "Latitude"),
	array("EN" => "Longitude"),
);
$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true)
);

$p['LIST_template']="TEMPLATES/coord_table_list.php";
$p['SINGLE_template']="TEMPLATES/single.php";

$coords = new combinedObjectTTDI();
$coords->setParams($p);
$classManagger->add($coords);

/*********************************************************************************CATEGORIES*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="categories";
$p['MENULABLE']="Item Categories";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=false;
$p['DATE']=false;
$p['TEXT']=false;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=false;
$p['SORTINDEX']=false;

$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_STRINGS'] = array(		
	array("EN" => "Enum Const"),	
);
$p['ADD_STRINGS_modif'] = array(
	array(0 => true,1 => true),	
);

$p['LIST_template']="TEMPLATES/table_list.php";

$categ_manc = new combinedObjectTTDI();
$categ_manc->setParams($p);
$classManagger->add($categ_manc);

/*********************************************************************************ITEMS*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="items";
$p['MENULABLE']="Items";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=true;
$p['MULTIPIC']=true;
$p['FILE']=true;
$p['MULTIFILE']=true;

$p['DATE']=true;
$p['TEXT']=true;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=false;
$p['SORTINDEX']=false;

$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_STRINGS'] = array(		
	array("EN" => "Enum Const"),	
);

$p['ADD_STRINGS_modif'] = array(
	array(0 => true,1 => true),	
);

$p['ADD_NUMERICS'] = array(
	array("EN" => "Neighbourhood"),
	array("EN" => "Household"),
	array("EN" => "User"),
	array("EN" => "Category"),
);
$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true)
);

$p['LIST_template']="TEMPLATES/table_list.php";

$categ_manc = new combinedObjectTTDI();
$categ_manc->setParams($p);
$classManagger->add($categ_manc);


/*********************************************************************************ASSOC USER NEIGHBOURHOOD*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="neighbourhood_household_users";
$p['MENULABLE']="N. H. Users";
$p['DISPLAYMENUTAB']=true;

$p['TEXT']=false;
$p['TITLE']=false;

$p['LANGUAGES']=array("EN");
$p['CURRENTLANGUAGE']=0;

$p['ADD_NUMERICS'] = array(
	array("EN" => "Neighbourhood"),
	array("EN" => "Household"),
	array("EN" => "User"),
	array("EN" => "Access"),	
	array("EN" => "Parent"),
);

$p['ADD_NUMERICS_modif'] = array(
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true),
	array(0 => true,1 => true)
);

$p['LIST_template']="TEMPLATES/table_list.php";

$categ_manc = new combinedObjectTTDI();
$categ_manc->setParams($p);
$classManagger->add($categ_manc);

/**///________________________________________________________________________________
/**///________________________________________________________________________________
/**///________________________________________________________________________________


?>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>SITE ADMIN TITLE</title>
		<!--**************************************************************************************** ESENTIAL JAVA SCRIPTS, DO NOT REMOVE-->
		<!----> <script src="javascripts/lib/prototype.js" type="text/javascript"></script>
		<!----> <script src="javascripts/src/scriptaculous.js" type="text/javascript"></script>
		<!----> <script src="javascripts/src/unittest.js" type="text/javascript"></script>
		<!----> <script language="javascript" type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js"></script>	
		<!--*****************************************************************************************************************************-->
		
		<link rel="stylesheet" type="text/css" href="CSS/SIMPLE_STRUCT.css" />
	</head>
	<body>
		<div class="MASTER">			
			<!--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ THE HEADER WITH LOGO MENUES AND LOGOUT -->			
			<div class="HEADER">
				<div class="DIV_HEADER_CONT">
					<div class="DIV_LOGOUT">
						<?php 
							if($LOGGED_IN)
							{	
							 	$classManagger->displayLanguages($KEY);                                                                
								$LANG = $_SESSION['language'];
								 	
							 	?>
							 		<div class="DIV_sessionbar">
								 		<form action="" method="post">
								 			<input type="hidden" name="param1" value="dologout" />
											<div onclick="this.parentNode.submit()" class="A_LOGOUT"><?php echo $TEXTS[$LANG]['exit'];?></div>	
										</form>
										<?php  
											if($_SESSION['admin_level']==0)
											{
											 	?>
													<form action="" method="post">
											 			<input type="hidden" name="param1" value="listusers" />
														<div onclick="this.parentNode.submit()" class="A_ADDUSER"><?php echo $TEXTS[$LANG]['users'];?>&nbsp;/&nbsp;</div>	
													</form>
												<?php 
											}
										?>	
									</div>																					
								<?php 								
							}							
							else
								echo "&nbsp;";
						?>
					</div>
				</div>
				<div class="DIV_MENU">
					<?php 
						if($LOGGED_IN)
							$classManagger->displayMenues($KEY);
						else
							echo "&nbsp;";
					?>
				</div>
			</div>	
			<div class="BORDER_TOP">
				<div class="BORDER_TOP_LEFT">					
				</div>
				<div class="BORDER_TOP_RIGHT">						
				</div>					
			</div>
			<div class="NAVIGATOR_OUT">
				<div class="NAVIGATOR_INN">
					<div class="NAVIGATOR">
						<?php 
							if($LOGGED_IN)								
								$classManagger->displayNavigations($KEY);
						?>
					</div>
				</div>
			</div>				
			<!--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ THE BODY WITH LOGIN OR EDITOR GUIS -->
			<div class="BODY_OUT">
				<div class="BODY_INN">
					<div class="BODY">																				 															<!--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ THE CONTENT BEGIN -->
							<?php																			
								if($LOGGED_IN)
								{
									$classManagger->displayBodys($KEY);	
								}
								else
								{
								 	if((isset($_SESSION['admin_op']))&&($_SESSION['admin_op']=='listusers'))
									 	$loginManager->showUserListForm();	
									else
										$loginManager->showLoginForm();	
								}							
							?>
						<!--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ THE CONTENT END -->	
					</div>
				</div>	
			</div>								
			<div class="BORDER_BOTTOM">
				<div class="BORDER_BOTTOM_LEFT">					
				</div>
				<div class="BORDER_BOTTOM_RIGHT">						
				</div>					
			</div>
			<div class="TR_FOOTER">															
				<div class="DIV_FOOTER">
					<div class="DIV_COPYRIGHT">Copyright &copy; S.C. Softvision S.R.L. 2011</div>
				</div>							
			</div>
		</div>				
	</body>
</html>
<?php 
	mysqli_close($connection);
?>