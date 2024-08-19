<?php	
	
	error_reporting(E_ALL);
	
	$TAGS_CONVERTED = array();
	
	if(isset($_REQUEST['upload']))
	{
	 	$name = $_FILES['file']['name'];
		$ret = move_uploaded_file($_FILES['file']['tmp_name'],$name);
		if($ret == false)
			$MSG = "UPLOAD FAILED, TRY AGAIN !";
		else
		{				
			$filename = split("\.",basename($name));
			$filename = $filename[0] . ".php";
			
			$content = file_get_contents($name);
			$content = transform_template($content);
			/*
			header("Content-type: application/force-download");
			header('Content-Disposition: inline; filename="' . $filename . '"');
			header("Content-Transfer-Encoding: Binary");
			header("Content-length: " . strlen($content));
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename="' . $filename .  '"');
			
	    	print('<textarea style="width:1200px;height:1200px;">' . $content . "</textarea>");
	    	*/
	    	die();
		}				
	}


	function recursive_convert($path)
	{	 	
		$dir = opendir($path);	
		while ($file = readdir($dir)) 
		{
		 
	        if((is_dir($file))&&($file != ".")&&($file != ".."))
	        {	         	
				recursive_convert($path . $file . "/");
			}
			
			else
			{
				$filerev = strrev(basename($file));
				$tokens = split("\.",$filerev,2);
				
		        $fileextention = strrev($tokens[0]);
				$filename = strrev($tokens[1]);
				
				if(($fileextention == "tpl")&&(!file_exists($path . $filename)))
				{
					$content = file_get_contents($path . $file);
					$content = transform_template($content);
		
					file_put_contents($path . $filename . ".php",$content);
					unlink($path . $file);
				}		
			}
		
	    }
	  
	}

	recursive_convert("./");	
		
	function transform_template($text)
	{
	 	global $TAGS_CONVERTED;
	 	
	 	$TAGS_CONVERTED = array();
	 	
	 	$text = handle_tags($text,false,false);
		$text = handle_ifs($text,false,false);		
		$text = handle_loops($text,false,false);
		$text = handle_cloops($text,false,false);		
		
		
		$final_text = "<?php \n";
		foreach($TAGS_CONVERTED as $name => $type)
		{
			$final_text .= "\$TEMPLATE_FILE_TAGS['" . $name . "'] = '" . $type . "';\n";
		}
		$final_text .="if(\$DISPLAY_TEMPLATE_BODY) { ?>\n";
		$final_text .=$text;	
		$final_text .="\n<?php } ?>";
				
		return $final_text;	
		
	}
	
	
	function handle_tags($text,$inside_loop = false,$inside_cloop = false)
	{
	 	global $TAGS_CONVERTED;

	 	if((!$inside_loop)&&(!$inside_cloop))
	 	{
			$tagbegin = "<tag:";
			$tagend = " />";		
	
			$NAMES = getTagNames("tag",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
				$TAG = "<tag:" . $name . " />";				
				$name = str_replace(" ","_",$name);
							
				$text = str_replace($TAG,"<?php echo $" . $name . "; ?>",$text);
			}
			$TAGS_CONVERTED = array_merge($TAGS_CONVERTED,$NAMES);
		}
	 	
		$loopname = ($inside_loop)?$inside_loop:$inside_cloop;
	 	$loopnames = split("\[\]\.",$loopname);
	 	
		if($loopname)
		{	
			$buildup = "$" . $loopnames[0] . "[$" . $loopnames[0] . "_counter]";
			for($i=1;$i<count($loopnames);$i++)
			{
				$buildup .= "['" . $loopnames[$i] . "'][$" . $loopnames[$i] . "_counter]";
			}
	
			$tagbegin = "<tag:" . $loopname ."[].";
			$tagend = " />";
			$NAMES = getTagNames("tag",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
				$TAG = "<tag:" . $loopname . "[]." . $name . " />";				
				$name = str_replace(" ","_",$name);
							
				$text = str_replace($TAG,"<?php echo " . $buildup . "['" . $name . "']; ?>",$text);
			}
			
			$TAG = "<tag:" . $loopname . "[] />";
			$text = str_replace($TAG,"<?php echo " . $buildup . "; ?>",$text);
		}
		
		return $text;
	}	
	
	function handle_ifs($text,$inside_loop = false,$inside_cloop = false)
	{
	 	global $TAGS_CONVERTED;
	 	
	 	$oldtext=$text;
	 	
	 	if((!$inside_loop)&&(!$inside_cloop))
	 	{
			$tagbegin = "<if:";
			$tagend = ">";					
	
			$NAMES = getTagNames("if",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
				$TAGHEAD = "<if:" . $name . ">";
				$TAGELSE = "<else:" . $name . ">";
				$TAGFOOT = "</if:" . $name . ">";	
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php if($" . $name . ") { ?>",$text);
				$text = str_replace($TAGELSE,"<?php } else { ?>",$text);
				$text = str_replace($TAGFOOT,"<?php } ?>",$text);				
			}	
			$TAGS_CONVERTED = array_merge($TAGS_CONVERTED,$NAMES);			
		}
		
		$loopname = ($inside_loop)?$inside_loop:$inside_cloop;
	 	$loopnames = split("\[\]\.",$loopname);
	 	
		if($loopname)
		{
			$tagbegin = "<if:" . $loopname . "[].";
			$tagend = ">";					
	
			$buildup = "$" . $loopnames[0] . "[$" . $loopnames[0] . "_counter]";
			for($i=1;$i<count($loopnames);$i++)
			{
				$buildup .= "['" . $loopnames[$i] . "'][$" . $loopnames[$i] . "_counter]";
			}
	
			$NAMES = getTagNames("if",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
				$TAGHEAD = "<if:" . $loopname . "[]." . $name . ">";
				$TAGELSE = "<else:" . $loopname . "[]." . $name . ">";
				$TAGFOOT = "</if:" . $loopname . "[]." . $name . ">";	
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php if(" . $buildup . "['" . $name . "']) { ?>",$text);
				$text = str_replace($TAGELSE,"<?php } else { ?>",$text);
				$text = str_replace($TAGFOOT,"<?php } ?>",$text);				
			}
			
			$TAGHEAD = "<if:" . $loopname . "[]>";
			$TAGELSE = "<else:" . $loopname . "[]>";
			$TAGFOOT = "</if:" . $loopname . "[]>";	
			
			$text = str_replace($TAGHEAD,"<?php if(" . $buildup . ") { ?>",$text);
			$text = str_replace($TAGELSE,"<?php } else { ?>",$text);
			$text = str_replace($TAGFOOT,"<?php } ?>",$text);				
		}
		return $text;
	}
	
	function handle_loops($text,$inside_loop = false,$inside_cloop = false)
	{
	 	global $TAGS_CONVERTED;
	 	
	 	$oldtext=$text;
	 	
	 	if((!$inside_loop)&&(!$inside_cloop))
	 	{
			$tagbegin = "<loop:";
			$tagend = ">";					
	
			$NAMES = getTagNames("loop",$tagbegin,$tagend,$text);
						
			foreach($NAMES as $name => $type)
			{
				$TAGHEAD = "<loop:" . $name . ">";
				$TAGFOOT = "</loop:" . $name . ">";	
				
				$text = handle_tags($text,$name,false);
				$text = handle_ifs($text,$name,false);		
				$text = handle_loops($text,$name,false);	
				$text = handle_cloops($text,$name,false);
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php if(isset($" . $name . "))for($" . $name . "_counter=0;$" . $name . "_counter < count($" . $name . ");$" . $name . "_counter ++) { ?>",$text);
				$text = str_replace($TAGFOOT,"<?php } ?>",$text);															
			}	
			$TAGS_CONVERTED = array_merge($TAGS_CONVERTED,$NAMES);			
		}
		
		$loopname = ($inside_loop)?$inside_loop:$inside_cloop;
	 	$loopnames = split("\[\]\.",$loopname);

		if($loopname)
		{
			$tagbegin = "<loop:" . $loopname . "[].";
			$tagend = ">";						
	
			$buildup = "$" . $loopnames[0] . "[$" . $loopnames[0] . "_counter]";
			for($i=1;$i<count($loopnames);$i++)
			{
				$buildup .= "['" . $loopnames[$i] . "'][$" . $loopnames[$i] . "_counter]";
			}
	
			$NAMES = getTagNames("loop",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
				$TAGHEAD = "<loop:" . $loopname . "[]." . $name . ">";
				$TAGFOOT = "</loop:" . $loopname . "[]." . $name . ">";	
				
				$text = handle_tags($text,$loopname . "[]." . $name,false);
				$text = handle_ifs($text,$loopname . "[]." . $name,false);		
				$text = handle_loops($text,$loopname . "[]." . $name,false);				
				$text = handle_cloops($text,$loopname . "[]." . $name,false);
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php if(isset(" . $buildup . "['" . $name . "']))for($" . $name . "_counter=0;$" . $name . "_counter < count(" . $buildup . "['" . $name . "']);$" . $name . "_counter ++) { ?>",$text);
				$text = str_replace($TAGFOOT,"<?php } ?>",$text);
			}				
		}
		return $text;
	}
	
	function handle_cloops($text,$inside_loop = false,$inside_cloop = false)
	{
	 	global $TAGS_CONVERTED;
	 	
	 	$oldtext=$text;
	 	
	 	if((!$inside_loop)&&(!$inside_cloop))
	 	{
			$tagbegin = "<cloop:";
			$tagend = ">";					
	
			$NAMES = getTagNames("cloop",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{	
				$text = handle_tags($text,false,$name);
				$text = handle_ifs($text,false,$name);		
				$text = handle_loops($text,false,$name);	
				$text = handle_cloops($text,false,$name);				
				
				$TAGHEAD = "<cloop:" . $name . ">";
				$TAGFOOT = "</cloop:" . $name . ">";				
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php for($" . $name . "_counter=0;$" . $name . "_counter < count($" . $name . ");$" . $name . "_counter ++) { switch($" . $name . "[$" . $name . "_counter]['case']){",$text);
				$text = str_replace($TAGFOOT,"}} ?>",$text);				

				$pos_S = strpos($text,$TAGHEAD);
				$pos_E = strpos($text,$TAGFOOT);
				
				$cloop_content =  substr($text,$pos_S + strlen($TAGHEAD),$pos_E - $pos_S - strlen($TAGHEAD));
				$tagbegin = "<case:";
				$tagend = ">";
				$CASENAMES = getTagNames("case",$tagbegin,$tagend,$cloop_content);
				
				foreach($CASENAMES as $casename => $casetype)
				{
				 	$TAGHEAD = "<case:" . $casename . ">";
					$TAGFOOT = "</case:" . $casename . ">";
							
					if($casename == "default")
					{
						$text = str_replace($TAGHEAD,"default: ?>",$text);
						$text = str_replace($TAGFOOT,"<?php",$text);
					}
					else
					{
						$text = str_replace($TAGHEAD,"case '" . $casename . "': ?>",$text);
						$text = str_replace($TAGFOOT,"<?php break;",$text);
					}
				}										
			}
			$TAGS_CONVERTED = array_merge($TAGS_CONVERTED,$NAMES);				
		}
		
		$loopname = ($inside_loop)?$inside_loop:$inside_cloop;
	 	$loopnames = split("\[\]\.",$loopname);

		if($loopname)
		{
			$tagbegin = "<cloop:" . $loopname . "[].";
			$tagend = ">";						
	
			$buildup = "$" . $loopnames[0] . "[$" . $loopnames[0] . "_counter]";
			for($i=1;$i<count($loopnames);$i++)
			{
				$buildup .= "['" . $loopnames[$i] . "'][$" . $loopnames[$i] . "_counter]";
			}
	
			$NAMES = getTagNames("cloop",$tagbegin,$tagend,$text);
			foreach($NAMES as $name => $type)
			{
			 
			 	$text = handle_tags($text,false,$loopname . "[]." . $name);
				$text = handle_ifs($text,false,$loopname . "[]." . $name);		
				$text = handle_loops($text,false,$loopname . "[]." . $name);		
				$text = handle_cloops($text,false,$loopname . "[]." . $name);
				
				$TAGHEAD = "<cloop:" . $loopname . "[]." . $name . ">";
				$TAGFOOT = "</cloop:" . $loopname . "[]." . $name . ">";	
				
				$name = str_replace(" ","_",$name);
				
				$text = str_replace($TAGHEAD,"<?php for($" . $name . "_counter=0;$" . $name . "_counter < count(" . $buildup  . "['" . $name . "']);$" . $name . "_counter ++) { switch(" . $buildup  . "['" . $name . "'][$" . $name . "_counter]['case']){",$text);
				$text = str_replace($TAGFOOT,"}} ?>",$text);				

				$pos_S = strpos($text,$TAGHEAD);
				$pos_E = strpos($text,$TAGFOOT);
				
				$cloop_content =  substr($text,$pos_S + strlen($TAGHEAD),$pos_E - $pos_S - strlen($TAGHEAD));
				$tagbegin = "<case:";
				$tagend = ">";
				$CASENAMES = getTagNames("case",$tagbegin,$tagend,$cloop_content);
				
				foreach($CASENAMES as $casename => $casetype)
				{
				 	$TAGHEAD = "<case:" . $casename . ">";
					$TAGFOOT = "</case:" . $casename . ">";
					
					if($casename == "default")
					{
						$text = str_replace($TAGHEAD,"default: ?>",$text);
						$text = str_replace($TAGFOOT,"<?php",$text);
					}
					else
					{
						$text = str_replace($TAGHEAD,"case '" . $casename . "': ?>",$text);
						$text = str_replace($TAGFOOT,"<?php break;",$text);
					}
				}						
			}				
		}
		return $text;
	}
	
	function getTagNames($type,$tagbegin,$tagend,$text)
	{	 	
		$pos = 0;
		$NAMES = array();

		$pos = strpos($text,$tagbegin,$pos);

		while(!(false === $pos))
		{
			$pos2 = strpos($text,$tagend,$pos);
			$start = $pos+strlen($tagbegin);
			$end = $pos2;
			$name = substr($text,$start,$end-$start);

			if((!strpos($name,"[") ) && (!strpos($name,"]") ) && (!strpos($name,".")))
				$NAMES[$name] = $type;
		
			$pos = $pos2 + strlen($tagend);
			
			$pos = strpos($text,$tagbegin,$pos);
		}
		
		return $NAMES;
	}

	
?>
<html>
	<head>
	</head>
	<body>
		<center>
			<form style="width:400px" action="" method="post" enctype="multipart/form-data">
				<fieldset>
	    			<legend>
						UPLOAD FILE
					</legend>			
					<label for="file_id" style="float:left;clear:left;width:100px;text-align:left">Template File</label>
					<input type="file" name="file" id="file_id" style="float:left;clear:right">					
					<input type="hidden" name="upload" value="1" />
					<input type="submit" value="upload" style="float:left;clear:left"/>
				</fieldset>
			</form>			
		</center>		
	</body>
</html>