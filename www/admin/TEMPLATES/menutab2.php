<?php 
$TEMPLATE_FILE_TAGS['href'] = 'tag';
$TEMPLATE_FILE_TAGS['text'] = 'tag';
if($DISPLAY_TEMPLATE_BODY) { ?>
<div class="menutab2">
	<div class="menulink2_positioner">
		<a class="menulink2" href="<?php echo $href; ?>"><?php echo $text; ?></a>
	</div>
</div>
<?php } ?>