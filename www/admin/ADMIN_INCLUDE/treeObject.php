<?php 
if(!defined("IN_ADMIN"))
	die("HACKING IS FUN!");
$treeObject_defparams=array
(
	"KEY" => "default",
	"MENULABLE" => "default",
	"DISPLAYMENUTAB" => true,
	
	"TEXT_modif" => true,
	
	"TREE_template" => "TEMPLATES/tree.php",
	"MENU_template" => "TEMPLATES/menutab.php",
	"NAVIGATOR_template" => "TEMPLATES/navigator.php",	
	
	"LANGUAGES" => array("RO"),
	"CURRENTLANGUAGE" => 0,
	
	"DELETEABLE" => true
);


class treeObject //Text Title Date Image
{
	private $params;
	private $assoc;
	private $parent = -1;
	function setParent($obj)
	{
		$this->parent = $obj;
	}
	function displayNavigation($KEY)	
	{
	 	$tpl = new Template();
	 	
		if($this->params['KEY']==$KEY)
		{
		 	if(-1 != $this->parent)
		 	{
				$p = $this->parent->getParams();
				$k = $p['KEY'];
				$this->parent->displayNavigation($k);
			}
			
			$tpl->set('text',$this->params['MENULABLE'] . "&nbsp;&raquo;&nbsp;");
			$tpl->set('href',"?page=" . $this->params['KEY']);			
			echo $tpl->fetch($this->params['NAVIGATOR_template']);			
		}
	}
	function treeObject()
	{						
	}
	function getParams()
	{
		return $this->params;
	}
	function setParams($params)
	{
		$this->params = $params;
		$this->manageDataStructure();
	}
	function associateWith($obj)
	{
		$this->assoc = $obj;		
	}
	function displayMenu($key)
	{
		if($this->params['DISPLAYMENUTAB']==true)
		{
			$tpl = new Template();
		
			if($this->params['KEY']==$key)
				$tpl->set('selected', "_selected");
			else
				$tpl->set('selected', "");
				
			$tpl->set('href', "?page=" . $this->params['KEY']);
			$tpl->set('text', $this->params['MENULABLE']);
			
			echo $tpl->fetch($this->params['MENU_template']);
		}
	}
	function languageSelection($key)
	{
		if($this->params['KEY']==$key)
		{
		 	//IF A LANGUAGE SET OPERATION WAS JUST PERFORMED SET SESSION VARIABLE
		 	if($_REQUEST['param1']=="setlang")
		 	{
				$_SESSION['language'] = $_REQUEST['language'];
			}
			//IF SESSION HAS SET LANGUAGE ATTEMPT TO SINC WITH LANGUAGE
			
			if(isset($_SESSION['language']))
			{
			 	$hasthislanguage = false;			 	
				for($i=0;$i<count($this->params['LANGUAGES']);$i++)
				{	
				 	if($this->params['LANGUAGES'][$i] == $_SESSION['language'])
				 	{
				 		//SESSION LANG CORRESPONDS TO ONE OF OBJECTS SET LANGUAGES
						$this->params['CURRENTLANGUAGE']=$i;
				 		$hasthislanguage=true;
				 	}
				}
				//SESSION LANGUAGE NOT IN OBJECT SET SESSION TO OBJECTS LAST (default) LANGUAGE
				if(!$hasthislanguage)
					$_SESSION['language']=$this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
			}
			else
			{
				//session object language not set, set it to current objects default 
				$_SESSION['language']=$this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];	
			}
			
			
			//set array for language template
			$tpl_array = array();
			if(count($this->params['LANGUAGES'])>1)
			{
				for($i=0;$i<count($this->params['LANGUAGES']);$i++)
				{

                                    $tpl_array[$i]=array	(
                                                                        'param1' => "setlang",
                                                                        'extraparamINP' => $this->params['KEY'] . "_id",
                                                                        'extraparamVAR' => $_REQUEST[$this->params['KEY'] . "_id"],
                                                                        'languageINP' => "language",
                                                                        'languageVAR' => $this->params['LANGUAGES'][$i],
                                                                        'selected' => (($this->params['LANGUAGES'][$i] == $_SESSION['language'])? true:false),
                                                                        'hasnext' => (($i<(count($this->params['LANGUAGES'])-1))? true:false)
                                                                );
				}
				$tpl = new Template();
				$tpl->set('langlinks', $tpl_array);
				echo $tpl->fetch('TEMPLATES/languagebar.php');
			}
		}
	}
	function manageDataStructure()
	{
		global $DATABASE;
		
		//check if table exists
		$tableexists = false;				
		$tables = mysql_query("SHOW TABLES FROM " . $DATABASE)or die(mysql_error());	
		
		while($row = mysql_fetch_array($tables))
		{
			if(strtolower($this->params['KEY']) == strtolower($row["Tables_in_" . $DATABASE]))
				$tableexists=true;
		}	
		
		
		
		//IF TABLE EXISTS CJECK IF IT NEEDS ALTERING
		if($tableexists)
		{	
		 	$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE 0";
			$data = mysql_query($query);
			for($i=0;$i<mysql_num_fields($data);$i++)
			{
				for($j=0;$j<count($this->params['LANGUAGES']);$j++)
				{				 					
			 		if($this->params['KEY'] . "_text_" . $this->params['LANGUAGES'][$j] == mysql_field_name($data,$i))
					{
						$var = "title_" . $this->params['LANGUAGES'][$j];
						$$var = true;
					}																			
				}				
			}	 			 
		 	
			for($i=0;$i<count($this->params['LANGUAGES']);$i++)
			{		 							 
			  	//IF TITLE IS NOT IN TABLE AND IS NEEDED ALTER TABLE
				$var = "title_" . $this->params['LANGUAGES'][$i];						
				if(!$$var)
				{
					$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_titlu_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
					mysql_query($query)or die(mysql_error());
				}				
			}					
		}
		//IF TABLE DOES NOT EXIST CREATE IT
		else
		{
			$query ="CREATE TABLE `" . $DATABASE . "`.`" . $this->params['KEY'] . "`(
						`" . $this->params['KEY'] . "_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY 
						,`" . $this->params['KEY'] . "_parent` INT NOT NULL DEFAULT '-1'";
						
			for($i=0;$i<count($this->params['LANGUAGES']);$i++)
			{				
				$query.=", `" . $this->params['KEY'] . "_titlu_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";				
			}				
			$query.=") ENGINE = MYISAM";
			
			mysql_query($query)or die(mysql_error() . $query);
		}		
	}			
	function displayBody($key)
	{	 
		if($key == $this->params['KEY']) //IF CLASS RECOGNIZES ITSELF AS THE CURRENT DISPLAY CLASS
		{
		 	if(((isset($_REQUEST[$this->params['KEY'] . '_id']))&&($_REQUEST[$this->params['KEY'] . '_id']!=''))||(isset($_REQUEST['param1'])))//if single display is on and an operation has been made, do necesarry operations
		 	{		 	 	
				$this->doOp();
			} 			 	
		 	
		 	//CHECK FOR EXISTANCE OF ROOT ELEMENT
		 	
		 	if((isset($_REQUEST[$this->params['KEY'] . '_id']))&&($_REQUEST['param1']=="openasroot"))
		 		echo $this->recursiveDisplay($_REQUEST[$this->params['KEY'] . '_id'],-1,true);
			else
		 	{
				$query="SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_parent = '-1'";  
				$data = mysql_query($query)or die(mysql_error() . $query);
				
			 	if(mysql_num_rows($data)==0)
			 	{							
					echo $this->recursiveDisplay(-1,-1,true);			
				}
				else
				{
				 	$row = mysql_fetch_array($data);			 	
					echo $this->recursiveDisplay($row[$this->params['KEY'] . '_id'],-1,true);	
				}
			}			
		}	
	}
	function doOp()
	{
		$LANG = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
		if($_REQUEST['param1']=="save") //save element
		{
			if($_REQUEST[$this->params['KEY'] . '_id']=="-1")//save new element
			{
				$query = "INSERT INTO " . $this->params['KEY'] . " (" . $this->params['KEY'] . "_text_" . $LANG . ", " . $this->params['KEY'] . "_parent) VALUES ('" . $_REQUEST[$this->params['KEY'] . '_text_' . $LANG] . "','" . $_REQUEST['param2'] . "')";
				mysql_query($query)or die (mysql_error() . $query);
			}
			else //modify existing element
			{
				$query = "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_text_" . $LANG . " ='" . $_REQUEST[$this->params['KEY'] . '_text_' . $LANG] . "' WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";	
				mysql_query($query)or die (mysql_error() . $query);	
			}
		}
		if($_REQUEST['param1']=="del") //save element
		{
		 	$this->recursiveDelete($_REQUEST[$this->params['KEY'] . '_id']);
		}
	}
	function recursiveDelete($id)
	{
		$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_parent = '" . $id . "'";
		$data = mysql_query($query)or die(mysql_error());
		while($row = mysql_fetch_array($data))
		{
			$this->recursiveDelete($row[$this->params['KEY'] . '_id']);
		}
		$query = "DELETE FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $id . "'";
		mysql_query($query)or die(mysql_error());			
	} 
	function recursiveDisplay($this_id,$parent_id,$firstloop)
	{
	 	global $TEXTS;
		$LANG = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
	 	$tpl = new Template();
	 	$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = " . $this_id;
 		$data = mysql_query($query)or die(mysql_error() . $query);
 		
 		if((mysql_num_rows($data)>0)&&($this_id!= '-1' ))
 		{	
 		 	//element exists, show element and children
			//ELEMENT
			$row = mysql_fetch_array($data);
		
			$tpl->set('text_iseditable',$this->params['TEXT_modif']);
			$tpl->set('is_deleteable',$this->params['DELETEABLE']);
			$tpl->set('exists',true);
			$tpl->set('can_add_children',true);
			$tpl->set('firstloop',$firstloop);
			$tpl->set('has_association',isset($this->assoc));	
			
			
			

			$tpl->set('keyname', $this->params['KEY'] . '_id');
			$tpl->set('keyvalue', $row[$this->params['KEY'] . '_id']);
			$tpl->set('save_param1', 'save');
			$tpl->set('textINP', $this->params['KEY'] . '_text_' . $LANG);
			$tpl->set('textVAR', $row[$this->params['KEY'] . '_text_' . $LANG]);
			$tpl->set('del_param1', 'del');			
			$tpl->set('del_confirm', $TEXTS[$LANG]['del_confirm']);
			$tpl->set('addassocobj_param1', $row[$this->params['KEY'] . '_id']);
			if(isset($this->assoc))
			{
				$tmp = $this->assoc->getParams();
				$tpl->set('addassocobj_page', $tmp['KEY']);
			}
			
			
						
			$tpl->set('openasroot_param1', 'openasroot');
			$tpl->set('childdiv_id', $this->params['KEY'] . "_" . $row[$this->params['KEY'] . '_id'] . '_children');
			
			
			//CHILDREN
			$query2 = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_parent = " . $this_id;	
			$data2 = mysql_query($query2)or die(mysql_error() . $query2);
			if(mysql_num_rows($data2)>0)
				$tpl->set('has_children',true);
			else
				$tpl->set('has_children',false);
				
			$children="";
			while($row2 = mysql_fetch_array($data2))
			{
				$children.=$this->recursiveDisplay($row2[$this->params['KEY'] . '_id'],$this_id,false);
			}
			$children.=$this->recursiveDisplay(-1,$this_id,false);
			$tpl->set('childdiv_content', $children);
		}
		else
		{		 	
			//element does not exist, edit-create and add to parent
		 	$tpl->set('text_iseditable',$this->params['TEXT_modif']);
			$tpl->set('is_deleteable',false);
			$tpl->set('exists',false);
			$tpl->set('has_children',false);
			$tpl->set('can_add_children',false);	
			$tpl->set('firstloop',$firstloop);
			$tpl->set('has_association',false);
			
			
			$tpl->set('keyname', $this->params['KEY'] . '_id');
			$tpl->set('keyvalue', "-1");
			$tpl->set('save_param1', 'save');
			$tpl->set('save_param2', $parent_id);
			$tpl->set('textINP', $this->params['KEY'] . '_text_' . $LANG);
			$tpl->set('textVAR', "");			
		}
		
		return $tpl->fetch($this->params['TREE_template']);	 	
		
	}
}
?>
