<?php 
$TEMPLATE_FILE_TAGS['href'] = 'tag';
$TEMPLATE_FILE_TAGS['text'] = 'tag';
if($DISPLAY_TEMPLATE_BODY) { ?>
<a class="navlink" href="<?php echo $href; ?>"><?php echo $text; ?></a>
<?php } ?>