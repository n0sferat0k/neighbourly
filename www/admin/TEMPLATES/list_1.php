<?php 
$TEMPLATE_FILE_TAGS['firstpage_href'] = 'tag';
$TEMPLATE_FILE_TAGS['firstpage_text'] = 'tag';
$TEMPLATE_FILE_TAGS['lastpage_href'] = 'tag';
$TEMPLATE_FILE_TAGS['lastpage_text'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform_id'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['keyname'] = 'tag';
$TEMPLATE_FILE_TAGS['keyvalue'] = 'tag';
$TEMPLATE_FILE_TAGS['addnew_text'] = 'tag';
$TEMPLATE_FILE_TAGS['has_firstpage'] = 'if';
$TEMPLATE_FILE_TAGS['has_lastpage'] = 'if';
$TEMPLATE_FILE_TAGS['is_singular'] = 'if';
$TEMPLATE_FILE_TAGS['is_appendable'] = 'if';
$TEMPLATE_FILE_TAGS['has_date'] = 'if';
$TEMPLATE_FILE_TAGS['has_title'] = 'if';
$TEMPLATE_FILE_TAGS['has_text'] = 'if';
$TEMPLATE_FILE_TAGS['is_deletable'] = 'if';
$TEMPLATE_FILE_TAGS['has_pic'] = 'if';
$TEMPLATE_FILE_TAGS['paging'] = 'loop';
$TEMPLATE_FILE_TAGS['listitems'] = 'loop';
if($DISPLAY_TEMPLATE_BODY) { ?>
<link rel="stylesheet" type="text/css" href="CSS/TPL_list_1.css" />

<table cellspacing="0" cellpadding="0" class="TABLE_list_navbar">
	<tr>
		<td class="TD_list_navbar_main">
			<?php if($has_firstpage) { ?>
				<a class="A_list_pageinglinkunselected" href="<?php echo $firstpage_href; ?>"><?php echo $firstpage_text; ?></a>
			<?php } else { ?>
			<?php } ?>
			<?php if(isset($paging))for($paging_counter=0;$paging_counter < countOrZero($paging);$paging_counter ++) { ?>				
				<?php if(isset($paging[$paging_counter]['islink']))for($islink_counter=0;$islink_counter < countOrZero($paging[$paging_counter]['islink']);$islink_counter ++) { ?>
					<a class="A_list_pageinglinkunselected" href="<?php echo $paging[$paging_counter]['islink'][$islink_counter]['href']; ?>"><?php echo $paging[$paging_counter]['islink'][$islink_counter]['text']; ?></a>
				<?php } ?>
				<?php if(isset($paging[$paging_counter]['isnotlink']))for($isnotlink_counter=0;$isnotlink_counter < countOrZero($paging[$paging_counter]['isnotlink']);$isnotlink_counter ++) { ?>
					<div class="DIV_list_pageinglinkselected"><?php echo $paging[$paging_counter]['isnotlink'][$isnotlink_counter]['text']; ?></div>
				<?php } ?>
			<?php } ?>
			<?php if($has_lastpage) { ?>
				<a class="A_list_pageinglinkunselected" href="<?php echo $lastpage_href; ?>"><?php echo $lastpage_text; ?></a>
			<?php } else { ?>
			<?php } ?>

			<?php if($is_singular) { ?>
			<?php } else { ?>
				<?php if($is_appendable) { ?> 
					<form id="<?php echo $auxform_id; ?>" action="" method="post">
						<input type="hidden" name="param1" value="<?php echo $auxform_param1; ?>" />
						<input type="hidden" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />	
					</form>				
					<div class="DIV_list_addlink" onclick="document.getElementById('<?php echo $auxform_id; ?>').submit()"><?php echo $addnew_text; ?></div>
				<?php } else { ?>
				<?php } ?>
			<?php } ?>
		</td>
		<td class="TD_list_navbar_padright">&nbsp;</td>
	</tr>
</table>

<table cellspacing="0" cellpadding="0" class="TABLE_list_container">
	<?php if(isset($listitems))for($listitems_counter=0;$listitems_counter < countOrZero($listitems);$listitems_counter ++) { ?>
		<tr class="TR_list_divider">
			<td colspan="4"></td>
		</tr>
	  	<tr class="TR_list_item">
			<td class="TD_list_leftspacer"><div class="TD_list_leftspacer"></div></td>
			<td class="TD_list_main" valign="top">
				<?php if($has_date) { ?>
					<div class="DIV_list_date"><?php echo $listitems[$listitems_counter]['date']; ?></div>						
				<?php } else { ?>
				<?php } ?>

				<?php if($has_title) { ?>
				  	<div class="DIV_list_title"><?php echo $listitems[$listitems_counter]['title']; ?></div>	
				<?php } else { ?>
				<?php } ?>
				
				<?php if($has_text) { ?>
					<div class="DIV_list_text"><?php echo $listitems[$listitems_counter]['text']; ?></div>
				<?php } else { ?>	
				<?php } ?>
			</td>
			<td class="TD_list_right">				
				<div class="DIV_list_operations" valign="top">
					<?php if($is_deletable) { ?>
						<form action="" method="post">
							<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op1_param']; ?>" />
							<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />	
							<div class="opbutton_del" onclick="if (confirm('<?php echo $listitems[$listitems_counter]['op1_confirm']; ?>')) this.parentNode.submit()"></div>			
						</form>
					<?php } else { ?>
					<?php } ?>
					<form action="" method="post">					
						<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />	
						<div class="opbutton_mod" onclick="this.parentNode.submit()"></div>			
					</form>
				</div>
				<?php if($has_pic) { ?>
					<div class="DIV_list_pic_lable">
						<?php echo $listitems[$listitems_counter]['lable_pic']; ?>				
					</div>
					<div class="DIV_list_pic">
						<img src="<?php echo $listitems[$listitems_counter]['pic']; ?>" class="IMG_list_pic" />
					</div>
				<?php } else { ?>
				<?php } ?>
			</td>
			
		</tr>			
	<?php } ?>
</table>
<?php } ?>