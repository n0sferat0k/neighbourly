<?php 
if(!defined("IN_ADMIN"))
	die("HACKING IS FUN!");
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);	
error_reporting(E_ERROR|E_WARNING|E_PARSE);
//error_reporting(0);
$SESSIONLENGTH = 1200;

if(strpos($_SERVER['REQUEST_URI'],"\\"))
    $tokens = explode("\\",$_SERVER['REQUEST_URI']);
else
    $tokens = explode("/",$_SERVER['REQUEST_URI']);

$SITE_BASE_DIR = "/";
if(is_array($tokens))
    for($i=0;$i<(count($tokens)-2);$i++)
    {                        
        $token = $tokens[$i];
        if($token != "")
            $SITE_BASE_DIR .= $token . "/";
    } 

define("SITE_BASE_DIR",$SITE_BASE_DIR);

class loginManager
{	
	function doLoginCheck()
	{	
		global $DATABASE;
		global $SESSIONLENGTH;
		global $connection;
		
	 	$admintableexists = false;
	 	$sessiontableexists = false;
		$tables = mysqli_query($connection, "SHOW TABLES FROM " . $DATABASE)or die(mysqli_error($connection) . " SHOW TABLES");
	 	while($row = mysqli_fetch_array($tables))
		{
			if("admin" == $row["Tables_in_" . $DATABASE])
			{
				$admintableexists=true;				
			}
			if("session" == $row["Tables_in_" . $DATABASE])
			{
				$sessiontableexists=true;				
			}			
		}
	 
	 	if(!$admintableexists)
	 	{
			$query ="CREATE TABLE `" . $DATABASE . "`.`admin`(
						`admin_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY";			
			$query.=", `admin_uname` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";	
			$query.=", `admin_pass` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";	
			$query.=", `admin_name` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";				
			$query.=", `admin_level` INT NOT NULL DEFAULT '0'";
			$query.=") ENGINE = MYISAM";
			mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
			
			mysqli_query($connection, "INSERT INTO admin (admin_uname,admin_pass,admin_name,admin_level) VALUES ('admin','21232f297a57a5a743894a0e4a801fc3','default admin','0')")or die(mysqli_error($connection));
		}
	 	if(!$sessiontableexists)
	 	{
			$query ="CREATE TABLE `" . $DATABASE . "`.`session`(
					   `session_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY";			
			$query.=", `session_hash` varchar(32) NOT NULL";	
			$query.=", `session_start` INT NOT NULL";			
			$query.=", `session_expire` INT NOT NULL";			
			$query.=", `admin_id` INT NOT NULL";
			$query.=") ENGINE = MYISAM";
			mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);							
		}
		else
		{
		 	$query = "DELETE FROM session WHERE session_expire < " . time();
			mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
		}
		 	
	 	if(isset($_REQUEST['param1']))
		{	
		 	if($_REQUEST['param1']=="dologin")
		 	{
				$query = "SELECT * FROM admin WHERE admin_uname ='" . urlencode($_REQUEST['admin_uname']) . "' AND admin_pass='" . md5($_REQUEST['admin_pass']) . "'";
				$data = mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" .$query);
				if(mysqli_num_rows($data)>0)
				{
					//login succes
					srand(time());
				 	$nr = rand(0,100000);
				 	$hash = md5($nr);
					 	
					$row = mysqli_fetch_array($data);
					$_SESSION['logged']=true;
					$_SESSION['hash']=$hash;
					$_SESSION['admin_name']=$row['admin_name'];
					$_SESSION['admin_id']=$row['admin_id'];
					$_SESSION['admin_level']=$row['admin_level'];
					
					$query = "SELECT * FROM session WHERE admin_id = " . $row['admin_id'];
					$data = mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
					if(mysqli_num_rows($data) > 0)
					{
						$query = "UPDATE session SET session_hash = '" . $_SESSION['hash'] . "',session_expire = " . (time() + $SESSIONLENGTH) . " WHERE admin_id = " . $row['admin_id'];
						mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
					}
					else
					{
					 	
						$query = "INSERT INTO session (session_hash,session_start,session_expire,admin_id) VALUES ('" . $_SESSION['hash'] . "'," . time() . "," . (time() + $SESSIONLENGTH) . "," . $row['admin_id'] . ")";
						mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);						
					}
				}
				else
				{
					//login fail
					unset($_SESSION);
					session_destroy();
				}
			}
			if(($_REQUEST['param1']=="listusers")&&($_SESSION['admin_level']=='0'))
		 	{
		 	 	$_SESSION['admin_op'] = "listusers";
		 	 	return false;
			}
			if(($_REQUEST['param1']=="adduser")&&($_SESSION['admin_level']=='0'))
		 	{
				mysqli_query($connection, "INSERT INTO admin (admin_uname,admin_pass,admin_name,admin_level) VALUES ('" . urlencode($_REQUEST['admin_uname']) . "','" . md5($_REQUEST['admin_pass']) . "','" . $_REQUEST['admin_name'] . "','" . $_REQUEST['admin_level'] . "')")or die(mysqli_error($connection));
		 	 	$_SESSION['admin_op'] = "listusers";
		 	 	return false;
			}
			if(($_REQUEST['param1']=="deluser")&&($_SESSION['admin_level']=='0'))
		 	{
		 	 	mysqli_query($connection, "DELETE FROM admin WHERE admin_id = '" . $_REQUEST['admin_id'] . "'")or die(mysqli_error($connection));
		 	 	$_SESSION['admin_op'] = "listusers";
		 	 	return false;
			}
			if(($_REQUEST['param1']=="doneusers")&&($_SESSION['admin_level']=='0'))
		 	{		 	 	
		 	 	unset($_SESSION['admin_op']);
			}						
			if($_REQUEST['param1']=="dologout")
		 	{
		 	 	$query = "DELETE FROM session WHERE admin_id = " . $_SESSION['admin_id'];
				$data = mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
		 		unset($_SESSION);
				session_destroy();
			}			
		} 	
		
		if((isset($_SESSION['logged']))&&($_SESSION['logged']==true))
		{	
		 	$query = "SELECT * FROM session WHERE admin_id = " . (($_SESSION['admin_id'])? $_SESSION['admin_id']:-1) . " AND session_hash = '" . $_SESSION['hash'] . "'";
			$data = mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
		 	if(mysqli_num_rows($data) > 0)
		 	{
                $query = "UPDATE session SET session_expire = " . (time() + $SESSIONLENGTH) . " WHERE admin_id = " . (($_SESSION['admin_id'])? $_SESSION['admin_id']:-1) . " AND session_hash = '" . $_SESSION['hash'] . "'";
                mysqli_query($connection, $query)or die(mysqli_error($connection) . "<br />" . $query);
				return true;				
			}
			else
			{					
				return false;
			}		 
		}
		else 
			return false;	
	}		
	
	function showLoginForm()
	{
		$lable = 'administrare site (utilizator/parola)';		
		$usernameTXT = 'User:';
		$usernameINP = 'admin_uname';		
		$userpassTXT = 'Password:';
		$userpassINP = 'admin_pass';		
		$buttonTXT="Trimite";
		$param1="dologin";		

		
		$tpl = new Template();

		$tpl->set('lable', $lable);
		$tpl->set('usernameTXT', $usernameTXT);
		$tpl->set('usernameINP', $usernameINP);
		$tpl->set('userpassTXT', $userpassTXT);
		$tpl->set('userpassINP', $userpassINP);
		$tpl->set('buttonTXT', $buttonTXT);
		$tpl->set('param1', $param1);

		
		echo $tpl->fetch('TEMPLATES/loginform.php');
	}
	function showUserListForm()
	{		
		global $connection;
		$tpl = new Template();

		$tpl->set('lable_adduser', 'Adauga Utilizator');
		$tpl->set('lable_username', 'Utilizator');
		$tpl->set('usernameINP', 'admin_uname');
		$tpl->set('lable_userpass', 'Parola');
		$tpl->set('userpassINP', 'admin_pass');
		$tpl->set('lable_userpassrepeat', 'Repeta Parola');		
		$tpl->set('userpassrepeatINP', 'admin_pass2');		
		$tpl->set('lable_button', 'Trimite');
		$tpl->set('param1', 'adduser');
		
		$tpl->set('lable_name', 'Nume Prenume');
		$tpl->set('nameINP', 'admin_name');
		
		$options[0]['value_option'] = "0";
		$options[0]['lable_option'] = "Admin";
		$options[1]['value_option'] = "1";
		$options[1]['lable_option'] = "Agent";		
		
		$tpl->set('options', $options);
		$tpl->set('lable_userlevel', 'Tip Utilizator');		
		$tpl->set('userlevelINP', 'admin_level');				
				
		
		$data = mysqli_query($connection, "SELECT * FROM admin ORDER BY admin_level")or die(mysqli_error($connection));
		$cnt=0;
		$admins = array();
		while($row=mysqli_fetch_array($data))
		{
		 	$admins[$cnt]['admin_name'] = $row['admin_name'];
		 	$admins[$cnt]['admin_uname'] = $row['admin_uname'];
		 	$admins[$cnt]['admin_level'] = ($row['admin_level']=='0')? "Admin":"Agent";
		 	$admins[$cnt]['admin_id'] = $row['admin_id'];
		 	
			$admins[$cnt]['confirm_del'] = "Stergi userul ?";
			$admins[$cnt]['param1'] = "deluser";
			$admins[$cnt]['admin_id_INP'] = 'admin_id';
			$admins[$cnt]['lable_del'] = 'Sterge';
			 		 	
			$cnt++;
		}
		$tpl->set('admins', $admins);
		
		$tpl->set('param1_exit', "doneusers");
		$tpl->set('lable_exit', "Terminat");
			
		echo $tpl->fetch('TEMPLATES/userlistform.php');
		
	}	
}

class classManagger
{
 	private $classArray;
	  	
 	function __construct()
 	{
 	 	unset($this->classArray);
		$this->classArray['size']=0;	
	}
 	
	function add($class)
	{
		$this->classArray['classlist'][$this->classArray['size']]=$class;
		$this->classArray['size']++;		
	}	
	
	function displayMenues($KEY)
	{
		for($i=0;$i<$this->classArray['size'];$i++)
		{
			$this->classArray['classlist'][$i]->displayMenu($KEY);
		}	
	}
	function displayLanguages($KEY)
	{
		for($i=0;$i<$this->classArray['size'];$i++)
		{
			$this->classArray['classlist'][$i]->languageSelection($KEY);
		}	
	}
	function displayBodys($KEY)
	{	 	
		for($i=0;$i<$this->classArray['size'];$i++)
		{		 	
			$this->classArray['classlist'][$i]->displayBody($KEY);
		}
	}
	function displayNavigations($KEY)
	{
		for($i=0;$i<$this->classArray['size'];$i++)
		{
			$this->classArray['classlist'][$i]->displayNavigation($KEY);
		}		
	}	
}

?>