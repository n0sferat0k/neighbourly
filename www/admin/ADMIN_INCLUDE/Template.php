<?php
	class Template 
	{
		// Configuration variables
		private $base_path = '';
		private $reset_vars = FALSE;
	
		// Internal variables
		private $tagvalues = array();
		private $lastreturned = "";
	
		public function __construct($base_path = NULL, $reset_vars = false) 
		{
			if($base_path) 
				$this->base_path = $base_path;
			$this->reset_vars = $reset_vars;
		}
	
		public function set($tag, $var) 
		{	 	
		 	$tag = str_replace(" ","_",trim($tag));
		 	$var = $this->eliminate_spaces($var);
			$this->tagvalues[$tag] = $var;
		}
		
		public function eliminate_spaces($var)
		{
		 	$new_var = null;
			if(is_array($var))	
			{
				foreach($var as $name => $value)
				{
					$name = str_replace(" ","_",trim($name));
					if(is_array($value))
						$new_var[$name] = $this->eliminate_spaces($value);
					else
						$new_var[$name] = $value;
				}
			}
			else
				return $var;
				
			return $new_var;
		}
		
		public function get($tag) 
		{		
			return $this->tagvalues[$tag];
		}
	
		public function reset_vars() 
		{
			$this->tagvalues = array();
		}
	
		public function fetch($file_name,$log = false) 
		{
			$file = $this->base_path . $file_name;
			if(!file_exists($file))
				return FALSE;			
											
			foreach($this->tagvalues as $var_name => $var_value)
			{			 		 
				$$var_name = $var_value;
			}			
			
			$DISPLAY_TEMPLATE_BODY = true;
			ob_start();
                            include($file);
                            $this->lastreturned = ob_get_contents();
			ob_end_clean();
			
			if($this->reset_vars == true)
				$this->tagvalues = array();
						
			return $this->lastreturned;
		}
	}
?>