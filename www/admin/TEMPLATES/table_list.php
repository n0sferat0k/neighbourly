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
$TEMPLATE_FILE_TAGS['has_title'] = 'if';
$TEMPLATE_FILE_TAGS['is_deletable'] = 'if';
$TEMPLATE_FILE_TAGS['is_moveable'] = 'if';
$TEMPLATE_FILE_TAGS['is_sortable'] = 'if';
$TEMPLATE_FILE_TAGS['has_pic'] = 'if';
$TEMPLATE_FILE_TAGS['paging'] = 'loop';
$TEMPLATE_FILE_TAGS['listitems'] = 'loop';
if($DISPLAY_TEMPLATE_BODY) { ?>
<link rel="stylesheet" type="text/css" href="CSS/TPL_table_list.css" />

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
					<div class="ADD_NEW" onclick="document.getElementById('<?php echo $auxform_id; ?>').submit()">
						<?php echo $addnew_text; ?>
					</div>
				<?php } else { ?>
				<?php } ?>
			<?php } ?>
		</td>
		<td class="TD_list_navbar_padright">&nbsp;</td>
	</tr>
</table>

<div class="LIST_CONT">
	<?php if(isset($listitems))for($listitems_counter=0;$listitems_counter < countOrZero($listitems);$listitems_counter ++) { ?>
		<div class="TAB">
			<?php if($has_title) { ?>
			  	<div class="TAB_TITLE">
				  	<?php echo $listitems[$listitems_counter]['title']; ?>
				</div>	
			<?php } else if($has_text) { ?>
				<div class="TAB_TITLE">
				  	<?php echo $listitems[$listitems_counter]['text']; ?>
				</div>				
			<?php } else { ?>
			<?php } ?>
						
			<div class="TAB_OPS">		
				<div class="TAB_OPS_INN">
					<?php if($is_deletable) { ?>
						<form action="" method="post">
							<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op1_param']; ?>" />
							<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />	
								<div class="TAB_OPLINK_POS">
									<div class="TAB_OPLINK_DEL" onclick="if (confirm('<?php echo $listitems[$listitems_counter]['op1_confirm']; ?>')) this.parentNode.parentNode.submit()">
										<?php echo $listitems[$listitems_counter]['op1_text']; ?>
									</div>			
								</div>
						</form>
					<?php } else { ?>
					<?php } ?>
					<form action="" method="post">					
						<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />	
						<div class="TAB_OPLINK_POS">
							<div class="TAB_OPLINK_MODI" onclick="this.parentNode.parentNode.submit()">
								<?php echo $listitems[$listitems_counter]['modi_text']; ?>
							</div>			
						</div>
					</form>
					<?php if($is_moveable) { ?>
						<div class="TAB_OPLINK_POS">
							<form action="" method="post" id="upform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>">
								<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op2_param']; ?>" />
								<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />											
							</form>
							<form action="" method="post" id="downform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>">
								<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op3_param']; ?>" />
								<input type="hidden" name="<?php echo $listitems[$listitems_counter]['keyname']; ?>" value="<?php echo $listitems[$listitems_counter]['keyvalue']; ?>" />								
							</form>
							<div class="TAB_DOWNARROW" onclick="document.getElementById('upform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>').submit()">										
							</div>										
							<div class="TAB_UPARROW" onclick="document.getElementById('downform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>').submit()">										
							</div>
						</div>
					<?php } else { ?>
					<?php } ?>
                    <?php if($is_sortable) { ?>
						<div class="TAB_OPLINK_POS">
							<form action="" method="post" id="upsortform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>">
								<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op4_param']; ?>" />
								<input type="hidden" name="<?php echo $listitems[$listitems_counter]['sortindexname']; ?>" value="<?php echo $listitems[$listitems_counter]['sortindexvalue']; ?>" />
							</form>
							<form action="" method="post" id="downsortform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>">
								<input type="hidden" name="param1" value="<?php echo $listitems[$listitems_counter]['op5_param']; ?>" />
								<input type="hidden" name="<?php echo $listitems[$listitems_counter]['sortindexname']; ?>" value="<?php echo $listitems[$listitems_counter]['sortindexvalue']; ?>" />
							</form>
							<div class="TAB_DOWNARROW" onclick="document.getElementById('upsortform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>').submit()">
							</div>
							<div class="TAB_UPARROW" onclick="document.getElementById('downsortform_<?php echo $listitems[$listitems_counter]['keyvalue']; ?>').submit()">
							</div>
						</div>
					<?php } else { ?>
					<?php } ?>
				</div>
			</div>					
			<div class="TAB_IMG">	
				<?php if($has_pic) { ?>										
					<img src="thumb.php?img_tip=11&img_url=<?php echo $listitems[$listitems_counter]['pic']; ?>" class="TAB_IMAGE" />
				<?php } else { ?>
				<?php } ?>			
			</div>
		</div>			
	<?php } ?>
</div>
<?php } ?>