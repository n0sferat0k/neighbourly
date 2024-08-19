<?php 
$TEMPLATE_FILE_TAGS['childdiv_id'] = 'tag';
$TEMPLATE_FILE_TAGS['keyname'] = 'tag';
$TEMPLATE_FILE_TAGS['keyvalue'] = 'tag';
$TEMPLATE_FILE_TAGS['save_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['save_param2'] = 'tag';
$TEMPLATE_FILE_TAGS['textINP'] = 'tag';
$TEMPLATE_FILE_TAGS['textVAR'] = 'tag';
$TEMPLATE_FILE_TAGS['del_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['del_confirm'] = 'tag';
$TEMPLATE_FILE_TAGS['openasroot_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['addassocobj_page'] = 'tag';
$TEMPLATE_FILE_TAGS['addassocobj_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['childdiv_content'] = 'tag';
$TEMPLATE_FILE_TAGS['firstloop'] = 'if';
$TEMPLATE_FILE_TAGS['can_add_children'] = 'if';
$TEMPLATE_FILE_TAGS['text_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['is_deleteable'] = 'if';
$TEMPLATE_FILE_TAGS['has_children'] = 'if';
$TEMPLATE_FILE_TAGS['has_association'] = 'if';
$TEMPLATE_FILE_TAGS['exists'] = 'if';
if($DISPLAY_TEMPLATE_BODY) { ?>
<?php if($firstloop) { ?>
	<link rel="stylesheet" type="text/css" href="CSS/TPL_tree.css" />
<?php } else { ?>
<?php } ?>

<div class="DIV_tree_root">
	<?php if($can_add_children) { ?>	
		<form action="" method="post">		
			<div class="DIV_openchild_container" onclick="openclose('<?php echo $childdiv_id; ?>')"></div>
		</form>
	<?php } else { ?>
		<div class="DIV_noopenchild_container"></div>	
	<?php } ?>
	<?php if($text_iseditable) { ?>
		<form action="" method="post">
			<input type="hidden" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />
			<input type="hidden" name="param1" value="<?php echo $save_param1; ?>" />
			<input type="hidden" name="param2" value="<?php echo $save_param2; ?>" />
			<div class="DIV_input_container">
				<input type="text" name="<?php echo $textINP; ?>" value="<?php echo $textVAR; ?>" />
			</div>
			<div class="DIV_save_container" onclick="this.parentNode.submit()"></div>
		</form>
	<?php } else { ?>
		<div class="DIV_input_container">
			<?php echo $textVAR; ?>
		</div>
	<?php } ?>
	
	<?php if($is_deleteable) { ?>
		<form action="" method="post">
			<input type="hidden" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />
			<input type="hidden" name="param1" value="<?php echo $del_param1; ?>" />
			<div class="DIV_del_container" onclick="if(confirm('<?php echo $del_confirm; ?>'))this.parentNode.submit()"></div>
		</form>	
	<?php } else { ?>
	<?php } ?>
	
	<?php if($has_children) { ?>
		<form action="" method="post">
			<input type="hidden" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />
			<input type="hidden" name="param1" value="<?php echo $openasroot_param1; ?>" />		
			<div class="DIV_openasroot_container" onclick="this.parentNode.submit()"></div>
		</form>
	<?php } else { ?>
	<?php } ?>			
	<?php if($has_association) { ?>
		<form action="?page=<?php echo $addassocobj_page; ?>" method="post">
			<input type="hidden" name="param1" value="<?php echo $addassocobj_param1; ?>" />		
			<div class="DIV_addassocobj_container" onclick="this.parentNode.submit()"></div>
		</form>
	<?php } else { ?>
	<?php } ?>
</div>
<?php if($exists) { ?>
	<div class="DIV_tree_children" style="display:none" id="<?php echo $childdiv_id; ?>">
		<?php echo $childdiv_content; ?>
	</div>
<?php } else { ?>
<?php } ?>
<?php } ?>