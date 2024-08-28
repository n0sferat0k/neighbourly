<?php
if (!defined("IN_ADMIN"))
	die("HACKING IS FUN!");


//CHANGE THE ID IF AN ELEMENT IN A TABLE, THE ID FIELD MUST HAVE THA NAME tablename_id (also adjusts foreign references with help of table masterslave)
function table_exists($TABLE)
{
	global $DATABASE;
	$tables = mysqli_query($connection, "SHOW TABLES FROM " . $DATABASE) or die(mysqli_error($connection));
	while ($row = mysqli_fetch_array($tables)) {
		if (strtolower($TABLE) == strtolower($row["Tables_in_" . $DATABASE]))
			return true;
	}
	return false;
}
function changeID($TABLE, $ID_OLDVALUE, $ID_NEWVALUE)
{
	changeField($TABLE, $TABLE . "_id", $ID_OLDVALUE, $ID_NEWVALUE);
	if (table_exists("masterslave")) {
		$data = mysqli_query($connection, "SELECT * from masterslave WHERE master = '" . $TABLE . "'") or die(mysqli_error($connection));
		while ($row = mysqli_fetch_array($data)) {
			mysqli_query($connection, "UPDATE " . $row['slave'] . " SET " . $TABLE . "_id = " . $ID_NEWVALUE . " WHERE " . $TABLE . "_id = " . $ID_OLDVALUE) or die(mysqli_error($connection));
		}
	}
}

function changeField($TABLE, $FIELDNAME, $ID_OLDVALUE, $ID_NEWVALUE)
{
	$query = "UPDATE " . $TABLE . " SET " . $FIELDNAME . " = " . $ID_NEWVALUE . " WHERE " . $FIELDNAME . " = " . $ID_OLDVALUE;
	mysqli_query($connection, $query) or die(mysqli_error($connection) . $query);
}

//MOVE ONE ELEMENT UP OR DOWN BY ONE POSITION IN THE ID ORDER, THE ID FIELD MUST HAVE THA NAME tablename_id (also adjusts foreign references with help of table masterslave)
function moveInTable($TABLE, $ID_VALUE, $DIR)
{
	switch ($DIR) {
		case 'UP':
			$data = mysqli_query($connection, "SELECT * FROM " . $TABLE . " WHERE " . $TABLE . "_id < " . $ID_VALUE . " ORDER BY " . $TABLE . "_id DESC LIMIT 1") or die(mysqli_error($connection));
			if (mysqli_num_rows($data) > 0) //there is at least 1 element with a smaller ID => perform element switch 
			{
				$ELEMENT = mysqli_fetch_array($data);
				$TMP = $ELEMENT[$TABLE . "_id"];
				changeID($TABLE, $TMP, -1);
				changeID($TABLE, $ID_VALUE, $TMP);
				changeID($TABLE, -1, $ID_VALUE);
			}
			break;
		case 'DOWN':
			$data = mysqli_query($connection, "SELECT * FROM " . $TABLE . " WHERE " . $TABLE . "_id > " . $ID_VALUE . " ORDER BY " . $TABLE . "_id ASC LIMIT 1") or die(mysqli_error($connection));
			if (mysqli_num_rows($data) > 0) //there is at least 1 element with a smaller ID => perform element switch 
			{
				$ELEMENT = mysqli_fetch_array($data);
				$TMP = $ELEMENT[$TABLE . "_id"];
				changeID($TABLE, $TMP, -1);
				changeID($TABLE, $ID_VALUE, $TMP);
				changeID($TABLE, -1, $ID_VALUE);;
			}
			break;
		default:;
	}
}

//MOVE ONE ELEMENT UP OR DOWN BY ONE POSITION IN THE SORTINDEX ORDER, THE SORTINDEX FIELD MUST HAVE THE NAME tablename_sortindex
function moveInTableBySortIndex($TABLE, $SORTINDEX_VALUE, $DIR)
{
	switch ($DIR) {
		case 'UP':
			$data = mysqli_query($connection, "SELECT * FROM " . $TABLE . " WHERE " . $TABLE . "_sortindex > " . $SORTINDEX_VALUE . " ORDER BY " . $TABLE . "_sortindex ASC LIMIT 1") or die(mysqli_error($connection));
			if (mysqli_num_rows($data) > 0) //there is at least 1 element with a smaller ID => perform element switch
			{
				$ELEMENT = mysqli_fetch_array($data);
				$TMP = $ELEMENT[$TABLE . "_sortindex"];
				changeField($TABLE, $TABLE . "_sortindex", $TMP, -1);
				changeField($TABLE, $TABLE . "_sortindex", $SORTINDEX_VALUE, $TMP);
				changeField($TABLE, $TABLE . "_sortindex", -1, $SORTINDEX_VALUE);;
			}
			break;
		case 'DOWN':
			$data = mysqli_query($connection, "SELECT * FROM " . $TABLE . " WHERE " . $TABLE . "_sortindex < " . $SORTINDEX_VALUE . " ORDER BY " . $TABLE . "_sortindex DESC LIMIT 1") or die(mysqli_error($connection));
			if (mysqli_num_rows($data) > 0) //there is at least 1 element with a smaller ID => perform element switch
			{
				$ELEMENT = mysqli_fetch_array($data);
				$TMP = $ELEMENT[$TABLE . "_sortindex"];
				changeField($TABLE, $TABLE . "_sortindex", $TMP, -1);
				changeField($TABLE, $TABLE . "_sortindex", $SORTINDEX_VALUE, $TMP);
				changeField($TABLE, $TABLE . "_sortindex", -1, $SORTINDEX_VALUE);
			}
			break;
		default:;
	}
}

//DELETE AN ELEMENT FROM THE TABLE AND ALL ELEMENTS FROM SUBORDONATE TABLES LINKED TO THIS ELEMENT BY A FOREIGN KEY
function recursiveDelete($TABLE, $ID_VALUE)
{
	$d = mysqli_query($connection, "SELECT * FROM " . $TABLE . " WHERE " . $TABLE . "_id = " . $ID_VALUE) or die(mysqli_error($connection));
	$ELEMENT = mysqli_fetch_array($d);

	$data = mysqli_query($connection, "SELECT * FROM masterslave WHERE master = '" . $TABLE . "'") or die(mysqli_error($connection));
	while ($row = mysqli_fetch_array($data)) {
		$data2 = mysqli_query($connection, "SELECT * FROM " . $row['slave'] . " WHERE " . $TABLE . "_id = " . $ELEMENT[$TABLE . "_id"]) or die(mysqli_error($connection));
		while ($row2 = mysqli_fetch_array($data2)) {
			recursiveDelete($row['slave'], $row2[$row['slave'] . '_id']);
		}
	}
	if (isset($ELEMENT[$TABLE . '_poza']))
		unlink("../" . $ELEMENT[$TABLE . '_poza']);
	mysqli_query($connection, "DELETE FROM " . $TABLE . " WHERE " . $TABLE . "_id = " . $ID_VALUE) or die(mysqli_error($connection));
}
//SAVE AN UPLOADED FILE, IF OTHER FILE WITH SAME NAME EXISTS RENAME CURRENT FILE TO filename_1.extension, or filename_2.extension or ...
function SaveFile($fstruct, $dir)
{
	$tokens = explode(".", basename($fstruct['name']));
	
	$FILE_NAME = str_replace(" ", "_", $tokens[0]);
	$FILE_EXT = $tokens[1];

	$DEST_1 = $dir . "/" . $FILE_NAME;
	$DEST_2 = "../" . $DEST_1 . "." . $FILE_EXT;
	$RET = $DEST_1 . "." . $FILE_EXT;
	$i = 1;
	while (file_exists($DEST_2)) {
		$DEST_2 = "../" . $DEST_1 . "_" . $i . "." . $FILE_EXT;
		$RET = $DEST_1 . "_" . $i . "." . $FILE_EXT;
		$i++;
	}

	move_uploaded_file($fstruct['tmp_name'], $DEST_2);
	chmod($DEST_2, 0777);

	return $RET;
}

function getTimeStamp($str)
{
	$d1 = explode("/", $str);	
	$day = trim($d1[0]);
	$month = trim($d1[1]);
	$d2 = explode(" ", $d1[2]);
	$year = trim($d2[0]);
	$d3 = explode(":", $d2[1]);
	$hour = trim($d3[0]);
	$minute = trim($d3[1]);
	$second = trim($d3[2]);

	if ((is_numeric($hour)) && (is_numeric($minute)) && (is_numeric($second)) && (is_numeric($month)) && (is_numeric($day)) && (is_numeric($year)))
		$ts = mktime($hour, $minute, $second, $month, $day, $year);
	else
		$ts = strtotime($month . "/" . $day . "/" . $year);
		
	return $ts;
}

//CREATE A DATE FORMAT STRING FROM A TIMESTAMP
function formatDate($TS, $type)
{
	$date = getdate($TS);

	switch ($type) {
		case "type1":
			return "[" . $date['mday'] . "." . $date['mon'] . "." . $date['year'] . "]";
		case "type2":
			return $date['mday'] . "." . $date['mon'] . "." . $date['year'];
		default:
			return "Unknown time format";
	}
}

function countOrZero($target)
{
	return isset($target) && is_countable($target) ? count($target) : 0;
}

function getOrEmpty($target, $key)
{
	return isset($target[$key]) ? $target[$key] : "";
}

function getOrZero($target, $key)
{
	return isset($target[$key]) ? $target[$key] : 0;
}

function isRequestParamEqualTo($param, $value)
{
	return isset($_REQUEST[$param]) && $_REQUEST[$param] == $value;
}
