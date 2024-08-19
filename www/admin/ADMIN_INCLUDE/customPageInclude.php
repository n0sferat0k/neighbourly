<?php 
if(!defined("IN_ADMIN"))
	die("HACKING IS FUN!");
$customPageInclude_defparams=array
(
	"KEY" => "default",
	"MENULABLE" => "default",
	"DISPLAYMENUTAB" => true,
	
	"CONTENT" => "default.php",
	
	"MENU_template" => "TEMPLATES/menutab.php",
	"NAVIGATOR_template" => "TEMPLATES/navigator.php",
			
	"LANGUAGES" => array("RO"),
	"CURRENTLANGUAGE" => 0
);


class customPageInclude //Text Title Date Image
{
	private $params;
	private $struct;
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
	function customPageInclude()
	{	
	}
	function getParams()
	{
		return $this->params;
	}
	function setParams($params)
	{
		$this->params = $params;
	}	
	function workWithStruct($struct)
	{
		$this->struct = $struct;
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
	function displayBody($key)
	{
	 	if($this->params['KEY']==$key)
	 	{
		 	
			//GLOBAL VARIABLES PASSED FROM ADMIN STRUCTURE TO CUSTOM PAGE
			global $PAGE_KEY;
			global $PAGE_STRUCT_NAME;
			global $PAGE_STRUCT_ID;
			global $PAGE_LANGUAGE;
			global $TEXTS;
			$PAGE_LANGUAGE = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
	 	
			$PAGE_KEY = $this->params['KEY'];
			if(isset($this->struct))
			{
				$PAGE_STRUCT_NAME = $this->struct;
				$PAGE_STRUCT_ID = $_REQUEST['param1'];
			}						
			include($this->params['CONTENT']);
		}
	}
}
?>
