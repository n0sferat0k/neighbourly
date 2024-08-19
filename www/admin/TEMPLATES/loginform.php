<?php 
$TEMPLATE_FILE_TAGS['lable'] = 'tag';
$TEMPLATE_FILE_TAGS['param1'] = 'tag';
$TEMPLATE_FILE_TAGS['usernameTXT'] = 'tag';
$TEMPLATE_FILE_TAGS['usernameINP'] = 'tag';
$TEMPLATE_FILE_TAGS['userpassTXT'] = 'tag';
$TEMPLATE_FILE_TAGS['userpassINP'] = 'tag';
$TEMPLATE_FILE_TAGS['buttonTXT'] = 'tag';
if($DISPLAY_TEMPLATE_BODY) { ?>
<center>
	<form class="login_form" action="" method="post">
		<fieldset>
			<legend><?php echo $lable; ?></legend>
			<input type="hidden" name="param1" value="<?php echo $param1; ?>" />								
			<table cellspacing="0" cellpadding="0" style="width:100%;height:100%">
				<tr style="height:30px;">
					<td style="width:50%"><?php echo $usernameTXT; ?></td>
					<td style="width:50%"><input type="text" name="<?php echo $usernameINP; ?>" /></td>				
				</tr>
				<tr style="height:30px;">
					<td><?php echo $userpassTXT; ?></td>
					<td><input type="password" name="<?php echo $userpassINP; ?>" /></td>
				</tr>
				<tr style="height:30px;">
					<td colspan="2">
						<center>
							<input type="submit" value="<?php echo $buttonTXT; ?>">
						</center>
					</td>				
				</tr>
			</table>				
		</fieldset>
	</form>
</center>
<?php } ?>