<?php
	error_reporting(E_ERROR|E_WARNING|E_PARSE);	
    //error_reporting(E_ALL);
	include_once("../PHP_INCLUDE/CATEGORIES.php");	
	include_once("../PHP_INCLUDE/FUNCTIONS.php");
    include_once("../PHP_INCLUDE/EXPRESSIONS.php");
    include_once("../PHP_INCLUDE/LANGUAGES.php");
    include_once("../PHP_INCLUDE/PRODUCTS.php");
    include_once("../PHP_INCLUDE/ERROR.php");
		
	if(isset($_REQUEST['categories_cut_list']))
    {
        $_SESSION['categories_cut_list'] = $_REQUEST['categories_cut_list'];
        unset($_SESSION['categories_copy_list']);        
    }
    if(isset($_REQUEST['categories_copy_list']))
    {
        $_SESSION['categories_copy_list'] = $_REQUEST['categories_copy_list'];
        unset($_SESSION['categories_cut_list']);
    }            
    if(isset($_REQUEST['categories_del_list']))
    {
        $_SESSION['categories_del_list'] = $_REQUEST['categories_del_list'];
        if($_SESSION['categories_copy_list'] == $_SESSION['categories_del_list'])
            unset($_SESSION['categories_copy_list']);
        if($_SESSION['categories_cut_list'] == $_SESSION['categories_del_list'])
            unset($_SESSION['categories_cut_list']);
    }
        
    $id_list = $_REQUEST['categories_id_list'];
   	$id_array=getIdArrayFromIdList($id_list);	
	$id=getIdFromIdArray($id_array);
	
	$CATEGORY = new categories(null,$id);
    $CATEGORY->db_extract();	
      
    $ERROR = null;  
            
	switch($_REQUEST['cmd'])
	{
        case 'import_replace':                                
                                categories::import_from_xls($_FILES['import_file'],'import_replace');
                                break;
        case 'import_append':                                
                                categories::import_from_xls($_FILES['import_file'],'import_append');
                                break;
		case 'add_categ':
						$ERROR = categories::add_category($_REQUEST['id_string'],$id);
                        break;
		case 'paste_categ':                        
						categories::paste_category($id,$_SESSION['categories_cut_list'],$_SESSION['categories_copy_list']);
                        unset($_SESSION['categories_cut_list']);      
                        unset($_SESSION['categories_copy_list']);						
						break;
		case 'del_categ':
						categories::del_category($_SESSION['categories_del_list'],$_REQUEST['del_type']);
                        unset($_SESSION['categories_del_list']);
                        break;
        case 'rename':
                        $expression = new expressions($_REQUEST['id_string']); 
                        $expression->db_extract_languages();
                        
                        foreach($expression->values as $LANG => $VAL)
                        {
                            $expression->values[$LANG] = $_REQUEST[$LANG];
                        }
                        $expression->db_inject();
                        break;                
                        
		default:;
	}
        
    $cut_list = $_SESSION['categories_cut_list'];      
    $copy_list = $_SESSION['categories_copy_list'];
    $del_list = $_SESSION['categories_del_list'];    

	$cut_array=getIdArrayFromIdList($cut_list);
	$cut=getIdFromIdArray($cut_array);	
	$copy_array=getIdArrayFromIdList($copy_list);
	$copy=getIdFromIdArray($copy_array);
	$del_array=getIdArrayFromIdList($del_list);
	$del=getIdFromIdArray($del_array);
?>

<link rel="stylesheet" type="text/css" href="CSS/categorii.css" />
<script language="javascript" type="text/javascript" src="javascripts/ABC_hashlist.js"></script>
<script language="javascript" type="text/javascript" src="javascripts/POPUP_V9.js"></script>
<script language="javascript" type="text/javascript" src="javascripts/GENERAL.js"></script>
<?php
    if($ERROR instanceof error)
        $ERROR->display();
?> 

<div class="NAVIGATOR">
	<?php
		$url = "";
		for($i=1;$i<count($id_array);$i++)
		{
    	 	$url.="-" . $id_array[$i];
            $cat = new categories(null,$id_array[$i]);
            if($cat->db_extract())
            {
            	echo '<a class="navlink" href="?page=' . $PAGE_KEY . '&categories_id_list=' . $url . '">' . (($cat->expression->values[$PAGE_LANGUAGE])?$cat->expression->values[$PAGE_LANGUAGE]:$cat->id_string) . '&nbsp;&raquo;&nbsp; </a>';
            }
		}
	?>	
</div>

<div class="option_bar">
	<div class="option option_new" onclick="createIdSourcedPopupFromObject('newfolder_dialog',this)">	
		Categorie Noua
	</div>
    <div class="option option_new" onclick="createIdSourcedPopupFromObject('import_dialog',this)">	
		Importa Excel
	</div>
	<?php 
		if((isset($copy_list))||(isset($cut_list)))
		{
			?>
				<form action="" method="POST">
				    <input type="hidden" name="cmd" value="paste_categ" />
					<div class="option option_paste" onclick="this.parentNode.submit()">	
						Categorie Paste
					</div>
				</form>
			<?php
		}        		
	?>
</div>

<div class="general_dialog" id="newfolder_dialog" style="display:none">
	<form action="" method="post">
		<div class="general_dialog_lable">Nume de identificare (engleza):</div>
		<input type="hidden" name="cmd" value="add_categ" />
		<div class="general_dialog_input_CONT">
            <input class="general_dialog_input" type="text" name="id_string" />
        </div>
		<div class="opcontainer">
			<div class="general_dialog_buttonOK" onclick="this.parentNode.parentNode.submit()">
				Salveaza
			</div>
			<div class="general_dialog_buttonCANCEL" onclick="destroyPopup()">
				Anuleaza
			</div>		
		</div>
	</form>
</div>

<div class="general_dialog" id="import_dialog" style="display:none">
	<form action="" method="post" enctype="multipart/form-data">
		<div class="general_dialog_lable">Fisier xls:</div>
		<input type="hidden" name="cmd" value="" id="import_cmd"/>
		<div class="general_dialog_input_CONT">
            <input class="general_dialog_input" type="file" name="import_file" />
        </div>    
		<div class="opcontainer">
			<div class="general_dialog_buttonOK" onclick="document.getElementById('import_cmd').value='import_replace';this.parentNode.parentNode.submit()">
				Inlocuire
			</div>
            <div class="general_dialog_buttonOK" onclick="document.getElementById('import_cmd').value='import_append';this.parentNode.parentNode.submit()">
				Adaugare
			</div>
			<div class="general_dialog_buttonCANCEL" onclick="destroyPopup()">
				Anuleaza
			</div>		
		</div>
	</form>
</div>

<div class="ABC_hash_list_filter_container">
	<div class="ABC_hash_list_filter_lable">
		Filtru de cautare:
	</div>
	<input type="text" class="ABC_hash_list_filter" onkeyup="HL.filterContent(this.value)"/>
</div>

<div class="ABC_hash_list_container" id="ABC_hash_list_container">
</div>

<script language="javascript" type="text/javascript">   
	init_ABC_hash_list('ABC_hash_list_container');
	
	function showContextMenu(id,obj)
	{		
        destroyPopup();
        initPopupForSingleDelayedMouseoutDisengage(); 
		createIdSourcedPopupFromObject(id,obj);
        forceStartDelayedMouseoutDestroy();
        return false;         
	}
</script>

<?php 

    $CURRENT_CATEGORIES = categories::get_children_by_parent_id($CATEGORY->id);
    
	$JS="";
	$HTML="";
           
	foreach($CURRENT_CATEGORIES as $CATEG)
	{         
        $JS.=  "add_element_to_ABC_hash_list(" . $CATEG->id . ",'" . (($CATEG->expression->values[$PAGE_LANGUAGE])?$CATEG->expression->values[$PAGE_LANGUAGE]:$CATEG->id_string) . "','entry_id_" . $CATEG->id . "');";
        
        if($cut==$CATEG->id)
        {
            $HTML.=	'<div class="ABC_hash_list_entry_transp" id="entry_id_' . $CATEG->id . '" >
    						<div class="ABC_hash_list_text">	
                                <span class="ABC_hash_list_link" onContextMenu="return showContextMenu(\'context_menu_' . $CATEG->id .  '\',this);">						
    							     ' . (($CATEG->expression->values[$PAGE_LANGUAGE])?$CATEG->expression->values[$PAGE_LANGUAGE]:$CATEG->id_string) . '
                                </span>
    						</div>						
    					</div>
                        <div class="context_menu" id="context_menu_' . $CATEG->id .  '">
    						<div class="CM_option_pos"><form action="" method="post"><input type="hidden" name="categories_copy_list" value="' . $id_list . '-' . $CATEG->id . '" /> <span class="CM_option_link" onclick="this.parentNode.submit()">Copy</span></form></div>    						
    						<div class="CM_option_pos"><span class="CM_option_link" onclick="initPopupForSingleDelayedMouseoutDisengage();createIdSourcedPopupFromId(\'delete_' . $CATEG->id . '\',\'entry_' . $CATEG->id . '\')">Delete</span></div>
                            <div class="CM_option_pos"><span class="CM_option_link" onclick="initPopupForSingleDelayedMouseoutDisengage();createIdSourcedPopupFromId(\'rename_' . $CATEG->id . '\',\'entry_' . $CATEG->id . '\')">Rename</span></div>
       					</div>';
        }
        else
        {
            $HTML.=	'<div class="ABC_hash_list_entry" id="entry_id_' . $CATEG->id . '" >
    						<div class="ABC_hash_list_text">
    							<a class="ABC_hash_list_link" onContextMenu="return showContextMenu(\'context_menu_' . $CATEG->id .  '\',this); return false;" id="entry_' . $CATEG->id .  '" href="?page=' . $PAGE_KEY . '&categories_id_list=' . $id_list . '-' . $CATEG->id . '" >
    								' . (($CATEG->expression->values[$PAGE_LANGUAGE])?$CATEG->expression->values[$PAGE_LANGUAGE]:$CATEG->id_string) . ' 																
    							</a>
    						</div>						
    					</div>
                        <div class="context_menu" id="context_menu_' . $CATEG->id .  '">
    						<div class="CM_option_pos"><form action="" method="post"><input type="hidden" name="categories_copy_list" value="' . $id_list . '-' . $CATEG->id . '" /> <span class="CM_option_link" onclick="this.parentNode.submit()">Copy</span></form></div>
    						<div class="CM_option_pos"><form action="" method="post"><input type="hidden" name="categories_cut_list" value="' . $id_list . '-' . $CATEG->id . '" /><span class="CM_option_link" onclick="this.parentNode.submit()">Cut</span></form></div>
    						<div class="CM_option_pos"><span class="CM_option_link" onclick="createIdSourcedPopupFromId(\'delete_' . $CATEG->id . '\',\'entry_' . $CATEG->id . '\')">Delete</span></div>
                            <div class="CM_option_pos"><span class="CM_option_link" onclick="createIdSourcedPopupFromId(\'rename_' . $CATEG->id . '\',\'entry_' . $CATEG->id . '\')">Rename</span></div>                            
    					</div>';
        } 
        
        
                      
        $HTML.=     '<div class="popup_container" id="rename_' . $CATEG->id . '">
                        <div class="popup_title">' . $CATEG->id_string . ':</div>
                        <form action="" method="post">
                            <input type="hidden" name="cmd" value="rename" />
                            <input type="hidden" name="id_string" value="' . $CATEG->id_string . '" />
                            ';          
                                
                                foreach($CATEG->expression->values as $LANG => $VAL)
                                {
                                    $HTML .='<div class="popup_entry"><div class="popup_lable">' . $LANG . ':</div><input class="popup_input" type="text" name="' . $LANG . '" value="' . $VAL . '" /></div>';
                                }
                            
                                                           
        $HTML.=            '<div class="popup_entry"><input type="submit" class="popup_button" value="Salveaza" /></div>
                            <div class="popup_entry"><input type="button" class="popup_button" value="Anuleaza" onclick="destroyPopup()"/></div>
                        </form>
                    </div>
                    ';
            
        if(count($CATEG->parents_id) > 1)
        {
            $HTML.='<div class="popup_container" id="delete_' . $CATEG->id . '">
                        <div class="popup_title">Aceasta categorie mai apare si in alta parte, prin aceasta stergere categoria va ramane in sistem alaturi de toate produsele care ii apartin</div>
                        <form action="" method="post">
                            <input type="hidden" name="cmd" value="del_categ" />                                
                            <input type="hidden" name="del_type" value="unlink" />
                            <input type="hidden" name="categories_del_list" value="' . $id_list . '-' . $CATEG->id . '" />
                                                        
                            <div class="popup_entry">
                                <input type="submit" class="popup_button" value="Sterge" />
                                <input type="button" class="popup_button" value="Anuleaza" onclick="destroyPopup()" />
                            </div>
                        </form>
                    </div>';
        }
        else
        {
            if(($CATEG->nr_of_products > 0) || (count($CATEG->children_id) > 0))
            {
                $HTML.='<div class="popup_container" id="delete_' . $CATEG->id . '">
                            <div class="popup_title">Aceasta are produse si/sau subcategorii si apare doar aici, doriti sa se stearga tot</div>
                            <form action="" method="post">
                                <input type="hidden" name="cmd" value="del_categ" />                                
                                <input type="hidden" name="del_type" value="purge" />
                                <input type="hidden" name="categories_del_list" value="' . $id_list . '-' . $CATEG->id . '" />
                                
                                <div class="popup_entry">
                                    <input type="submit" class="popup_button_wide" value="Sterge tot" />                                        
                                </div>
                            </form>
                            <div class="popup_title">, doriti sa retineti produsele si/sau subcategoriile in afara categoriei si stergeti doar categoria</div>
                            <form action="" method="post">
                                <input type="hidden" name="cmd" value="del_categ" />                                
                                <input type="hidden" name="del_type" value="empty" />
                                <input type="hidden" name="categories_del_list" value="' . $id_list . '-' . $CATEG->id . '" />
                                
                                <div class="popup_entry">
                                    <input type="submit" class="popup_button_wide" value="Sterge categoria" />                                        
                                </div>
                            </form>
                            <div class="popup_title">Sau doriti stergeti categoria si sa explodati continutul</div>
                            <form action="" method="post">
                                <input type="hidden" name="cmd" value="del_categ" />                                
                                <input type="hidden" name="del_type" value="explode" />
                                <input type="hidden" name="categories_del_list" value="' . $id_list . '-' . $CATEG->id . '" />
                                
                                <div class="popup_entry">
                                    <input type="submit" class="popup_button_wide" value="Sterge si explodeaza" />                                        
                                </div>
                            </form>
                            <div class="popup_title">Pentru anulare apasati aici:</div>
                            <div class="popup_entry">
                                <input type="button" class="popup_button_wide" value="Anuleaza" onclick="destroyPopup()" />
                            </div>    
                        </div>
                    ';
            }
            else
            {
                $HTML.='<div class="popup_container" id="delete_' . $CATEG->id . '">
                        <div class="popup_title">Aceasta categorie nu are nici produse nici subcategorii si apare doar aici, doriti sa se stearga permanent</div>
                        <form action="" method="post">
                            <input type="hidden" name="cmd" value="del_categ" />                                
                            <input type="hidden" name="del_type" value="purge" />
                            <input type="hidden" name="categories_del_list" value="' . $id_list . '-' . $CATEG->id . '" />
                            
                            <div class="popup_entry">
                                <input type="submit" class="popup_button" value="Sterge" />
                                <input type="button" class="popup_button" value="Anuleaza" onclick="destroyPopup()" />
                            </div>
                        </form>
                    </div>';
            }                               
        }                      
	}	    	    
?>

<?php echo $HTML;?>

<script language="javascript" type="text/javascript">
	<?php echo $JS;?>
	HL.dumpContent(ABC_hash_list_container);
</script>