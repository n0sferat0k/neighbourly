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
/**/include_once ("ADMIN_INCLUDE/combinedObjectTTDI.php");
/**/include_once ("ADMIN_INCLUDE/customPageInclude.php");
/**/include_once ("ADMIN_INCLUDE/treeObject.php");
/**/include_once ("ADMIN_INCLUDE/menuWrapper.php");
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
$p['MENULABLE']="Introducere";
$p['DISPLAYMENUTAB']=true;

$p['PIC']=false;
$p['DATE']=false;
$p['TEXT']=true;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=false;
$p['MULTIPIC']=false;

$p['TEXT_modif']=true;
$p['TITLE_modif']=true;

$p['SINGULAR']=true;
$p['DELETABLE']=false;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$intro = new combinedObjectTTDI();
$intro->setParams($p);
$classManagger->add($intro);




/*********************************************************************************GALERIE 1*/


unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="galerie_1";
$p['MENULABLE']="Galerie Sediu 1";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['DATE']=true;
$p['TEXT']=false;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;
$p['MULTIPIC']=false;


$p['TEXT_modif']=true;
$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$galerie_1 = new combinedObjectTTDI();
$galerie_1->setParams($p);
$classManagger->add($galerie_1);

/*********************************************************************************GALERIE 2*/


unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="galerie_2";
$p['MENULABLE']="Galerie Sediu 2";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['DATE']=true;
$p['TEXT']=false;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;
$p['MULTIPIC']=false;


$p['TEXT_modif']=true;
$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$galerie_2 = new combinedObjectTTDI();
$galerie_2->setParams($p);
$classManagger->add($galerie_2);



/*********************************************************************************GALERII*/
unset($p);
$p = $menuWrapper_defparams;
$p['KEY']="galerii";
$p['MENULABLE']="Galerii";
$p['DISPLAYMENUTAB']=true;
$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$galerii = new menuWrapper();
$galerii->setParams($p);
$galerii->add($galerie_1);
$galerii->add($galerie_2);

$classManagger->add($galerii);


/*********************************************************************************EVENIMENT 1*/


unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="eveniment_1";
$p['MENULABLE']="Evenimente Sediu 1";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['MULTIPIC']=true;
$p['PICHASNAME']=true;


$p['DATE']=true;
$p['TEXT']=true;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;

$p['TEXT_modif']=true;
$p['TITLE_modif']=true;
$p['DATE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$eveniment_1 = new combinedObjectTTDI();
$eveniment_1->setParams($p);
$classManagger->add($eveniment_1);


/*********************************************************************************EVENIMENT 2*/


unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="eveniment_2";
$p['MENULABLE']="Evenimente Sediu 2";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['MULTIPIC']=true;
$p['PICHASNAME']=true;

$p['DATE']=true;
$p['TEXT']=true;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;

$p['TEXT_modif']=true;
$p['TITLE_modif']=true;
$p['DATE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$eveniment_2 = new combinedObjectTTDI();
$eveniment_2->setParams($p);
$classManagger->add($eveniment_2);


/*********************************************************************************EVENIMENTE*/
unset($p);
$p = $menuWrapper_defparams;
$p['KEY']="evenimente";
$p['MENULABLE']="Evenimente";
$p['DISPLAYMENUTAB']=true;
$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$evenimente = new menuWrapper();
$evenimente->setParams($p);
$evenimente->add($eveniment_1);
$evenimente->add($eveniment_2);

$classManagger->add($evenimente);

/*********************************************************************************CATEGORII MANCARURURI*/


unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="categ_manc";
$p['MENULABLE']="Categorii mancaruri";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=false;
$p['DATE']=false;
$p['TEXT']=false;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;

$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";

$categ_manc = new combinedObjectTTDI();
$categ_manc->setParams($p);
$classManagger->add($categ_manc);

/*********************************************************************************CATEGORII BAUTURI*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="categ_baut";
$p['MENULABLE']="Categorii bauturi";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=false;
$p['DATE']=false;
$p['TEXT']=false;
$p['TITLE']=true;
$p['ACCENT']=false;
$p['ZINDEX']=false;
$p['MOVEABLE']=true;

$p['TITLE_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";

$categ_baut = new combinedObjectTTDI();
$categ_baut->setParams($p);
$classManagger->add($categ_baut);



/*********************************************************************************MANCARURI*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="mancaruri";
$p['MENULABLE']="Mancaruri";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['PICHASNAME']=true;
$p['MULTIPIC']=true;

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['TEXT']=true;
$p['TEXT_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/mancare.php";

$p['ADD_STRINGS'] =        array   (
                                        array("RO" => "Pret promotional eveniment 1","EN" => "Special price with event 1"),
                                        array("RO" => "Pret promotional eveniment 2","EN" => "Special price with event 2"),
                                        array("RO" => "Pret","EN" => "Price")
                                    );
$p['ADD_STRINGS_modif'] =  array   (
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true)
                                    );

$p['ADD_NUMERICS'] =        array   (
                                        array("RO" => "Categorie","EN" => "Category"),
                                        array("RO" => "Eveniment sediu 1","EN" => "Event in location 1"),
                                        array("RO" => "Eveniment sediu 2","EN" => "Event in location 2")
                                    );
$p['ADD_NUMERICS_modif'] =  array   (
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true)
                                    );

$mancaruri = new combinedObjectTTDI();
$mancaruri->setParams($p);
$classManagger->add($mancaruri);


/*********************************************************************************BAUTURI*/

unset($p);
$p = $combinedObcejtTTDI_defparams;
$p['KEY']="bauturi";
$p['MENULABLE']="Bauturi";
$p['DISPLAYMENUTAB']=false;

$p['PIC']=true;
$p['PIC_del']=true;
$p['PICHASNAME']=true;
$p['MULTIPIC']=true;

$p['TITLE']=true;
$p['TITLE_modif']=true;

$p['TEXT']=true;
$p['TEXT_modif']=true;

$p['SINGULAR']=false;
$p['DELETABLE']=true;
$p['APPENDABLE']=true;

$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$p['LIST_template']="TEMPLATES/table_list.php";
$p['SINGLE_template']="TEMPLATES/bautura.php";


$p['ADD_STRINGS'] =        array   (
                                        array("RO" => "Pret promotional eveniment 1","EN" => "Special price with event 1"),
                                        array("RO" => "Pret promotional eveniment 2","EN" => "Special price with event 2"),
                                        array("RO" => "Pret","EN" => "Price")
                                    );
$p['ADD_STRINGS_modif'] =  array   (
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true)
                                    );

$p['ADD_NUMERICS'] =        array   (
                                        array("RO" => "Categorie","EN" => "Category"),
                                        array("RO" => "Eveniment sediu 1","EN" => "Event in location 1"),
                                        array("RO" => "Eveniment sediu 2","EN" => "Event in location 2")
                                    );
$p['ADD_NUMERICS_modif'] =  array   (
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true),
                                        array(0 => true,1 => true)
                                    );

$bauturi = new combinedObjectTTDI();
$bauturi->setParams($p);
$classManagger->add($bauturi);


/*********************************************************************************ETELEK / ITALOK*/
unset($p);
$p = $menuWrapper_defparams;
$p['KEY']="produse";
$p['MENULABLE']="Produse";
$p['DISPLAYMENUTAB']=true;
$p['LANGUAGES']=array("RO","EN");
$p['CURRENTLANGUAGE']=0;

$produse = new menuWrapper();
$produse->setParams($p);
$produse->add($categ_manc);
$produse->add($categ_baut);
$produse->add($mancaruri);
$produse->add($bauturi);

$classManagger->add($produse);


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
					<div class="DIV_LOGO_POS">
						<div class="DIV_LOGO"></div>						
					</div>
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
					<div class="DIV_COPYRIGHT">Copyright &copy; 2010</div>
				</div>							
			</div>
		</div>				
	</body>
</html>
<?php 
	mysql_close($connection);
?>
