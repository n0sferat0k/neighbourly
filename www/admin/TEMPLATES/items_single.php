<?php 
$TEMPLATE_FILE_TAGS['auxform_id'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform_onsubmit'] = 'tag';
$TEMPLATE_FILE_TAGS['submitSource'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['keyname'] = 'tag';
$TEMPLATE_FILE_TAGS['keyvalue'] = 'tag';
$TEMPLATE_FILE_TAGS['mainform_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_title'] = 'tag';
$TEMPLATE_FILE_TAGS['titletextINP'] = 'tag';
$TEMPLATE_FILE_TAGS['titletextVAL'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_date'] = 'tag';
$TEMPLATE_FILE_TAGS['datetextINP'] = 'tag';
$TEMPLATE_FILE_TAGS['datetextVAL'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_link'] = 'tag';
$TEMPLATE_FILE_TAGS['linktextINP'] = 'tag';
$TEMPLATE_FILE_TAGS['linktextVAL'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_accent'] = 'tag';
$TEMPLATE_FILE_TAGS['accentcheckINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_zindex'] = 'tag';
$TEMPLATE_FILE_TAGS['zindexselectINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_text'] = 'tag';
$TEMPLATE_FILE_TAGS['texttextINP'] = 'tag';
$TEMPLATE_FILE_TAGS['texttextVAL'] = 'tag';
$TEMPLATE_FILE_TAGS['submitbuttonlable'] = 'tag';
$TEMPLATE_FILE_TAGS['VIEW_HIDE_IMGS'] = 'tag';
$TEMPLATE_FILE_TAGS['VIEW_HIDE_FILES'] = 'tag';
$TEMPLATE_FILE_TAGS['images_null_text'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform2_onsubmit'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform2_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_delpic'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform3_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_defpic'] = 'tag';
$TEMPLATE_FILE_TAGS['key'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_image'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_addpic'] = 'tag';
$TEMPLATE_FILE_TAGS['pic_src'] = 'tag';
$TEMPLATE_FILE_TAGS['imagefileINP'] = 'tag';
$TEMPLATE_FILE_TAGS['files_null_text'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform4_onsubmit'] = 'tag';
$TEMPLATE_FILE_TAGS['auxform4_param1'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_delfile'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_file'] = 'tag';
$TEMPLATE_FILE_TAGS['filehasnameBOOL'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_addfile'] = 'tag';
$TEMPLATE_FILE_TAGS['needs_auxform'] = 'if';
$TEMPLATE_FILE_TAGS['has_add_numerics'] = 'if';
$TEMPLATE_FILE_TAGS['has_add_strings'] = 'if';
$TEMPLATE_FILE_TAGS['has_title'] = 'if';
$TEMPLATE_FILE_TAGS['title_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['has_date'] = 'if';
$TEMPLATE_FILE_TAGS['date_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['has_link'] = 'if';
$TEMPLATE_FILE_TAGS['link_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['has_accent'] = 'if';
$TEMPLATE_FILE_TAGS['is_accented'] = 'if';
$TEMPLATE_FILE_TAGS['has_zindex'] = 'if';
$TEMPLATE_FILE_TAGS['has_text'] = 'if';
$TEMPLATE_FILE_TAGS['text_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['has_right'] = 'if';
$TEMPLATE_FILE_TAGS['has_pic1'] = 'if';
$TEMPLATE_FILE_TAGS['has_file1'] = 'if';
$TEMPLATE_FILE_TAGS['has_pic2'] = 'if';
$TEMPLATE_FILE_TAGS['multipic'] = 'if';
$TEMPLATE_FILE_TAGS['pic_isdeleteable'] = 'if';
$TEMPLATE_FILE_TAGS['pichasname'] = 'if';
$TEMPLATE_FILE_TAGS['pic_isdeletable'] = 'if';
$TEMPLATE_FILE_TAGS['pic_iseditable'] = 'if';
$TEMPLATE_FILE_TAGS['has_file2'] = 'if';
$TEMPLATE_FILE_TAGS['file_isdeleteable'] = 'if';
$TEMPLATE_FILE_TAGS['filehasname'] = 'if';
$TEMPLATE_FILE_TAGS['multifile'] = 'if';
$TEMPLATE_FILE_TAGS['zindexes'] = 'loop';
$TEMPLATE_FILE_TAGS['images'] = 'loop';
$TEMPLATE_FILE_TAGS['files'] = 'loop';
$TEMPLATE_FILE_TAGS['add_numerics'] = 'cloop';
$TEMPLATE_FILE_TAGS['add_strings'] = 'cloop';
if($DISPLAY_TEMPLATE_BODY) {?>
<link rel="stylesheet" type="text/css" href="CSS/TPL_single.css" />
<script src="javascripts/POPUP_V3.js" type="text/javascript"></script>
<script src="javascripts/GENERAL.js" type="text/javascript"></script>

<?php if($needs_auxform) { ?>
<!-******************************************************************************** AUXILIAR FORM FOR DELETING PICTURE-->
	<form id="<?php echo $auxform_id; ?>" action="" method="post" onsubmit="return confirm('<?php echo $auxform_onsubmit; ?>');">
		<input type="hidden" name="submitSource" value="<?php echo $submitSource; ?>" />		
		<input type="hidden" id = "param1_id" name="param1" value="<?php echo $auxform_param1; ?>" />		
		<input type="hidden" id = "param2_id" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />	
		<input type="hidden" id = "param3_id" name="" value="" />
		<input type="hidden" id = "param4_id" name="" value="" />
	</form>
	<script language="javascript" type="text/javascript">		
		function doop(formid,conf,param1value,param3name,param3value,param4name,param4value)
		{						
			var form = document.getElementById(formid);
			var param1 = document.getElementById('param1_id');
			var param3 = document.getElementById('param3_id');
			var param4 = document.getElementById('param4_id');
			if((conf == '')||((conf != '')&&(confirm(conf))))
			{
					param1.value = param1value;
					param3.name = param3name;								
					param3.value = param3value;										
					param4.name = param4name;								
					param4.value = param4value;													
					form.submit();								
			}							
		}
	</script>
<?php } else { ?>
<?php } ?>

<center>
	<form action="" method="post" enctype="multipart/form-data">
		<input type="hidden" name="submitSource" value="<?php echo $submitSource; ?>" />		
		<input type="hidden" name="param1" value="<?php echo $mainform_param1; ?>" />
		<input type="hidden" name="<?php echo $keyname; ?>" value="<?php echo $keyvalue; ?>" />
		<div class="PAGE">
			<div class="PAGE_LEFT">					
				<?php if($has_add_strings) { ?>
					<?php for($add_strings_counter=0;$add_strings_counter < countOrZero($add_strings);$add_strings_counter ++) { switch($add_strings[$add_strings_counter]['case']){
			  			case 'editable': ?>
					    	<div class="LABLE">	
								<?php echo $add_strings[$add_strings_counter]['add_stringsVAR']; ?>	
							</div>					
							<div class="POSITIONER">							
								<?php 
									if($add_strings_counter == 0) {
										?>
											<select class="INPUT_SELECT" name="<?php echo $add_strings[$add_strings_counter]['add_stringsINP']; ?>">
												<option value="INFO" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "INFO") echo 'selected="selected"'?> >Info</option>
												<option value="DONATION" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "DONATION") echo 'selected="selected"'?> >Donation</option>
												<option value="BARTER" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "BARTER") echo 'selected="selected"'?> >Barter</option>
												<option value="SALE" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "SALE") echo 'selected="selected"'?> >Sale</option>
												<option value="EVENT" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "EVENT") echo 'selected="selected"'?> >Event</option>
												<option value="NEED" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "NEED") echo 'selected="selected"'?> >Need</option>
												<option value="REQUEST" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "REQUEST") echo 'selected="selected"'?> >Request</option>
												<option value="SKILLSHARE" <?php if($add_strings[$add_strings_counter]['add_stringsVAL'] == "SKILLSHARE") echo 'selected="selected"'?> >Skillshare</option>
											</select>
										<?php
									} else {
										?>
											<input type="text" name="<?php echo $add_strings[$add_strings_counter]['add_stringsINP']; ?>" value="<?php echo $add_strings[$add_strings_counter]['add_stringsVAL']; ?>" class="INPUT_TEXT"/>
										<?php
									}
								?>								
							</div>	
					  	<?php break;
					  	case 'static': ?>
					    	<div class="LABLE">	
							  	<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAR']; ?>	
							</div>
					    	<div class="POSITIONER">
								<input type="text" name="<?php echo $add_strings[$add_strings_counter]['add_stringsINP']; ?>" value="<?php echo $add_strings[$add_strings_counter]['add_stringsVAL']; ?>" class="INPUT_TEXT" readonly="readonly"/>
							</div>
					  	<?php break;					  
					}} ?>
				<?php } ?>
					
				<?php if($has_add_numerics) { ?>				
					
					<?php for($add_numerics_counter=0;$add_numerics_counter < countOrZero($add_numerics);$add_numerics_counter ++) { switch($add_numerics[$add_numerics_counter]['case']){
			  			case 'editable': ?>
					    	<div class="LABLE">	
								<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAR']; ?>	
							</div>					
							<div class="POSITIONER">
								<?php
									if($add_numerics_counter == 2 || $add_numerics_counter == 3) {
										$date = getdate($add_numerics[$add_numerics_counter]['add_numericsVAL']);
										$formattedDate = (($date['mon'] < 10) ? "0" . $date['mon'] : $date['mon']) . "/" . (($date['mday'] < 10) ? "0" . $date['mday'] : $date['mday']) . "/" . $date['year'] . " " . (($date["hours"] < 10) ? "0" . $date["hours"] : $date["hours"]) . ":" . (($date['minutes'] < 10) ? "0" . $date['minutes'] : $date['minutes']) . ":" . (($date['seconds'] < 10) ? "0" . $date['seconds'] : $date['seconds']) . " " . date_default_timezone_get();
										?>
											<input type="hidden" id="inp_adddnum_<?php echo $add_numerics_counter ?>" name="<?php echo $add_numerics[$add_numerics_counter]['add_numericsINP']; ?>" value="<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAL']?>"/> 
											<input type="text" value="<?php echo $formattedDate; ?>" class="INPUT_TEXT" onchange="document.getElementById('inp_adddnum_<?php echo $add_numerics_counter ?>').value = new Date(this.value).getTime() / 1000"/>
										<?php
									} else {
										?>
											<input type="text" name="<?php echo $add_numerics[$add_numerics_counter]['add_numericsINP']; ?>" value="<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAL']; ?>" class="INPUT_TEXT"/>
										<?php
									}
								?>
							</div>													
					  	<?php break;
					  	case 'static': ?>
					  		<div class="LABLE">	
							  	<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAR']; ?>	
							</div>
					    	<div class="POSITIONER">
								<input type="text" name="<?php echo $add_numerics[$add_numerics_counter]['add_numericsINP']; ?>" value="<?php echo $add_numerics[$add_numerics_counter]['add_numericsVAL']; ?>" class="INPUT_TEXT" readonly="readonly"/>
							</div>
					  	<?php break;					  
					}} ?>
					
				<?php } ?>

				<?php if($has_title) { ?>	
					<div class="LABLE">				
						<?php echo $lable_title; ?>
					</div>					
					<?php if($title_iseditable) { ?>
						<div class="POSITIONER">
							<input type="text" name="<?php echo $titletextINP; ?>" value="<?php echo $titletextVAL; ?>" class="INPUT_TEXT"/>
						</div>
					<?php } else { ?>
						<div class="POSITIONER">
							<input type="text" name="<?php echo $titletextINP; ?>" value="<?php echo $titletextVAL; ?>" class="INPUT_TEXT" readonly="readonly"/>
						</div>
					<?php } ?>											
				<?php } else { ?>
				<?php } ?>	
							
				<?php if($has_date) { ?>
					<div class="LABLE">
						<?php echo $lable_date; ?>					
					</div>
						<?php 
						$date = getdate();
						$formattedDate = (($date['mon'] < 10) ? "0" . $date['mon'] : $date['mon']) . "/" . (($date['mday'] < 10) ? "0" . $date['mday'] : $date['mday']) . "/" . $date['year'] . " " . (($date["hours"] < 10) ? "0" . $date["hours"] : $date["hours"]) . ":" . (($date['minutes'] < 10) ? "0" . $date['minutes'] : $date['minutes']) . ":" . (($date['seconds'] < 10) ? "0" . $date['seconds'] : $date['seconds']) . " " . date_default_timezone_get();
						if($date_iseditable) { ?>
							<div class="POSITIONER">
								<input type="text" id="id_<?php echo $datetextINP; ?>" name="<?php echo $datetextINP; ?>" value="<?php echo $datetextVAL; ?>" class="INPUT_TEXT" />
								<span class="DATA_NOW" onclick="document.getElementById('id_<?php echo $datetextINP ?>').value='<?php echo $formattedDate;?>'">Now</span>
							</div>
						<?php } else { ?>
							<div class="POSITIONER">
								<input type="text" name="<?php echo $datetextINP; ?>" value="<?php echo $datetextVAL; ?>" class="INPUT_TEXT" readonly="readonly"/>
							</div>
						<?php } ?>
				<?php } else { ?>										
				<?php } ?>
				
				<?php if($has_link) { ?>	
					<div class="LABLE">				
						<?php echo $lable_link; ?>
					</div>					
					<?php if($link_iseditable) { ?>
						<div class="POSITIONER">
							<input type="text" name="<?php echo $linktextINP; ?>" value="<?php echo $linktextVAL; ?>" class="INPUT_TEXT"/>
						</div>
					<?php } else { ?>
						<div class="POSITIONER">
							<input type="text" name="<?php echo $linktextINP; ?>" value="<?php echo $linktextVAL; ?>" class="INPUT_TEXT" readonly="readonly"/>
						</div>
					<?php } ?>											
				<?php } else { ?>
				<?php } ?>	
				
				<?php if($has_accent) { ?>
					<div class="LABLE">
						<?php echo $lable_accent; ?>
					</div>
					<input type="checkbox" name="<?php echo $accentcheckINP; ?>" <?php if($is_accented) { ?> checked="checked" <?php } else { ?><?php } ?>/>												
				<?php } else { ?>										
				<?php } ?>
				
				<?php if($has_zindex) { ?>
					<div class="LABLE">
						<?php echo $lable_zindex; ?>
					</div>
					<div class="POSITIONER">
						<select name="<?php echo $zindexselectINP; ?>" class="INPUT_SELECT">
							<?php for($zindexes_counter=0;$zindexes_counter < countOrZero($zindexes);$zindexes_counter ++) { ?>
								<option value="<?php echo $zindexes[$zindexes_counter]['val']; ?>" <?php for($selected_counter=0;$selected_counter < countOrZero($zindexes[$zindexes_counter]['selected']);$selected_counter ++) { ?> selected="selected" <?php } ?> >
									<?php echo $zindexes[$zindexes_counter]['text']; ?>
								</option>
							<?php } ?>
						</select>
					</div>
				<?php } else { ?>										
				<?php } ?>
							
				<?php if($has_text) { ?>
					<div class="LABLE">
						<?php echo $lable_text; ?>
					</div>
					<?php if($text_iseditable) { ?>
						<div class="POSITIONER">
							<div class="EDITOR_POS">
								<textarea rows="16" cols="70" name="<?php echo $texttextINP; ?>"><?php echo $texttextVAL; ?></textarea>
							</div>
						</div>
					<?php } else { ?>
							<input type="hidden" name="<?php echo $texttextINP; ?>" value="<?php echo $texttextVAL; ?>" />
							<div class="TEXT_CONTAINER"><?php echo $texttextVAL; ?></div>							
					<?php } ?>				
				<?php } else { ?>
				<?php } ?>
				<div class="POSITIONER">
					<input type="submit" value="<?php echo $submitbuttonlable; ?>" class="BUTTON" />
				</div>					
			</div>
			<?php if($has_right) { ?>
				<div class="PAGE_RIGHT">
					<script language="javascript" type="text/javascript">
						function switchTo(obj,id,back)
						{
							var submitSources = document.getElementsByName("submitSource");
							for(i = 0;i < submitSources.length;i++)
							{
							 	submitSources[i].value = back;
							}							
							var buts = document.getElementsByName("buttons");
							var conts = document.getElementsByName("containers");
							var tmp1;
							var tmp2;
							var obj2;
							for(i = 0;i < buts.length;i++)
							{
								tmp1 = buts[i].className;
								tmp2 = tmp1.split(" ");
								
								buts[i].className = "BUTTON_HIDE " + tmp2[1];
							}
							for(i = 0;i < conts.length;i++)
							{
								conts[i].style.display = "none";
							}							
							
							tmp1 = obj.className;
							tmp2 = tmp1.split(" ");							
							obj.className = "BUTTON_VIEW " + tmp2[1];
							obj2 = document.getElementById(id);							
							obj2.style.display = "block";
						}
					</script>
					<?php if($has_pic1) { ?>
						<div class="BUTTON_<?php echo $VIEW_HIDE_IMGS; ?> IMG_V" name="buttons" id="view_button" onclick="switchTo(this,'IMG_view',1)"></div>
						<div class="BUTTON_HIDE IMG_E" name="buttons" id="edit_button" onclick="switchTo(this,'IMG_edit',2)"></div>
						<?php if($has_file1) { ?>
							<div class="BUTTON_<?php echo $VIEW_HIDE_FILES; ?> FILE_V" name="buttons" id="view_button" onclick="switchTo(this,'FILE_view',3)"></div>
							<div class="BUTTON_HIDE FILE_E" name="buttons" id="edit_button" onclick="switchTo(this,'FILE_edit',4)"></div>
						<?php } else { ?>
						<?php } ?>
					<?php } else { ?>
						<?php if($has_file1) { ?>
							<div class="BUTTON_VIEW FILE_V" name="buttons" id="view_button" onclick="switchTo(this,'FILE_view',3)"></div>
							<div class="BUTTON_HIDE FILE_E" name="buttons" id="edit_button" onclick="switchTo(this,'FILE_edit',4)"></div>
						<?php } else { ?>
						<?php } ?>
					<?php } ?>
					
					<div class="FRAME_OUTER" id="position_reference_for_popup">
						<div class="FRAME_INNER">
							<div class="FRAME_INNER_POS1">
								<div class="FRAME_INNER_POS2">
									<?php if($has_pic2) { ?>																					
										<?php if($multipic) { ?>											
											<div class="FRAME_INNER_<?php echo $VIEW_HIDE_IMGS; ?>" id="IMG_view" name="containers">
												<?php echo $images_null_text; ?>
												<?php for($images_counter=0;$images_counter < countOrZero($images);$images_counter ++) { ?>	
													<div class="IMAGE_CONTAINER <?php echo $images[$images_counter]['selected']; ?>">
														<div class="IMAGE_INNER">													
															<div class="IMAGE_PIC" style="background-image:url('thumb.php?img_tip=0&img_url=<?php echo $images[$images_counter]['pic_src']; ?>')">
															</div>
															<?php if($pic_isdeleteable) { ?>
																<div class="OP_DELETE" onclick="doop('<?php echo $auxform_id; ?>','<?php echo $auxform2_onsubmit; ?>','<?php echo $auxform2_param1; ?>','<?php echo $images[$images_counter]['keyname']; ?>','<?php echo $images[$images_counter]['keyvalue']; ?>')">
																	<?php echo $lable_delpic; ?>
																</div>	
															<?php } else { ?>
															<?php } ?>												
															
															<div class="OP_DEFAULT" onclick="doop('<?php echo $auxform_id; ?>','','<?php echo $auxform3_param1; ?>','<?php echo $images[$images_counter]['keyname']; ?>','<?php echo $images[$images_counter]['keyvalue']; ?>')">
																<?php echo $lable_defpic; ?>
															</div>
															<?php if($pichasname) { ?>											
																<input class="OP_RENAME" type="text" id="<?php echo $images[$images_counter]['keyname']; ?>_<?php echo $images[$images_counter]['keyvalue']; ?>" value="<?php echo $images[$images_counter]['picname']; ?>" onchange="doop('<?php echo $auxform_id; ?>','','setpicname','<?php echo $images[$images_counter]['keyname']; ?>','<?php echo $images[$images_counter]['keyvalue']; ?>','<?php echo $images[$images_counter]['namename']; ?>',document.getElementById('<?php echo $images[$images_counter]['keyname']; ?>_<?php echo $images[$images_counter]['keyvalue']; ?>').value)" />
															<?php } else { ?>
                                                            <?php } ?>
                                                            
                                                            <div class="OP_GETURL">
																<input type="text" class="OP_GETURL_INPUT" onclick="this.focus();this.select();" value="<?php echo $images[$images_counter]['pic_rel_url'];?>">
															</div>                                                           
															
														</div>
													</div>
												<?php } ?>
											</div>	
											<div class="FRAME_INNER_HIDE" id="IMG_edit" name="containers">
												<input type="hidden" name="<?php echo $key; ?>_IMGS_count" value="1" id="counter"/>																																																									
												<div class="FRAME_LABLE">
													<?php echo $lable_image; ?>											
												</div>
												<input size="24" type="file" name="<?php echo $key; ?>_pic_0" class="INPUT_FILE" />
												<div id="container"></div>
												<script type="text/javascript" language="javascript">
													function addpic()
													{
														var counter	= document.getElementById('counter');
														var container	= document.getElementById('container');
														
														if(counter.value<5)
														{
															var imgfile = document.createElement("INPUT");
															imgfile.size=24;
															imgfile.type="file";
															imgfile.name="<?php echo $key; ?>_pic_" + counter.value;
															imgfile.className = "INPUT_FILE";
															
															counter.value++;
															container.appendChild(imgfile);
														}
													}													
												</script>
												<div class="FRAME_ADDLINK" onclick="addpic()"><?php echo $lable_addpic; ?></div>
												<input type="submit" value="<?php echo $submitbuttonlable; ?>" class="BUTTON" />
											</div>										
										<?php } else { ?>											
											<div class="FRAME_INNER_<?php echo $VIEW_HIDE_IMGS; ?>" id="IMG_view" name="containers">	
												<div class="IMAGE_CONTAINER">
													<div class="IMAGE_INNER">
														<div class="IMAGE_PIC" style="background-image:url('thumb.php?img_tip=0&img_url=<?php echo $pic_src; ?>')">
														</div>
														<?php if($pic_isdeleteable) { ?>
															<div class="OP_DELETE" onclick="if(document.getElementById('<?php echo $auxform_id; ?>').onsubmit()) document.getElementById('<?php echo $auxform_id; ?>').submit()">
																<?php echo $lable_delpic; ?>
															</div>
														<?php } else { ?>
                                                        <?php } ?>
                                                        
                                                        <div class="OP_GETURL">
															<input type="text" class="OP_GETURL_INPUT" onclick="this.focus();this.select();" value="<?php echo $pic_rel_url;?>">
														</div>                                                       
														
													</div>
												</div>										
											</div>	
											<div class="FRAME_INNER_HIDE" id="IMG_edit" name="containers">									
												<?php if($pic_iseditable) { ?>
													<div class="FRAME_LABLE">
														<?php echo $lable_image; ?>											
													</div>
													<input size="24" type="file" name="<?php echo $imagefileINP; ?>" class="INPUT_FILE" />
													<input type="submit" value="<?php echo $submitbuttonlable; ?>" class="BUTTON" />
												<?php } else { ?>
												<?php } ?>						
											</div>
										<?php } ?>
									<?php } else { ?>																
									<?php } ?>
									<?php if($has_file2) { ?>
										<div class="FRAME_INNER_<?php echo $VIEW_HIDE_FILES; ?>" id="FILE_view" name="containers">
											<?php echo $files_null_text; ?>
											<?php for($files_counter=0;$files_counter < countOrZero($files);$files_counter ++) { ?>
												<div class="FILE_CONTAINER">
													<div class="IMAGE_INNER">													
														<div class="FILE_PIC" style="background-image:url(thumb.php?img_tip=3&img_url=<?php echo $files[$files_counter]['filepic_src']; ?>)" />																	
														</div>
														<div class="FILE_PATH">
															<?php echo $files[$files_counter]['filepath']; ?>
														</div>
														<?php if($file_isdeleteable) { ?>				
															<div class="OP_DELETE" onclick="doop('<?php echo $auxform_id; ?>','<?php echo $auxform4_onsubmit; ?>','<?php echo $auxform4_param1; ?>','<?php echo $files[$files_counter]['keyname']; ?>','<?php echo $files[$files_counter]['keyvalue']; ?>')">
																<?php echo $lable_delfile; ?>
															</div>
														<?php } else { ?>
														<?php } ?>	
															
														<?php if($filehasname) { ?>		
															<div class="OP_RENAME" onclick="createIdSourcedPopupFromObjectOverlapped('container_<?php echo $files[$files_counter]['keyvalue']; ?>',document.getElementById('position_reference_for_popup'),100,-100)">
																<?php echo $files[$files_counter]['filename_lable']; ?>
															</div>	
															<div class="OP_RENAME_DIV" id="container_<?php echo $files[$files_counter]['keyvalue']; ?>">
																<div class="OP_RENAME_DIV_CLOSE" onclick="destroyPopup()"></div>								
																<input class="OP_RENAME_INPUT" type="text" id="<?php echo $files[$files_counter]['keyname']; ?>_<?php echo $files[$files_counter]['keyvalue']; ?>" value="<?php echo $files[$files_counter]['filename']; ?>"  />
																<input type="button" class="BUTTON" onclick="doop('<?php echo $auxform_id; ?>','','setfilename','<?php echo $files[$files_counter]['keyname']; ?>','<?php echo $files[$files_counter]['keyvalue']; ?>','<?php echo $files[$files_counter]['namename']; ?>',document.getElementById('<?php echo $files[$files_counter]['keyname']; ?>_<?php echo $files[$files_counter]['keyvalue']; ?>').value)" value="<?php echo $submitbuttonlable; ?>">															
															</div>
														<?php } else { ?>															
														<?php } ?>
													</div>
												</div>													
											<?php } ?>
										</div>	
										<div class="FRAME_INNER_HIDE" id="FILE_edit" name="containers">
											<input type="hidden" name="<?php echo $key; ?>_FILES_count" value="1" id="counter2"/>																																																									
											<div class="FRAME_LABLE">
												<?php echo $lable_file; ?>											
											</div>
											<?php if($filehasname) { ?>
												<input size="24" type="text" name="<?php echo $key; ?>_filename_0" class="INPUT_NAME" />
											<?php } else { ?>
											<?php } ?>
											<input size="24" type="file" name="<?php echo $key; ?>_file_0" class="INPUT_FILE" />
											<div id="container2"></div>	
											<script type="text/javascript" language="javascript">
												function addfile()
												{
												 	var filehasname = <?php echo $filehasnameBOOL; ?>;
													var counter	= document.getElementById('counter2');
													var container	= document.getElementById('container2');
													
													if(counter.value<5)
													{
														var fileinp = document.createElement("INPUT");
														fileinp.size=24;
														fileinp.type="file";
														fileinp.name="<?php echo $key; ?>_file_" + counter.value;
														fileinp.className = "INPUT_FILE";
														
														if(filehasname == true)
														{
															var filename = document.createElement("INPUT");
															filename.type="text";
															filename.name="<?php echo $key; ?>_filename_" + counter.value;
															filename.className = "INPUT_NAME";
															container.appendChild(filename);
														}																												
														counter.value++;
														container.appendChild(fileinp);
													}
												}												
											</script>
											<?php if($multifile) { ?>
												<div class="FRAME_ADDLINK" onclick="addfile()"><?php echo $lable_addfile; ?></div>
											<?php } else { ?>
											<?php } ?>
											<input type="submit" value="<?php echo $submitbuttonlable; ?>" class="BUTTON" />
										</div>
									<?php } else { ?>
									<?php } ?>
								</div>
							</div>
						</div>
					</div>				
				</div>
			<?php } else { ?>
			<?php } ?>	
		</div>			
	</form>
</center>

<?php } ?>