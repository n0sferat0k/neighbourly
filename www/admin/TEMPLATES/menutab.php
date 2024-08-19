<?php 
$TEMPLATE_FILE_TAGS['selected'] = 'tag';
$TEMPLATE_FILE_TAGS['href'] = 'tag';
$TEMPLATE_FILE_TAGS['text'] = 'tag';
if($DISPLAY_TEMPLATE_BODY) { ?>
<div class="menutab<?php echo $selected; ?>">
	<a class="menulink<?php echo $selected; ?>" href="<?php echo $href; ?>"><?php echo $text; ?></a>
</div>
<?php } ?>