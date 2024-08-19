<?php 
$TEMPLATE_FILE_TAGS['langlinks'] = 'loop';
if($DISPLAY_TEMPLATE_BODY) { ?>
    <div class="DIV_languagebar">
	<?php
            if(isset($langlinks))
            {
                for($langlinks_counter=0;$langlinks_counter < count($langlinks);$langlinks_counter ++)
                {
                    if(!$langlinks[$langlinks_counter]['selected'])
                    {
                        ?>
                                <form action="" method="post">
                                        <input type="hidden" name="param1" value="<?php echo $langlinks[$langlinks_counter]['param1']; ?>" />
                                        <input type="hidden" name="<?php echo $langlinks[$langlinks_counter]['languageINP']; ?>" value="<?php echo $langlinks[$langlinks_counter]['languageVAR']; ?>" />
                                        <input type="hidden" name="<?php echo $langlinks[$langlinks_counter]['extraparamINP']; ?>" value="<?php echo $langlinks[$langlinks_counter]['extraparamVAR']; ?>" />
                                        <div class="DIV_unselected_language" onclick="this.parentNode.submit()"><?php echo $langlinks[$langlinks_counter]['languageVAR']; ?></div>
                                </form>
                        <?php
                    }
                    else
                    {
                        ?>
                            <div class="DIV_selected_language"><?php echo $langlinks[$langlinks_counter]['languageVAR']; ?></div>
                        <?php
                    }
                    if($langlinks[$langlinks_counter]['hasnext'])
                    {
                        ?>
                            <div class="DIV_language_separator">|</div>
                        <?php
                    }
                }
            }
        ?>
    </div>
<?php } ?>