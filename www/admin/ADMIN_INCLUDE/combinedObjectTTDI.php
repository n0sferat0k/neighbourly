<?php
if (!defined("IN_ADMIN"))
	die("HACKING IS FUN!");

$combinedObcejtTTDI_defparams = array(
	"KEY" => "default",
	"MENULABLE" => "default",
	"DISPLAYMENUTAB" => true,

	"TEXT" => true,
	"TITLE" => true,
	"DATE" => false,
	"LINK" => false,
	"PIC" => false,
	"FILE" => false,
	"ACCENT" => false,
	"ZINDEX" => false,
	"MOVEABLE" => false,
	"SORTINDEX" => false,
	"ADD_NUMERICS" => array(),
	"ADD_STRINGS" => array(),


	"MULTIPIC" => false,
	"MULTIFILE" => false,
	"PICHASNAME" => false,
	"FILEHASNAME" => false,
	"NOPICSRC" => "graphics/no_pic.jpg",

	"TEXT_modif" => true,
	"TITLE_modif" => true,
	"PIC_modif" => true,
	"PIC_del" => true,
	"FILE_del" => true,
	"FILE_modif" => true,
	"DATE_modif" => false,
	"LINK_modif" => false,
	"ADD_NUMERICS_modif" => array(),
	"ADD_STRINGS_modif" => array(),

	"TEXT_value" => "",
	"TITLE_value" => "",
	"PIC_value" => "",
	"DATE_value" => "",

	"SINGULAR" => false,
	"DELETABLE" => false,
	"APPENDABLE" => true,

	"ELPERPAGE" => 40,
	"PAGESPAN" => 10,

	"ORDER"	=> "id",
	"ORDERDIR" => "DESC",
	"QUERY_custom_field" => null,
	"QUERY_custom_tail" => null,

	"LIST_template" => "TEMPLATES/list.php",
	"SINGLE_template" => "TEMPLATES/single.php",
	"MENU_template" => "TEMPLATES/menutab.php",
	"NAVIGATOR_template" => "TEMPLATES/navigator.php",

	"LANGUAGES" => array("RO"),
	"CURRENTLANGUAGE" => 0,
	"PIC_islanguagedependent" => false
);

class combinedObjectTTDI //Text Title Date Image 
{
	private $params;
	private $parent = -1;
	function __construct() {}

	function setParent($obj)
	{
		$this->parent = $obj;
	}
	function displayNavigation($KEY)
	{

		$tpl = new Template();

		if ($this->params['KEY'] == $KEY) {
			if (-1 != $this->parent) {
				$p = $this->parent->getParams();
				$k = $p['KEY'];
				$this->parent->displayNavigation($k);
			}

			$tpl->set('text', $this->params['MENULABLE'] . "&nbsp;&raquo;&nbsp;");
			$tpl->set('href', "?page=" . $this->params['KEY']);
			echo $tpl->fetch($this->params['NAVIGATOR_template']);
		}
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
	function displayMenu($key)
	{
		if ($this->params['DISPLAYMENUTAB'] == true) {
			$tpl = new Template();

			if ($this->params['KEY'] == $key)
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
		if ($this->params['KEY'] == $key) {
			//IF A LANGUAGE SET OPERATION WAS JUST PERFORMED SET SESSION VARIABLE
			if (isset($_REQUEST['param1']) && $_REQUEST['param1'] == "setlang") {
				$_SESSION['language'] = $_REQUEST['language'];
			}

			//IF SESSION HAS SET LANGUAGE ATTEMPT TO SINC WITH LANGUAGE
			if (isset($_SESSION['language'])) {
				$hasthislanguage = false;
				for ($i = 0; $i < count($this->params['LANGUAGES']); $i++) {
					if ($this->params['LANGUAGES'][$i] == $_SESSION['language']) {
						//SESSION LANG CORRESPONDS TO ONE OF OBJECTS SET LANGUAGES
						$this->params['CURRENTLANGUAGE'] = $i;
						$hasthislanguage = true;
					}
				}
				//SESSION LANGUAGE NOT IN OBJECT SET SESSION TO OBJECTS LAST (default) LANGUAGE
				if (!$hasthislanguage)
					$_SESSION['language'] = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
			} else {
				//session object language not set, set it to current objects default 
				$_SESSION['language'] = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
			}


			if ($key == $this->params['KEY']) //IF CLASS RECOGNIZES ITSELF AS THE CURRENT DISPLAY CLASS
			{
				if (((isset($_REQUEST[$this->params['KEY'] . '_id'])) && ($_REQUEST[$this->params['KEY'] . '_id'] != '')) || (isset($_REQUEST['param1']))) //if single display is on and an operation has been made, do necesarry operations
				{
					$this->doOp();
				}
			}

			//set array for language template
			$tpl_array = array();
			if (count($this->params['LANGUAGES']) > 1) {
				for ($i = 0; $i < count($this->params['LANGUAGES']); $i++) {

					$tpl_array[$i] = array(
						'param1' => "setlang",
						'extraparamINP' => $this->params['KEY'] . "_id",
						'extraparamVAR' => isset($_REQUEST[$this->params['KEY'] . "_id"]) ? $_REQUEST[$this->params['KEY'] . "_id"] : "",
						'languageINP' => "language",
						'languageVAR' => $this->params['LANGUAGES'][$i],
						'selected' => (($this->params['LANGUAGES'][$i] == $_SESSION['language']) ? true : false),
						'hasnext' => (($i < (count($this->params['LANGUAGES']) - 1)) ? true : false)
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
		global $connection;

		//check if table exists
		$tableexists = false;
		$pictableexists = false;
		$filetableexists = false;

		$tables = mysqli_query($connection, "SHOW TABLES FROM " . $DATABASE) or die(mysqli_error($connection));

		while ($row = mysqli_fetch_array($tables)) {
			if (strtolower($this->params['KEY']) == strtolower($row["Tables_in_" . $DATABASE])) {
				$tableexists = true;
			}


			if (strtolower($this->params['KEY'] . "_imgs") == strtolower($row["Tables_in_" . $DATABASE]))
				$pictableexists = true;

			if (strtolower($this->params['KEY'] . "_files") == strtolower($row["Tables_in_" . $DATABASE]))
				$filetableexists = true;
		}


		if (($this->params['MULTIPIC']) && ($this->params['PIC'])) {
			//IF MULTI PIC BUT PIC TABLE DOES NOT EXIST
			if (!$pictableexists) {
				$query = "CREATE TABLE `" . $DATABASE . "`.`" . $this->params['KEY'] . "_imgs`(
							`" . $this->params['KEY'] . "_IMGS_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY";

				$query .= ", `" . $this->params['KEY'] . "_IMGS_pic` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
				$query .= ", `" . $this->params['KEY'] . "_id` INT NOT NULL";
				if ($this->params['PICHASNAME']) {
					$query .= ", `" . $this->params['KEY'] . "_IMGS_name` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL";
				}
				$query .= ") ENGINE = MYISAM";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			} else {
				//IF MULTI PIC TABLE EXISTS BUT HAS NO NAME
				if ($this->params['PICHASNAME']) {
					$hasname = false;
					$query = "SELECT * FROM " . $this->params['KEY'] . "_imgs WHERE 0";
					$data = mysqli_query($connection, $query);
					for ($i = 0; $i < mysqli_num_fields($data); $i++) {
						if ($this->params['KEY'] . "_IMGS_name" == mysqli_field_name($data, $i))
							$hasname = true;
					}
					if (!$hasname) {
						$query = "ALTER TABLE `" . $this->params['KEY'] . "_imgs` ADD `" .  $this->params['KEY'] . "_IMGS_name` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL";
						mysqli_query($connection, $query) or die(mysqli_error($connection));
					}
				}
			}
		}

		if ($this->params['FILE']) {
			//IF HAS FILES BUT FILE TABLE DOES NOT EXIST
			if (!$filetableexists) {
				$query = "CREATE TABLE `" . $DATABASE . "`.`" . $this->params['KEY'] . "_files`(
							`" . $this->params['KEY'] . "_FILES_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY";

				$query .= ", `" . $this->params['KEY'] . "_FILES_file` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
				$query .= ", `" . $this->params['KEY'] . "_id` INT NOT NULL";
				if ($this->params['FILEHASNAME']) {
					$query .= ", `" . $this->params['KEY'] . "_FILES_name` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL";
				}
				$query .= ") ENGINE = MYISAM";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			} else {
				//IF FILE TABLE EXISTS BUT HAS NO NAME
				if ($this->params['FILEHASNAME']) {
					$hasname = false;
					$query = "SELECT * FROM " . $this->params['KEY'] . "_files WHERE 0";
					$data = mysqli_query($connection, $query);
					for ($i = 0; $i < mysqli_num_fields($connection, $data); $i++) {
						if ($this->params['KEY'] . "_FILES_name" == mysqli_field_name($connection, $data, $i))
							$hasname = true;
					}
					if (!$hasname) {
						$query = "ALTER TABLE `" . $this->params['KEY'] . "_files` ADD `" .  $this->params['KEY'] . "_FILES_name` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL";
						mysqli_query($connection, $query) or die(mysqli_error($connection));
					}
				}
			}
		}

		//IF TABLE EXISTS CHECK IF IT NEEDS ALTERING
		if ($tableexists) {

			//CHECK WICH COLUMNS ARE ALREADY IN TABLE 
			$text = false;
			$title = false;
			$date = false;
			$pic = false;

			$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE 0";
			$data = mysqli_query($connection, $query);
			for ($i = 0; $i < mysqli_num_fields($data); $i++) {
				for ($j = 0; $j < count($this->params['LANGUAGES']); $j++) {
					if ($this->params['KEY'] . "_text_" . $this->params['LANGUAGES'][$j] == mysqli_field_name($data, $i)) {
						$var = "text_" . $this->params['LANGUAGES'][$j];
						$$var = true;
					}
					if ($this->params['KEY'] . "_titlu_" . $this->params['LANGUAGES'][$j] == mysqli_field_name($data, $i)) {
						$var = "title_" . $this->params['LANGUAGES'][$j];
						$$var = true;
					}
					if ($this->params['KEY'] . "_pic_" . $this->params['LANGUAGES'][$j] == mysqli_field_name($data, $i)) {
						$var = "pic_" . $this->params['LANGUAGES'][$j];
						$$var = true;
					}
				}
				if ($this->params['KEY'] . "_data" == mysqli_field_name($data, $i))
					$date = true;
				if ($this->params['KEY'] . "_link" == mysqli_field_name($data, $i))
					$link = true;
				if ($this->params['KEY'] . "_pic" == mysqli_field_name($data, $i))
					$pic = true;
				if ($this->params['KEY'] . "_accent" == mysqli_field_name($data, $i))
					$accent = true;
				if ($this->params['KEY'] . "_zindex" == mysqli_field_name($data, $i))
					$zindex = true;
				if ($this->params['KEY'] . "_sortindex" == mysqli_field_name($data, $i))
					$sortindex = true;

				for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
					if ($this->params['KEY'] . "_add_numerics_" . $j == mysqli_field_name($data, $i))
						$add_numerics[$j] = true;
				}
				for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
					if ($this->params['KEY'] . "_add_strings_" . $j == mysqli_field_name($data, $i))
						$add_strings[$j] = true;
				}
			}

			for ($i = 0; $i < count($this->params['LANGUAGES']); $i++) {
				//IF TEXT IS NOT IN TABLE AND IS NEEDED ALTER TABLE
				$var = "text_" . $this->params['LANGUAGES'][$i];
				if (($this->params['TEXT']) && (!$$var)) {
					$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_text_" . $this->params['LANGUAGES'][$i] . "` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
					mysqli_query($connection, $query) or die(mysqli_error($connection));
				}

				//IF TITLE IS NOT IN TABLE AND IS NEEDED ALTER TABLE
				$var = "title_" . $this->params['LANGUAGES'][$i];
				if (($this->params['TITLE']) && (!$$var)) {
					$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_titlu_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
					mysqli_query($connection, $query) or die(mysqli_error($connection));
				}
				if ($this->params['PIC_islanguagedependent']) {
					$var = "pic_" . $this->params['LANGUAGES'][$i];
					if (($this->params['PIC']) && (!$$var)) {
						$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_pic_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
						mysqli_query($connection, $query) or die(mysqli_error($connection));
					}
				}
			}
			//IF DATE IS NOT IN TABLE AND IS NEEDED ALTER TABLE			
			if (($this->params['DATE']) && (!$date)) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_data` INT NOT NULL";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}
			//IF LINK IS NOT IN TABLE AND IS NEEDED ALTER TABLE			
			if (($this->params['LINK']) && (!$link)) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_link` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}
			//IF PIC IS NOT IN TABLE AND IS NEEDED ALTER TABLE
			if (($this->params['PIC']) && (!$pic) && (!$this->params['PIC_islanguagedependent'])) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_pic` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}
			//IF ACCENT IS NOT IN TABLE AND IS NEEDED ALTER TABLE			
			if (($this->params['ACCENT']) && (!$accent)) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_accent` INT NOT NULL DEFAULT '0'";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}
			//IF ZINDEX IS NOT IN TABLE AND IS NEEDED ALTER TABLE			
			if (($this->params['ZINDEX']) && (!$zindex)) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_zindex` INT NOT NULL DEFAULT '0'";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}

			//IF SORTINDEX IS NOT IN TABLE AND IS NEEDED ALTER TABLE
			if (($this->params['SORTINDEX']) && (!$sortindex)) {
				$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_sortindex` INT NOT NULL DEFAULT '0'";
				mysqli_query($connection, $query) or die(mysqli_error($connection));
			}

			//IF ADDITIONAL NUMERICS ARE NOT IN TABLE
			for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
				if ($add_numerics[$j] != true) {
					$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_add_numerics_" . $j . "` INT NOT NULL DEFAULT '0'";
					mysqli_query($connection, $query) or die(mysqli_error($connection));
				}
			}

			//IF ADDITIONAL STRINGS ARE NOT IN TABLE	
			for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
				if ($add_strings[$j] != true) {
					$query = "ALTER TABLE `" . $this->params['KEY'] . "` ADD `" .  $this->params['KEY'] . "_add_strings_" . $j . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT ''";
					mysqli_query($connection, $query) or die(mysqli_error($connection));
				}
			}

			if ((!is_dir("../" . $this->params['KEY'] . "IMGS")) && ($this->params['PIC'])) {
				mkdir("../" . $this->params['KEY'] . "IMGS", 0777);
				chmod("../" . $this->params['KEY'] . "IMGS", 0777);
			}
			if ((!is_dir("../" . $this->params['KEY'] . "FILES")) && ($this->params['FILE'])) {
				mkdir("../" . $this->params['KEY'] . "FILES", 0777);
				chmod("../" . $this->params['KEY'] . "FILES", 0777);
			}
		}
		//IF TABLE DOES NOT EXIST CREATE IT AND CREATE DIR IF NECESARRY
		else {
			$query = "CREATE TABLE `" . $DATABASE . "`.`" . $this->params['KEY'] . "`(
						`" . $this->params['KEY'] . "_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY";

			for ($i = 0; $i < count($this->params['LANGUAGES']); $i++) {
				if ($this->params['TEXT'])
					$query .= ", `" . $this->params['KEY'] . "_text_" . $this->params['LANGUAGES'][$i] . "` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";

				if ($this->params['TITLE'])
					$query .= ", `" . $this->params['KEY'] . "_titlu_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";

				if ($this->params['PIC_islanguagedependent']) {
					if ($this->params['PIC'])
						$query .= ", `" . $this->params['KEY'] . "_pic_" . $this->params['LANGUAGES'][$i] . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";
				}
			}
			if ($this->params['DATE'])
				$query .= ", `" . $this->params['KEY'] . "_data` INT NOT NULL";

			if ($this->params['LINK'])
				$query .= ", `" . $this->params['KEY'] . "_link` TEXT CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";

			if ($this->params['ACCENT'])
				$query .= ", `" . $this->params['KEY'] . "_accent` INT NOT NULL DEFAULT '0'";

			if ($this->params['ZINDEX'])
				$query .= ", `" . $this->params['KEY'] . "_zindex` INT NOT NULL DEFAULT '0'";

			if ($this->params['SORTINDEX'])
				$query .= ", `" . $this->params['KEY'] . "_sortindex` INT NOT NULL DEFAULT '0'";

			if (($this->params['PIC']) && (!$this->params['PIC_islanguagedependent']))
				$query .= ", `" . $this->params['KEY'] . "_pic` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL";

			for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
				$query .= ", `" . $this->params['KEY'] . "_add_numerics_" . $j . "` INT NOT NULL DEFAULT '0'";
			}

			for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
				$query .= ", `" . $this->params['KEY'] . "_add_strings_" . $j . "` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT ''";
			}

			$query .= ") ENGINE = MYISAM";
			mysqli_query($connection, $query) or die(mysqli_error($connection));

			if ((!is_dir("../" . $this->params['KEY'] . "IMGS")) && ($this->params['PIC'])) {
				mkdir("../" . $this->params['KEY'] . "IMGS", 0777);
				chmod("../" . $this->params['KEY'] . "IMGS", 0777);
			}

			if ((!is_dir("../" . $this->params['KEY'] . "FILES")) && ($this->params['FILE'])) {
				mkdir("../" . $this->params['KEY'] . "FILES", 0777);
				chmod("../" . $this->params['KEY'] . "FILES", 0777);
			}
		}
	}

	function displayBody($key)
	{
		global $connection;

		if ($key == $this->params['KEY']) //IF CLASS RECOGNIZES ITSELF AS THE CURRENT DISPLAY CLASS
		{
			if (
				((isset($_REQUEST[$this->params['KEY'] . '_id'])) &&
					(!isRequestParamEqualTo($this->params['KEY'] . '_id', '')) &&
					(!isRequestParamEqualTo('param1', "del")) &&
					(!isRequestParamEqualTo('param1', "up")) &&
					(!isRequestParamEqualTo('param1', "down"))
				) ||
				($this->params['SINGULAR']) ||
				(isRequestParamEqualTo('param1', "add"))
			) //determine weather to do single or list display
			{
				if ($this->params['SINGULAR']) //IF SINGULAR ELEMENT (NOT ADDABLE) DISPLAY DETAILED VIEW BY DEFAULT
				{
					//check if already exists 
					$query = "SELECT * FROM " . $this->params['KEY'];
					$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
					if (mysqli_num_rows($data) > 0) {
						//if exists edit
						$row = mysqli_fetch_array($data);
						$_REQUEST[$this->params['KEY'] . '_id'] = $row[$this->params['KEY'] . '_id'];
					} else //if not exists create
						$_REQUEST[$this->params['KEY'] . '_id'] = "-1";
				}
				$this->singleDisplay();
			} else {
				$this->listDisplay();
			}
		}
	}
	function doOp() //functie care sterge modifica insereaza obiect
	{
		global $connection;

		$LANG = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
		if (isRequestParamEqualTo('param1', "setpicname")) {
			if ($this->params['MULTIPIC']) {
				mysqli_query($connection, "UPDATE " . $this->params['KEY'] . "_imgs SET " . $this->params['KEY'] . "_IMGS_name = '" . $_REQUEST[$this->params['KEY'] . '_IMGS_name'] . "' WHERE " . $this->params['KEY'] . "_IMGS_id = '" . $_REQUEST[$this->params['KEY'] . "_IMGS_id"] . "'") or die(mysqli_error($connection));
			}
		}
		if (isRequestParamEqualTo('param1', "setfilename")) {
			if (($this->params['FILE']) && ($this->params['FILE_modif'])) {
				mysqli_query($connection, "UPDATE " . $this->params['KEY'] . "_files SET " . $this->params['KEY'] . "_FILES_name = '" . $_REQUEST[$this->params['KEY'] . '_FILES_name'] . "' WHERE " . $this->params['KEY'] . "_FILES_id = '" . $_REQUEST[$this->params['KEY'] . "_FILES_id"] . "'") or die(mysqli_error($connection));
			}
		}
		if (isRequestParamEqualTo('param1', "setdefpic")) {
			if ($this->params['MULTIPIC']) {
				mysqli_query($connection, "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_pic = '" . $_REQUEST[$this->params['KEY'] . '_IMGS_id'] . "' WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . "_id"] . "'") or die(mysqli_error($connection));
			}
		}
		if (isRequestParamEqualTo('param1', "up")) {
			if ($this->params['MOVEABLE']) {
				moveInTable($this->params['KEY'], $_REQUEST[$this->params['KEY'] . '_id'], "UP");
			}
		}
		if (isRequestParamEqualTo('param1', "down")) {
			if ($this->params['MOVEABLE']) {
				moveInTable($this->params['KEY'], $_REQUEST[$this->params['KEY'] . '_id'], "DOWN");
			}
		}
		if (isRequestParamEqualTo('param1', "upsort")) {
			if ($this->params['SORTINDEX']) {
				moveInTableBySortIndex($this->params['KEY'], $_REQUEST[$this->params['KEY'] . '_sortindex'], "UP");
			}
		}
		if (isRequestParamEqualTo('param1', "downsort")) {
			if ($this->params['SORTINDEX']) {
				moveInTableBySortIndex($this->params['KEY'], $_REQUEST[$this->params['KEY'] . '_sortindex'], "DOWN");
			}
		}
		if (isRequestParamEqualTo('param1', "del")) //STERGE O INREGISTRARE
		{
			//delete image file from folder if exists
			$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";
			$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
			if (mysqli_num_rows($data) > 0) {
				$row = mysqli_fetch_array($data);
				if ($this->params['FILE']) //DELETE FILES FROM FILE TABLE 
				{
					$data2 = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));
					while ($row2 = mysqli_fetch_array($data2)) {
						unlink("../" . $row2[$this->params['KEY'] . '_FILES_file']);
					}
					mysqli_query($connection, "DELETE FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));
				}
				if ($this->params['MULTIPIC']) //DELETE IMAGES FROM IMAGES TABLE 
				{
					$data2 = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));
					while ($row2 = mysqli_fetch_array($data2)) {
						unlink("../" . $row2[$this->params['KEY'] . '_IMGS_pic']);
					}
					mysqli_query($connection, "DELETE FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));
				} else //DELETE IMAGE(S) FROM OBJECT TABLE
				{

					if ($this->params['PIC_islanguagedependent']) //delete language independent generic image
					{
						if ((isset($row[$this->params['KEY'] . '_pic'])) && ($row[$this->params['KEY'] . '_pic'] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_pic'])))
							unlink("../" . $row[$this->params['KEY'] . '_pic']);
					} else //delete language dependent images
					{
						for ($i = 0; $i < count($this->params['LANGUAGES']); $i++) {
							if ((isset($row[$this->params['KEY'] . '_pic_' . $this->params['LANGUAGES'][$i]])) && ($row[$this->params['KEY'] . '_pic_' . $this->params['LANGUAGES'][$i]] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_pic_' . $this->params['LANGUAGES'][$i]])))
								unlink("../" . $row[$this->params['KEY'] . '_pic_' . $this->params['LANGUAGES'][$i]]);
						}
					}
				}
			}
			//delete from db
			$query = "DELETE FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";
			mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
		}

		if (isRequestParamEqualTo('param1',"delfile") && ($this->params['FILE_del'])) //STERGE FISIERUL UNEI INREGISTRARI
		{
			//delete file from folder if exists
			$query = "SELECT * FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_FILES_id = '" . $_REQUEST[$this->params['KEY'] . '_FILES_id'] . "'";
			$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
			if (mysqli_num_rows($data) > 0) {
				$row = mysqli_fetch_array($data);

				if ((isset($row[$this->params['KEY'] . '_FILES_file'])) && ($row[$this->params['KEY'] . '_FILES_file'] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_FILES_file'])))
					unlink("../" . $row[$this->params['KEY'] . '_FILES_file']);

				//delete reference to file from db
				$query = "DELETE FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_FILES_id = '" . $_REQUEST[$this->params['KEY'] . '_FILES_id'] . "'";
				mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
			}
		}

		if (isRequestParamEqualTo('param1',"delpic")) //STERGE IMAGINEA UNEI INREGISTRARI
		{
			if ($this->params['MULTIPIC']) {
				//delete image file from folder if exists
				$query = "SELECT * FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_IMGS_id = '" . $_REQUEST[$this->params['KEY'] . '_IMGS_id'] . "'";
				$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
				if (mysqli_num_rows($data) > 0) {
					$row = mysqli_fetch_array($data);

					if ((isset($row[$this->params['KEY'] . '_IMGS_pic'])) && ($row[$this->params['KEY'] . '_IMGS_pic'] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_IMGS_pic'])))
						unlink("../" . $row[$this->params['KEY'] . '_IMGS_pic']);

					//delete reference to file from db
					$query = "DELETE FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_IMGS_id = '" . $_REQUEST[$this->params['KEY'] . '_IMGS_id'] . "'";
					mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);

					//check if deleted pic was primary pic
					$data2 = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_pic = '" . $row[$this->params['KEY'] . "_IMGS_id"] . "'") or die(mysqli_error($connection));
					if (mysqli_num_rows($data2) > 0) {
						$row2 = mysqli_fetch_array($data2);
						mysqli_query($connection, "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_pic = '-1' WHERE " . $this->params['KEY'] . "_id = '" . $row2[$this->params['KEY'] . "_id"] . "'") or die(mysqli_error($connection));
					}
				}
			} else {
				//delete image file from folder if exists
				$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";
				$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
				if (mysqli_num_rows($data) > 0) {
					$row = mysqli_fetch_array($data);
					if ($this->params['PIC_islanguagedependent']) {
						if ((isset($row[$this->params['KEY'] . '_pic_' . $LANG])) && ($row[$this->params['KEY'] . '_pic_' . $LANG] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_pic_' . $LANG])))
							unlink("../" . $row[$this->params['KEY'] . '_pic_' . $LANG]);

						//delete reference to file from db
						$query = "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_pic_" . $LANG . "='' WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";
						mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
					} else {
						if ((isset($row[$this->params['KEY'] . '_pic'])) && ($row[$this->params['KEY'] . '_pic'] != '') && (file_exists("../" . $row[$this->params['KEY'] . '_pic'])))
							unlink("../" . $row[$this->params['KEY'] . '_pic']);

						//delete reference to file from db
						$query = "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_pic='' WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'";
						mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
					}
				}
			}
		}

		if (isRequestParamEqualTo('param1',"saveelement")) //SALVAREA UNUI ELEMENT
		{
			if ($_REQUEST[$this->params['KEY'] . '_id'] == "-1") //SALVARE ELEMENT NOU
			{
				if ($this->params['APPENDABLE']) {
					$query_start = "INSERT INTO " . $this->params['KEY'] . " (";
					$query_end = " VALUES (";
					$first = true;
					if ($this->params['PIC']) //if supposed to have pic try 2 save pic
					{
						if (!$this->params['MULTIPIC']) {
							if ($this->params['PIC_islanguagedependent'])
								$fileInputName = $this->params['KEY'] . "_pic_" . $LANG;
							else
								$fileInputName = $this->params['KEY'] . "_pic";

							//salveaza poza si insereaza url-ul in query 
							if ($_FILES[$fileInputName]["tmp_name"]) {
								//daca s-a uploadat fisier								
								//salveaza fisier

								$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "IMGS");
							} else {
								$IMG = "";
							}
							//adauga la query
							if (!$first) //if not the first parameter added, add a comma
							{
								$query_start .= ",";
								$query_end .= ",";
							} else
								$first = false;
							if ($this->params['PIC_islanguagedependent'])	//if language dependent pic add language dependent query
								$query_start .= $this->params['KEY'] . "_pic_" . $LANG;
							else
								$query_start .= $this->params['KEY'] . "_pic";

							$query_end .= "'" . $IMG . "'";
						} else {
							if (!$first) //if not the first parameter added, add a comma
							{
								$query_start .= ",";
								$query_end .= ",";
							} else
								$first = false;

							$query_start .= $this->params['KEY'] . "_pic";
							$query_end .= "'-1'";
						}
					}

					if ($this->params['TITLE']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;
						$query_start .= $this->params['KEY'] . "_titlu_" . $LANG;
						$query_end .= "'" . addslashes($_REQUEST[$this->params['KEY'] . "_titlu_" . $LANG]) . "'";
					}
					if ($this->params['DATE']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;
						$TS = getTimeStamp($_REQUEST[$this->params['KEY'] . "_data"]); //FUNCTIE DIN FISIERUL INITTAB
						$query_start .= $this->params['KEY'] . "_data ";
						$query_end .= "'" . $TS . "'";
					}
					if ($this->params['LINK']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;
						$query_start .= $this->params['KEY'] . "_link";
						$query_end .= "'" . addslashes($_REQUEST[$this->params['KEY'] . "_link"]) . "'";
					}
					if ($this->params['ACCENT']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;
						$query_start .= $this->params['KEY'] . "_accent ";
						if (isset($_REQUEST[$this->params['KEY'] . "_accent"]))
							$query_end .= "'1'";
						else
							$query_end .= "'0'";
					}
					if ($this->params['ZINDEX']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma
							$first = false;
						$query_start .= $this->params['KEY'] . "_zindex ";
						$query_end .= "'" . intval($_REQUEST[$this->params['KEY'] . "_zindex"]) . "'";
					}
					if ($this->params['SORTINDEX']) {
						$sql = "SELECT MAX(" . $this->params['KEY'] . "_sortindex) AS maxindex from " . $this->params['KEY'];
						$data = mysqli_query($connection, $sql) or die(mysqli_error($connection));
						$row = mysqli_fetch_array($data);
						$maxindex = $row['maxindex'];
						if (!$maxindex)
							$maxindex = 1;

						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma
							$first = false;
						$query_start .= $this->params['KEY'] . "_sortindex ";
						$query_end .= "'" . intval($maxindex + 1) . "'";
					}
					if ($this->params['TEXT']) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;
						$query_start .= $this->params['KEY'] . "_text_" . $LANG;
						$query_end .= "'" . addslashes($_REQUEST[$this->params['KEY'] . "_text_" . $LANG]) . "'";
					}

					for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;

						$query_start .= $this->params['KEY'] . "_add_numerics_" . $j;
						$query_end .= intval($_REQUEST[$this->params['KEY'] . "_add_numerics_" . $j]);
					}

					for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
						if (!$first) {
							$query_start .= ",";
							$query_end .= ",";
						} else //if not the first parameter added add a comma					
							$first = false;

						$query_start .= $this->params['KEY'] . "_add_strings_" . $j;
						$query_end .= "'" . addslashes($_REQUEST[$this->params['KEY'] . "_add_strings_" . $j]) . "'";
					}

					//inchide query-ul 
					$query_start .= ") ";
					$query_end .= ") ";
					//executa query-ul
					mysqli_query($connection, $query_start . $query_end) or die(mysqli_error($connection) . $query_start . $query_end);
					$ID = mysqli_insert_id($connection);


					if ($this->params['FILE']) {
						if ($this->params['MULTIFILE'])
							$nroffiles = $_REQUEST[$this->params['KEY'] . '_FILES_count'];
						else
							$nroffiles = 1;

						for ($i = 0; $i < $nroffiles; $i++) {
							$fileInputName = $this->params['KEY'] . "_file_" . $i;
							if (is_uploaded_file($_FILES[$fileInputName]["tmp_name"])) {
								//daca s-a uploadat fisier salveaza fisier

								$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "FILES");
								$NAME = $_REQUEST[$this->params['KEY'] . "_filename_" . $i];

								if ($this->params['FILEHASNAME'])
									$query_files = "INSERT INTO " . $this->params['KEY'] . "_files (" . $this->params['KEY'] . "_FILES_file," . $this->params['KEY'] . "_FILES_name," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $NAME . "','" . $ID . "')";
								else
									$query_files = "INSERT INTO " . $this->params['KEY'] . "_files (" . $this->params['KEY'] . "_FILES_file," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $ID . "')";
								mysqli_query($connection, $query_files) or die(mysqli_error($connection) . $query_files);
							}
						}
					}


					if ($this->params['MULTIPIC']) {
						$nrofpics = isset($_REQUEST[$this->params['KEY'] . '_IMGS_count']) ? $_REQUEST[$this->params['KEY'] . '_IMGS_count'] : 0;

						for ($i = 0; $i < $nrofpics; $i++) {
							$fileInputName = $this->params['KEY'] . "_pic_" . $i;
							if (is_uploaded_file($_FILES[$fileInputName]["tmp_name"])) {
								//daca s-a uploadat fisier									
								//salveaza fisier

								$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "IMGS");
								$NAME = $_REQUEST[$this->params['KEY'] . "_picname_" . $i];

								if ($this->params['FILEHASNAME'])
									$query_imgs = "INSERT INTO " . $this->params['KEY'] . "_imgs (" . $this->params['KEY'] . "_IMGS_pic," . $this->params['KEY'] . "_IMGS_name," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $NAME . "','" . $ID . "')";
								else
									$query_imgs = "INSERT INTO " . $this->params['KEY'] . "_imgs (" . $this->params['KEY'] . "_IMGS_pic," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $ID . "')";
								mysqli_query($connection, $query_imgs) or die(mysqli_error($connection) . $query_imgs);
								$ID2 = mysqli_insert_id($connection);

								$query_update = "UPDATE " . $this->params['KEY'] . " SET " . $this->params['KEY'] . "_pic = '" . $ID2 . "' WHERE " . $this->params['KEY'] . "_id ='" . $ID . "'";
								mysqli_query($connection, $query_update) or die(mysqli_error($connection) . $query_update);
							}
						}
					}
					//force display of new entry by setting value of request parameter
					$_REQUEST[$this->params['KEY'] . '_id'] = $ID;
				}
			} else //SALVARE MODIFICARE LA ELEMENT VECHI
			{
				$first = true;
				$query = "UPDATE " . $this->params['KEY'] . " SET ";



				if ($this->params['FILE']) {
					if ($this->params['MULTIFILE'])	//IF MULTIFILE, FILES WILL BE APPENDED
					{
						$nroffiles = $_REQUEST[$this->params['KEY'] . '_FILES_count'];
					} else	//IF NOT MULTIPIC, OLD FILE MUST BE DELETED IF EXISTS, AND ONLY ONE FILE IS SAVED
					{
						$fileInputName = $this->params['KEY'] . "_file_0";
						if (is_uploaded_file($_FILES[$fileInputName]["tmp_name"])) {
							$old = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_id = " . $_REQUEST[$this->params['KEY'] . '_id']) or die(mysqli_error($connection));
							$oldfile = mysqli_fetch_array($old);
							if (file_exists("../" . $oldfile[$this->params['KEY'] . '_FILES_file'])) {
								unlink("../" . $oldfile[$this->params['KEY'] . '_FILES_file']);
							}
							mysqli_query($connection, "DELETE FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_id = " . $_REQUEST[$this->params['KEY'] . '_id']) or die(mysqli_error($connection));
							$nroffiles = 1;
						} else {
							$nroffiles = 0;
						}
					}


					for ($i = 0; $i < $nroffiles; $i++) {
						$fileInputName = $this->params['KEY'] . "_file_" . $i;
						if (is_uploaded_file($_FILES[$fileInputName]["tmp_name"])) {
							//daca s-a uploadat fisier salveaza fisier

							$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "FILES");
							$NAME = $_REQUEST[$this->params['KEY'] . "_filename_" . $i];

							if ($this->params['FILEHASNAME'])
								$query_imgs = "INSERT INTO " . $this->params['KEY'] . "_files (" . $this->params['KEY'] . "_FILES_file," . $this->params['KEY'] . "_FILES_name," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $NAME . "','" . $_REQUEST[$this->params['KEY'] . '_id'] . "')";
							else
								$query_imgs = "INSERT INTO " . $this->params['KEY'] . "_files (" . $this->params['KEY'] . "_FILES_file," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $_REQUEST[$this->params['KEY'] . '_id'] . "')";
							mysqli_query($connection, $query_imgs) or die(mysqli_error($connection) . $query_imgs);
						}
					}
				}

				if ($this->params['PIC']) {
					if ($this->params['MULTIPIC']) {

						$nrofpics = $_REQUEST[$this->params['KEY'] . '_IMGS_count'];

						for ($i = 0; $i < $nrofpics; $i++) {
							$fileInputName = $this->params['KEY'] . "_pic_" . $i;
							if ($_FILES[$fileInputName]["tmp_name"]) {
								//daca s-a uploadat fisier									
								//salveaza fisier							 																

								$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "IMGS");

								$query_imgs = "INSERT INTO " . $this->params['KEY'] . "_imgs (" . $this->params['KEY'] . "_IMGS_pic," . $this->params['KEY'] . "_id) VALUES ('" . $IMG . "','" . $_REQUEST[$this->params['KEY'] . '_id'] . "')";
								mysqli_query($connection, $query_imgs) or die(mysqli_error($connection) . $query_imgs);
							}
						}
					} else {
						if ($this->params['PIC_islanguagedependent'])
							$fileInputName = $this->params['KEY'] . "_pic_" . $LANG;
						else
							$fileInputName = $this->params['KEY'] . "_pic";

						//salveaza poza si insereaza url-ul in query 
						if ($_FILES[$fileInputName]["tmp_name"]) {
							//daca s-a uploadat fisier								
							//salveaza fisier

							$IMG = SaveFile($_FILES[$fileInputName], $this->params['KEY'] . "IMGS");
						} else {
							$IMG = "";
						}
						//adauga la query
						if ($IMG != "") {
							if (!$first) {
								$query .= ",";
							} else //if not the first parameter added add a comma					
								$first = false;

							$data = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));
							$row = mysqli_fetch_array($data);

							if ($this->params['PIC_islanguagedependent']) {
								//adauga la query
								$query .= $this->params['KEY'] . "_pic_" . $LANG . "  = '" . $IMG . "'";
								//sterge fisierul vechi 
								if ($row[$this->params['KEY'] . '_pic_' . $LANG] != "")
									unlink("../" . $row[$this->params['KEY'] . '_pic_' . $LANG]);
							} else {
								//adauga la query
								$query .= $this->params['KEY'] . "_pic  = '" . $IMG . "'";

								//sterge fisierul vechi 
								if ($row[$this->params['KEY'] . '_pic'] != "")
									unlink("../" . $row[$this->params['KEY'] . '_pic']);
							}
						}
					}
				}
				if ($this->params['TITLE']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;
					$query .= $this->params['KEY'] . "_titlu_" . $LANG . "  = '" . addslashes($_REQUEST[$this->params['KEY'] . "_titlu_" . $LANG]) . "'";
				}
				if ($this->params['DATE']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;

					$TS = getTimeStamp($_REQUEST[$this->params['KEY'] . "_data"]); //FUNCTIE DIN FISIERUL INITTAB					
					$query .= $this->params['KEY'] . "_data = '" . $TS . "'";
				}
				if ($this->params['LINK']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;
					$query .= $this->params['KEY'] . "_link = '" . addslashes($_REQUEST[$this->params['KEY'] . "_link"]) . "'";
				}
				if ($this->params['ACCENT']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;
					if (isset($_REQUEST[$this->params['KEY'] . "_accent"]))
						$query .= $this->params['KEY'] . "_accent = '1'";
					else
						$query .= $this->params['KEY'] . "_accent = '0'";
				}
				if ($this->params['ZINDEX']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;

					$query .= $this->params['KEY'] . "_zindex = '" . $_REQUEST[$this->params['KEY'] . "_zindex"] . "'";
				}
				if ($this->params['SORTINDEX']) {
					if (isset($_REQUEST[$this->params['KEY'] . "_sortindex"])) {
						if (!$first) {
							$query .= ",";
						} else //if not the first parameter added add a comma
							$first = false;

						$query .= $this->params['KEY'] . "_sortindex = " . $_REQUEST[$this->params['KEY'] . "_sortindex"];
					}
				}
				if ($this->params['TEXT']) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;
					$query .= $this->params['KEY'] . "_text_" . $LANG . " = '" . addslashes($_REQUEST[$this->params['KEY'] . "_text_" . $LANG]) . "'";
				}


				for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;

					$query .= $this->params['KEY'] . "_add_numerics_" . $j . " = " . intval($_REQUEST[$this->params['KEY'] . "_add_numerics_" . $j]);
				}

				for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
					if (!$first) {
						$query .= ",";
					} else //if not the first parameter added add a comma					
						$first = false;

					$query .= $this->params['KEY'] . "_add_strings_" . $j . " = '" . addslashes($_REQUEST[$this->params['KEY'] . "_add_strings_" . $j]) . "'";
				}

				//inchide query-ul 
				$query .= " WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . "_id"] . "'";
				//executa query-ul				
				mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
			}
		}
	}
	function singleDisplay() //functie care afiseaza un singur element pentru editare
	{
		global $TEXTS;
		global $connection;

		$tpl = new Template();
		$LANG = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];

		$tpl->set('language', $LANG);
		$tpl->set('is_admin', ($_SESSION['admin_level']) ? true : false);

		//setam parametrii conditionali pentru afisare/neafisare anumite elemente din tpl
		$tpl->set('has_pic1', $this->params['PIC']);
		$tpl->set('has_pic2', $this->params['PIC']);

		$tpl->set('has_file1', $this->params['FILE']);
		$tpl->set('has_file2', $this->params['FILE']);

		$tpl->set('has_title', $this->params['TITLE']);
		$tpl->set('has_date', $this->params['DATE']);
		$tpl->set('has_link', $this->params['LINK']);
		$tpl->set('has_text', $this->params['TEXT']);
		$tpl->set('has_accent', $this->params['ACCENT']);
		$tpl->set('has_zindex', $this->params['ZINDEX']);
		$tpl->set('has_sortindex', $this->params['SORTINDEX']);

		$tpl->set('has_add_numerics', (count($this->params['ADD_NUMERICS']) > 0) ? true : false);
		$tpl->set('has_add_strings', (count($this->params['ADD_STRINGS']) > 0) ? true : false);

		$tpl->set('has_right', ($this->params['PIC'] || $this->params['FILE']));

		$tpl->set('needs_auxform', ($this->params['PIC'] || $this->params['FILE']));
		

		$tpl->set('title_iseditable', $this->params['TITLE_modif']);
		$tpl->set('date_iseditable', $this->params['DATE_modif']);
		$tpl->set('link_iseditable', $this->params['LINK_modif']);
		$tpl->set('text_iseditable', $this->params['TEXT_modif']);
		$tpl->set('multipic', $this->params['MULTIPIC']);
		$tpl->set('multifile', $this->params['MULTIFILE']);

		$tpl->set('pichasname', $this->params['PICHASNAME']);
		$tpl->set('pichasnameBOOL', ($this->params['PICHASNAME'] == true) ? 'true' : 'false');
		$tpl->set('filehasname', $this->params['FILEHASNAME']);
		$tpl->set('filehasnameBOOL', ($this->params['FILEHASNAME'] == true) ? 'true' : 'false');

		$tpl->set('submitSource', (isset($_REQUEST['submitSource'])) ? $_REQUEST['submitSource'] : (($this->params['PIC']) ?  1 : 3));
		$tpl->set('VIEW_HIDE_IMGS', (isset($_REQUEST['submitSource']) && ($_REQUEST['submitSource'] <= 2)) ? "VIEW" : "HIDE");

		if ($this->params['PIC'] == false) {
			$tpl->set('VIEW_HIDE_FILES', "VIEW");
		} else {
			if (isset($_REQUEST['submitSource'])) {
				$tpl->set('VIEW_HIDE_FILES', ($_REQUEST['submitSource'] <= 2) ? "HIDE" : "VIEW");
			} else {
				$tpl->set('VIEW_HIDE_FILES', "VIEW");
			}
		}

		//seteaza parametrii tin template

		//***************************************************************************lable-uri si texte statice
		if ($this->params['PIC']) {			
			$tpl->set('pic_isdeleteable', $this->params['PIC_del']);
			$tpl->set('pic_iseditable', $this->params['PIC_modif']);

			$tpl->set('lable_image', $TEXTS[$LANG]['lable_image']);
			$tpl->set('lable_images', $TEXTS[$LANG]['lable_images']);
			$tpl->set('auxform2_onsubmit', $TEXTS[$LANG]['auxform_onsubmit_1']);

			$tpl->set('auxform_onsubmit', $TEXTS[$LANG]['auxform_onsubmit_1']);
			$tpl->set('lable_addpic', $TEXTS[$LANG]['lable_addpic']);
			$tpl->set('lable_delpic', $TEXTS[$LANG]['lable_delpic']);
			$tpl->set('lable_defpic', $TEXTS[$LANG]['lable_defpic']);

			$tpl->set('auxform_id',	"DelPicFrom");
			$tpl->set('auxform_param1',	"delpic"); //parametru ce imi spune sa sterg poza
			$tpl->set('auxform2_param1', "delpic"); //parametru ce imi spune sa sterg poza		
			$tpl->set('auxform3_param1', "setdefpic"); //parametru ce imi spune sa sterg poza

		}
		if ($this->params['FILE']) {			
			$tpl->set('file_iseditable', $this->params['FILE_modif']);
			$tpl->set('file_isdeleteable', $this->params['FILE_del']);

			$tpl->set('lable_addfile', $TEXTS[$LANG]['lable_addfile']);
			$tpl->set('lable_file', $TEXTS[$LANG]['lable_file']);
			$tpl->set('lable_delfile', $TEXTS[$LANG]['lable_delfile']);
			$tpl->set('auxform4_param1', "delfile"); //parametru ce imi spune sa sterg fisier		 		
			$tpl->set('auxform4_onsubmit', $TEXTS[$LANG]['auxform_onsubmit_2']);
		}

		if ($this->params['TITLE'])
			$tpl->set('lable_title', $TEXTS[$LANG]['lable_title']);
		if ($this->params['DATE'])
			$tpl->set('lable_date', $TEXTS[$LANG]['lable_date']);
		if ($this->params['LINK'])
			$tpl->set('lable_link', $TEXTS[$LANG]['lable_link']);
		if ($this->params['ACCENT'])
			$tpl->set('lable_accent', $TEXTS[$LANG]['lable_accent']);
		if ($this->params['ZINDEX'])
			$tpl->set('lable_zindex', $TEXTS[$LANG]['lable_zindex']);
		if ($this->params['SORTINDEX'])
			$tpl->set('lable_sortindex', $TEXTS[$LANG]['lable_sortindex']);
		if ($this->params['TEXT'])
			$tpl->set('lable_text', $TEXTS[$LANG]['lable_text']);


		$tpl->set('submitbuttonlable', $TEXTS[$LANG]['submitbuttonlable']);

		$tpl->set('mainform_param1', "saveelement"); //parametru care spune sa salvez modificari la element existent		

		//***************************************************************************nume de inputuri folosite in form 

		if ($this->params['PIC']) {
			if ($this->params['PIC_islanguagedependent'])
				$tpl->set('imagefileINP', $this->params['KEY'] . "_pic_" . $LANG);
			else
				$tpl->set('imagefileINP', $this->params['KEY'] . "_pic");
		}
		if ($this->params['TITLE'])
			$tpl->set('titletextINP', $this->params['KEY'] . "_titlu_" . $LANG);
		if ($this->params['DATE'])
			$tpl->set('datetextINP', $this->params['KEY'] . "_data");
		if ($this->params['LINK'])
			$tpl->set('linktextINP', $this->params['KEY'] . "_link");
		if ($this->params['TEXT'])
			$tpl->set('texttextINP', $this->params['KEY'] . "_text_" . $LANG);
		if ($this->params['ACCENT'])
			$tpl->set('accentcheckINP', $this->params['KEY'] . "_accent");
		if ($this->params['ZINDEX'])
			$tpl->set('zindexselectINP', $this->params['KEY'] . "_zindex");
		if ($this->params['SORTINDEX']) {
			$tpl->set('sortindexname', $this->params['KEY'] . "_sortindex");
		}
		$tpl->set('key', $this->params['KEY']);
		$tpl->set('keyname', $this->params['KEY'] . "_id");


		//**************************************************************************variabile obtinute din baza de date	

		//get info of current
		$CURRENT_KEY = $_REQUEST[$this->params['KEY'] . '_id'];
		$query = "SELECT * FROM " . $this->params['KEY'] . " WHERE " . $this->params['KEY'] . "_id = '" . $CURRENT_KEY . "'";
		$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . "<br />" . $query);

		if (mysqli_num_rows($data) > 0) {
			//get total count of this type
			$query2 = "SELECT * FROM " . $this->params['KEY'] . " WHERE 1";
			$data2 = mysqli_query($connection, $query2) or die(mysqli_error($connection) . "<br />" . $query);

			$zindexarray = array();
			for ($i = 0; $i < mysqli_num_rows($data2); $i++) {
				$zindexarray[$i]['val'] = $i;
				$zindexarray[$i]['text'] = $i + 1;
				$zindexarray[$i]['selected'] = array();
			}

			$row = mysqli_fetch_array($data);
			if ($this->params['DATE']) {
				$date = getdate($row[$this->params['KEY'] . '_data']);
				$formattedDate = (($date['mday'] < 10) ? "0" . $date['mday'] : $date['mday']) . "/" . (($date['mon'] < 10) ? "0" . $date['mon'] : $date['mon']) . "/" . $date['year'] . " " . (($date["hours"] < 10) ? "0" . $date["hours"] : $date["hours"]) . ":" . (($date['minutes'] < 10) ? "0" . $date['minutes'] : $date['minutes']) . ":" . (($date['seconds'] < 10) ? "0" . $date['seconds'] : $date['seconds']);
				$tpl->set('datetextVAL', $formattedDate);
			}

			$tpl->set('keyvalue', $row[$this->params['KEY'] . '_id']);
			$arr = array();
			$arr2 = array();

			if ($this->params['FILE']) {
				$data2 = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . "_files WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "'") or die(mysqli_error($connection));

				while ($row2 = mysqli_fetch_array($data2)) {
					if ($this->params['FILEHASNAME']) {
						$filename = $row2[$this->params['KEY'] . '_FILES_name'];
						if ($filename == "")
							$filename_lable = $TEXTS[$LANG]['lable_noname'];
						else
							$filename_lable = $filename;
					} else
						$filename = "";

					$basename = basename($row2[$this->params['KEY'] . '_FILES_file']);
					$tokens = explode("\.", $basename);
					switch ($tokens[1]) {
						case "txt":
							$filepic = "graphics/fileicon_txt.jpg";
							break;
						case "doc":
							$filepic = "graphics/fileicon_doc.jpg";
							break;
						case "pdf":
							$filepic = "graphics/fileicon_pdf.jpg";
							break;
						case "ppt":
							$filepic = "graphics/fileicon_ppt.jpg";
							break;
						case "rtf":
							$filepic = "graphics/fileicon_rtf.jpg";
							break;
						case "jpg":
							$filepic = "graphics/fileicon_jpg.jpg";
							break;
						default:
							$filepic = "graphics/fileicon.jpg";
					}

					$arr2[count($arr2)] = array(
						"keyname" => $this->params['KEY'] . "_FILES_id",
						"namename" => $this->params['KEY'] . "_FILES_name",
						"keyvalue" => $row2[$this->params['KEY'] . "_FILES_id"],
						"filename" => $filename,
						"filename_lable" => $filename_lable,
						"filepath" => $basename,
						"filefullpath" => "../" . $row2[$this->params['KEY'] . '_FILES_file'],
						"filepic_src" => $filepic,
					);
				}
			}
			if ($this->params['MULTIPIC']) {
				$data2 = mysqli_query($connection, "SELECT * FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_id = '" . $_REQUEST[$this->params['KEY'] . '_id'] . "' ORDER BY " . $this->params['KEY'] . "_IMGS_id ASC") or die(mysqli_error($connection));

				while ($row2 = mysqli_fetch_array($data2)) {
					if ($row2[$this->params['KEY'] . '_IMGS_id'] == $row[$this->params['KEY'] . '_pic'])
						$selected = "selected";
					else
						$selected = "";

					if ($this->params['PICHASNAME'])
						$picname = $row2[$this->params['KEY'] . '_IMGS_name'];
					else
						$picname = "";


					$arr[count($arr)] = array(
						"pic_src" => "../" . $row2[$this->params['KEY'] . "_IMGS_pic"],
						"pic_rel_url" => SITE_BASE_DIR . $row2[$this->params['KEY'] . "_IMGS_pic"],
						"keyname" => $this->params['KEY'] . "_IMGS_id",
						"namename" => $this->params['KEY'] . "_IMGS_name",
						"keyvalue" => $row2[$this->params['KEY'] . "_IMGS_id"],
						"selected" => $selected,
						"picname" => $picname
					);
				}
			} else {
				if ($this->params['PIC']) {
					if ($this->params['PIC_islanguagedependent']) {
						if ($row[$this->params['KEY'] . '_pic_' . $LANG] == "") //EVEN IF PIC SHOULD BE DELETABLE, IF NO URL IS SPECIFIED DO NOT SHOW DEL BUTTON
						{
							$tpl->set('pic_src', $this->params['NOPICSRC']);
							$tpl->set('pic_isdeletable', FALSE);
						} else {
							$tpl->set('pic_src', "../" . $row[$this->params['KEY'] . '_pic_' . $LANG]);
							$tpl->set('pic_rel_url', SITE_BASE_DIR . $row[$this->params['KEY'] . '_pic_' . $LANG]);
						}
					} else {
						if ($row[$this->params['KEY'] . '_pic'] == "") //EVEN IF PIC SHOULD BE DELETABLE, IF NO URL IS SPECIFIED DO NOT SHOW DEL BUTTON
						{
							$tpl->set('pic_src', $this->params['NOPICSRC']);
							$tpl->set('pic_isdeletable', FALSE);
							$tpl->set('pic_rel_url', "");
						} else {
							$tpl->set('pic_src', "../" . $row[$this->params['KEY'] . '_pic']);
							$tpl->set('pic_rel_url', SITE_BASE_DIR . $row[$this->params['KEY'] . '_pic']);
						}
					}
				}
			}

			if ($this->params['ACCENT'] && $row[$this->params['KEY'] . '_accent'] == 1)
				$tpl->set('is_accented', true);
			else
				$tpl->set('is_accented', false);

			//make selected index-s selected array not null array
			if ($this->params['ZINDEX']) {
				$zindexarray[$row[$this->params['KEY'] . '_zindex']]['selected'][0] = array();
				$tpl->set('zindexes', $zindexarray);
			}

			if (count($arr) > 0)
				$tpl->set('images_null_text', "");
			else
				$tpl->set('images_null_text', $TEXTS[$LANG]['images_null_text']);

			if (count($arr2) > 0)
				$tpl->set('files_null_text', "");
			else
				$tpl->set('files_null_text', $TEXTS[$LANG]['files_null_text']);

			$tpl->set('images', $arr);
			$tpl->set('files', $arr2);

			if ($this->params['TITLE']) {
				$tpl->set('titletextVAL', $row[$this->params['KEY'] . '_titlu_' . $LANG]);
			}
			if ($this->params['LINK']) {
				$tpl->set('linktextVAL', $row[$this->params['KEY'] . '_link']);
			}
			if ($this->params['TEXT']) {
				$tpl->set('texttextVAL', stripslashes($row[$this->params['KEY'] . '_text_' . $LANG]));
			}


			$ADD_NUMERICS = array();
			$ADD_STRINGS = array();

			for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
				$ADD_NUMERICS[$j]['add_numericsINP'] = $this->params['KEY'] . '_add_numerics_' . $j;
				$ADD_NUMERICS[$j]['add_numericsVAR'] = $this->params['ADD_NUMERICS'][$j][$LANG];
				$ADD_NUMERICS[$j]['add_numericsVAL'] = 	$row[$this->params['KEY'] . '_add_numerics_' . $j];

				$ADD_NUMERICS[$j]['case'] =	($this->params['ADD_NUMERICS_modif'][$j][$_SESSION['admin_level']]) ? "editable" : "static";
			}

			for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
				$ADD_STRINGS[$j]['add_stringsINP'] = $this->params['KEY'] . '_add_strings_' . $j;
				$ADD_STRINGS[$j]['add_stringsVAR'] = $this->params['ADD_STRINGS'][$j][$LANG];
				$ADD_STRINGS[$j]['add_stringsVAL'] = stripslashes($row[$this->params['KEY'] . '_add_strings_' . $j]);

				$ADD_STRINGS[$j]['case'] = ($this->params['ADD_STRINGS_modif'][$j][$_SESSION['admin_level']]) ? "editable" : "static";
			}

			$tpl->set('add_numerics', $ADD_NUMERICS);
			$tpl->set('add_strings', $ADD_STRINGS);
		} else {
			//get total count of this type
			$query2 = "SELECT * FROM " . $this->params['KEY'] . " WHERE 1";
			$data2 = mysqli_query($connection, $query2) or die(mysqli_error($connection) . "<br />" . $query);

			$zindexarray = array();
			for ($i = 0; $i <= mysqli_num_rows($data2); $i++) {
				$zindexarray[$i]['val'] = $i;
				$zindexarray[$i]['text'] = $i + 1;
				$zindexarray[$i]['selected'] = array();
			}


			$tpl->set('images', array());
			$tpl->set('pic_isdeletable', false);
			$tpl->set('keyvalue', '-1');
			$tpl->set('pic_src', "");
			$tpl->set('titletextVAL', "");
			$tpl->set('linktextVAL', "");
			$tpl->set('is_accented', false);
			$tpl->set('zindexes', $zindexarray);


			$ADD_NUMERICS = array();
			$ADD_STRINGS = array();

			for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
				$ADD_NUMERICS[$j]['add_numericsINP'] = $this->params['KEY'] . '_add_numerics_' . $j;
				$ADD_NUMERICS[$j]['add_numericsVAR'] = $this->params['ADD_NUMERICS'][$j][$LANG];
				$ADD_NUMERICS[$j]['add_numericsVAL'] = 0;

				$ADD_NUMERICS[$j]['case'] =	($this->params['ADD_NUMERICS_modif'][$j][$_SESSION['admin_level']]) ? "editable" : "static";
			}

			for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
				$ADD_STRINGS[$j]['add_stringsINP'] = $this->params['KEY'] . '_add_strings_' . $j;
				$ADD_STRINGS[$j]['add_stringsVAR'] = $this->params['ADD_STRINGS'][$j][$LANG];
				$ADD_STRINGS[$j]['add_stringsVAL'] = "";

				$ADD_STRINGS[$j]['case'] = ($this->params['ADD_STRINGS_modif'][$j][$_SESSION['admin_level']]) ? "editable" : "static";
			}

			$tpl->set('add_numerics', $ADD_NUMERICS);
			$tpl->set('add_strings', $ADD_STRINGS);

			$date = getdate(time());
			$formattedDate = (($date['mday'] < 10) ? "0" . $date['mday'] : $date['mday']) . "/" . (($date['mon'] < 10) ? "0" . $date['mon'] : $date['mon']) . "/" . $date['year'] . " " . (($date["hours"] < 10) ? "0" . $date["hours"] : $date["hours"]) . ":" . (($date['minutes'] < 10) ? "0" . $date['minutes'] : $date['minutes']) . ":" . (($date['seconds'] < 10) ? "0" . $date['seconds'] : $date['seconds']);
			$tpl->set('datetextVAL', $formattedDate);
			$tpl->set('texttextVAL', "");

			$tpl->set('images_null_text', $TEXTS[$LANG]['images_null_text']);
			$tpl->set('files_null_text', $TEXTS[$LANG]['files_null_text']);
		}

		echo $tpl->fetch($this->params['SINGLE_template']);
	}

	function listDisplay() //functie ce afiseaza o lista de elemente, fiecare cu optiuni pentru editare si stergere
	{
		global $TEXTS;
		global $connection;

		$tpl = new Template();
		$LANG = $this->params['LANGUAGES'][$this->params['CURRENTLANGUAGE']];
		//setam parametrii conditionali pentru afisare/neafisare anumite elemente din tpl

		$tpl->set('language', $LANG);
		$tpl->set('is_admin', ($_SESSION['admin_level']) ? true : false);

		$tpl->set('has_title', $this->params['TITLE']);
		$tpl->set('has_text', $this->params['TEXT']);
		$tpl->set('has_date', $this->params['DATE']);
		$tpl->set('has_pic', $this->params['PIC']);

		$tpl->set('is_deletable', $this->params['DELETABLE']);
		$tpl->set('is_moveable', $this->params['MOVEABLE']);
		$tpl->set('is_sortable', $this->params['SORTINDEX']);
		$tpl->set('is_singular', $this->params['SINGULAR']);
		$tpl->set('is_appendable', $this->params['APPENDABLE']);

		//seteaza formul care este linkul spre pagina de adaugare element nou
		$tpl->set('addnew_text', $TEXTS[$LANG]['addnew_text']);
		$tpl->set('auxform_id', $this->params['KEY'] . "_addform");
		$tpl->set('auxform_param1', "add");
		$tpl->set('key', $this->params['KEY']);
		$tpl->set('keyname', $this->params['KEY'] . "_id");
		$tpl->set('keyvalue', "-1");


		//seteaza array-ul pentru loop-ul din template (calcul paginare)

		$fields_of_query = isset($this->params['QUERY_custom_field'])
			? ", " . $this->params['QUERY_custom_field']
			: "";

		if (isset($this->params['QUERY_custom_tail'])) {
			$query = "SELECT *$fields_of_query FROM " . $this->params['KEY'] . " WHERE 1 " . $this->params['QUERY_custom_tail'];
		} else {
			$query = "SELECT *$fields_of_query FROM " . $this->params['KEY'] . " WHERE 1 ORDER BY " . $this->params['KEY'] . "_" . $this->params['ORDER'] . " " . $this->params['ORDERDIR'];
		}

		$data = mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);



		$TOTALNR = mysqli_num_rows($data);
		$tpl->set('paging', $this->createPagingLinks($TOTALNR));   //PAGING CREATE

		//put first and last page links 
		if ($TOTALNR > $this->params['ELPERPAGE']) {
			$tpl->set('has_firstpage', true);
			$tpl->set('has_lastpage', true);

			$tpl->set('firstpage_text', "<");
			$tpl->set('lastpage_text', ">");


			$totalpages = intval($TOTALNR / $this->params['ELPERPAGE']);
			$totalpagesfloat = $TOTALNR / $this->params['ELPERPAGE'];
			if ($totalpages < $totalpagesfloat) {
				$totalpages++;
			}
			$LASTPAGE_NR = $totalpages - 1;
			$tpl->set('firstpage_href', "?page=" . $this->params['KEY'] . "&nrpage=1");
			$tpl->set('lastpage_href', "?page=" . $this->params['KEY'] . "&nrpage=" . $LASTPAGE_NR);
		} else {
			$tpl->set('has_firstpage', false);
			$tpl->set('has_lastpage', false);
		}


		$arr = array();
		$size = 0;
		$count = 0;

		//calculeaza limitele impuse de paginare
		if (isset($_REQUEST['nrpage']))
			$curpage = $_REQUEST['nrpage'];
		else
			$curpage = 1;
		$from = ($this->params['ELPERPAGE'] * ($curpage - 1));
		$to = min($this->params['ELPERPAGE'] * $curpage, $TOTALNR);


		while ($row = mysqli_fetch_array($data)) {
			if (($count >= $from) && ($count < $to)) //ADAUGA DOAR ACELE ELEMENTE CARE APARTIN INTERVALULUI DIN PAGINARE
			{
				$date = getdate(getOrZero($row, $this->params['KEY'] . '_data'));
				$formattedDate = (($date['mday'] < 10) ? "0" . $date['mday'] : $date['mday']) . "/" . (($date['mon'] < 10) ? "0" . $date['mon'] : $date['mon']) . "/" . $date['year'] . " " . (($date["hours"] < 10) ? "0" . $date["hours"] : $date["hours"]) . ":" . (($date['minutes'] < 10) ? "0" . $date['minutes'] : $date['minutes']) . ":" . (($date['seconds'] < 10) ? "0" . $date['seconds'] : $date['seconds']);

				if ($this->params['TEXT']) {
					if (strlen($row[$this->params['KEY'] . '_text_' . $LANG]) > 315)
						$text = strip_tags(substr($row[$this->params['KEY'] . '_text_' . $LANG], 0, 310)) . " ...";
					else
						$text = strip_tags($row[$this->params['KEY'] . '_text_' . $LANG]);
				} else {
					$text = "";
				}

				if ($this->params['TITLE']) {
					if (strlen($row[$this->params['KEY'] . '_titlu_' . $LANG]) > 30)
						$titlu = strip_tags(substr($row[$this->params['KEY'] . '_titlu_' . $LANG], 0, 25)) . " ...";
					else
						$titlu = strip_tags($row[$this->params['KEY'] . '_titlu_' . $LANG]);
				} else {
					$titlu = "";
				}

				if (isset($row[$this->params['KEY'] . '_add_numerics_0'])) {
					$numerics_0 = $row[$this->params['KEY'] . '_add_numerics_0'];
				}
				if (isset($row[$this->params['KEY'] . '_add_numerics_1'])) {
					$numerics_1 = $row[$this->params['KEY'] . '_add_numerics_1'];
				}
				if (isset($row[$this->params['KEY'] . '_add_numerics_2'])) {
					$numerics_2 = $row[$this->params['KEY'] . '_add_numerics_2'];
				}
				if (isset($row[$this->params['KEY'] . '_add_numerics_3'])) {
					$numerics_3 = $row[$this->params['KEY'] . '_add_numerics_3'];
				}

				$numerics = array();
				$strings = array();

				for ($j = 0; $j < count($this->params['ADD_NUMERICS']); $j++) {
					$numerics[$j] = $row[$this->params['KEY'] . '_add_numerics_' . $j];
				}

				for ($j = 0; $j < count($this->params['ADD_STRINGS']); $j++) {
					$strings[$j] =  $row[$this->params['KEY'] . '_add_strings_' . $j];
				}

				if (isset($row[$this->params['KEY'] . '_add_strings_0'])) {
					if (strlen($row[$this->params['KEY'] . '_titlu_' . $LANG]) > 30)
						$strings_0 = strip_tags(substr($row[$this->params['KEY'] . '_add_strings_0'], 0, 25)) . " ...";
					else
						$strings_0 = strip_tags($row[$this->params['KEY'] . '_add_strings_0']);
				}


				if ($this->params['MULTIPIC']) {
					$query_IMGS = "SELECT * FROM " . $this->params['KEY'] . "_imgs WHERE " . $this->params['KEY'] . "_IMGS_id = '" . $row[$this->params['KEY'] . '_pic'] . "'";
					$data_IMGS = mysqli_query($connection, $query_IMGS) or die(mysqli_error($connection) . $query_IMGS);
					$row_IMGS = mysqli_fetch_array($data_IMGS);
					if ($row_IMGS[$this->params['KEY'] . "_IMGS_pic"] != '-1')
						$pic = "../" . $row_IMGS[$this->params['KEY'] . "_IMGS_pic"];
					else
						$pic = $this->params['NOPICSRC'];
				} else {
					if ($this->params['PIC']) {
						if ($this->params['PIC_islanguagedependent']) {
							if ($row[$this->params['KEY'] . '_pic_' . $LANG] != '')
								$pic = "../" . $row[$this->params['KEY'] . '_pic_' . $LANG];
							else
								$pic = $this->params['NOPICSRC'];
						} else {
							if ($row[$this->params['KEY'] . '_pic'] != '')
								$pic = "../" . $row[$this->params['KEY'] . '_pic'];
							else
								$pic = $this->params['NOPICSRC'];
						}
					} else
						$pic = "";
				}
				$arr[$size] = array(
					"title" => $titlu,
					"text" => $text,
					"date" => $formattedDate,
					"pic" => $pic,
					"numerics_0" => isset($numerics_0) ? $numerics_0 : "",
					"numerics_1" => isset($numerics_1) ? $numerics_1 : "",
					"numerics_2" => isset($numerics_2) ? $numerics_2 : "",
					"numerics_3" => isset($numerics_3) ? $numerics_3 : "",
					"strings_0" => isset($strings_0) ? $strings_0 : "",
					"numerics" => $numerics,
					"strings" => $strings,
					"keyname" => $this->params['KEY'] . "_id",
					"keyvalue" => $row[$this->params['KEY'] . "_id"],
					"sortindexname" => $this->params['KEY'] . "_sortindex",
					"sortindexvalue" => getOrZero($row, $this->params['KEY'] . "_sortindex"),
					"op1_text" => $TEXTS[$LANG]['op1_text'],
					"op1_confirm" => $TEXTS[$LANG]['op1_confirm'] . " \'" . $this->params['KEY'] . "\'",
					"op1_param" => "del",
					"op2_param" => "up",
					"op3_param" => "down",
					"op4_param" => "upsort",
					"op5_param" => "downsort",
					"modi_text" => $TEXTS[$LANG]['modi_text'],
					"lable_pic" => $TEXTS[$LANG]['picture']
				);
				$size++;
			}
			$count++;
		}


		//adauga array-ul creat la template
		$tpl->set('listitems', $arr);

		//afiseaza template	
		echo $tpl->fetch($this->params['LIST_template']);
	}
	function createPagingLinks($totalnr)
	{
		global $connection;

		$paging = array();
		$size = 0;
		if (isset($_REQUEST['nrpage']))
			$curpage = $_REQUEST['nrpage'];
		else
			$curpage = 1;
		$totalpages = intval($totalnr / $this->params['ELPERPAGE']);
		$totalpagesfloat = $totalnr / $this->params['ELPERPAGE'];
		if ($totalpages < $totalpagesfloat) {
			$totalpages++;
		}
		$from = max(1, $curpage - $this->params['PAGESPAN']);
		$to = min($totalpages, $curpage + $this->params['PAGESPAN']);

		for ($i = $from; $i <= $to; $i++) {
			if ($i != $curpage) {
				$islink[0] = array(
					'href' => "?page=" . $this->params['KEY'] . "&nrpage=" . $i,
					'text' => "&nbsp;" . $i . "&nbsp;"
				);

				$isnotlink[0] = array();
			} else {
				$isnotlink[0] = array(
					'text' => "&nbsp;" . $i . "&nbsp;"
				);

				$islink[0] = array();
			}

			$paging[$size] = array(
				'islink' => $islink,
				'isnotlink' => $isnotlink
			);
			$size++;
		}
		if ($totalpages > 1)
			return 	$paging;
		else
			return "";
	}
}
