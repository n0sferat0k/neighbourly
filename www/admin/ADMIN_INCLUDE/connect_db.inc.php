<?php 
		$DATABASE = "neighbourly"; //DO NOT DELETE - USED ELSEWHERE ALSO
		
		global $connection;
		$connection = mysqli_connect("localhost","root","") or die (mysqli_error($connection));
		mysqli_query($connection, "SET NAMES 'utf8'");
		mysqli_select_db($connection, $DATABASE) or die(mysqli_error($connection));
		mysqli_query($connection, 'USE ' . $DATABASE) or die(mysqli_error($connection));		

		function mysqli_field_name($result, $field_offset) {
			$properties = mysqli_fetch_field_direct($result, $field_offset);
			return is_object($properties) ? $properties->name : null;
		}		
?>