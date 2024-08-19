<?php 
$TEMPLATE_FILE_TAGS['lable_adduser'] = 'tag';
$TEMPLATE_FILE_TAGS['param1'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_username'] = 'tag';
$TEMPLATE_FILE_TAGS['usernameINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_userpass'] = 'tag';
$TEMPLATE_FILE_TAGS['userpassINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_userpassrepeat'] = 'tag';
$TEMPLATE_FILE_TAGS['userpassrepeatINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_userlevel'] = 'tag';
$TEMPLATE_FILE_TAGS['userlevelINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_name'] = 'tag';
$TEMPLATE_FILE_TAGS['nameINP'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_button'] = 'tag';
$TEMPLATE_FILE_TAGS['param1_exit'] = 'tag';
$TEMPLATE_FILE_TAGS['lable_exit'] = 'tag';
$TEMPLATE_FILE_TAGS['options'] = 'loop';
$TEMPLATE_FILE_TAGS['admins'] = 'loop';
if($DISPLAY_TEMPLATE_BODY) { ?>
<center>
	<script language="javascript" type="text/javascript">
		function onsubm()
		{
			var p1 = document.getElementById("PW1");
			var p2 = document.getElementById("PW2");
			if(p1.value!=p2.value)
			{				
				var r1 = document.createElement("SPAN");
				var r2 = document.createElement("SPAN");
				r1.style.color="red";				
				r2.style.color="red";
				r1.style.paddingLeft="15px";
				r2.style.paddingLeft="15px";
				r1.innerHTML="&raquo;";				
				r2.innerHTML="&raquo;";				
				document.getElementById("L1").appendChild(r1);
				document.getElementById("L2").appendChild(r2);
				
				return false;				
			}
			else
				return true;			
		}
	</script>
	<form class="useradd_form" action="" method="post" onsubmit="return onsubm();">
		<fieldset>
			<legend><?php echo $lable_adduser; ?></legend>
			<input type="hidden" name="param1" value="<?php echo $param1; ?>" />								
			<table cellspacing="0" cellpadding="0" style="width:100%;height:100%">
				<tr style="height:30px;">
					<td style="width:50%"><?php echo $lable_username; ?></td>
					<td style="width:50%"><input type="text" name="<?php echo $usernameINP; ?>" /></td>				
				</tr>
				<tr style="height:30px;">
					<td  id="L1"><?php echo $lable_userpass; ?></td>
					<td><input id="PW1" type="password" name="<?php echo $userpassINP; ?>" /></td>
				</tr>
				<tr style="height:30px;">
					<td id="L2"><?php echo $lable_userpassrepeat; ?></td>
					<td><input id="PW2" type="password" name="<?php echo $userpassrepeatINP; ?>" /></td>
				</tr>				
				<tr style="height:30px;">
					<td><?php echo $lable_userlevel; ?></td>
					<td>
						<select type="password" name="<?php echo $userlevelINP; ?>" />
							<?php if(isset($options))for($options_counter=0;$options_counter < count($options);$options_counter ++) { ?>
								<option value="<?php echo $options[$options_counter]['value_option']; ?>"><?php echo $options[$options_counter]['lable_option']; ?></option>
							<?php } ?>
						</select>
					</td>
				</tr>
				<tr style="height:30px;">
					<td style="width:50%"><?php echo $lable_name; ?></td>
					<td style="width:50%"><input type="text" name="<?php echo $nameINP; ?>" /></td>				
				</tr>
				<tr style="height:30px;">
					<td colspan="2">
						<center>
							<input type="submit" value="<?php echo $lable_button; ?>">
						</center>
					</td>				
				</tr>
			</table>				
		</fieldset>
	</form>
	<div class="admin_list">
		<div class="admin_id">ID</div>
		<div class="admin_name">NUME</div>
		<div class="admin_uname">USER</div>
		<div class="admin_level">TIP</div>
		<div class="admin_op">OP</div>
		<hr size="1" width="400px" style="float:left"/>
		<?php if(isset($admins))for($admins_counter=0;$admins_counter < count($admins);$admins_counter ++) { ?>
			<div class="admin_id"><?php echo $admins[$admins_counter]['admin_id']; ?></div>
			<div class="admin_name"><?php echo $admins[$admins_counter]['admin_name']; ?></div>
			<div class="admin_uname"><?php echo $admins[$admins_counter]['admin_uname']; ?></div>
			<div class="admin_level"><?php echo $admins[$admins_counter]['admin_level']; ?></div>
			<div class="admin_op">
				<form action="" method="post" onsubmit="return confirm('<?php echo $admins[$admins_counter]['confirm_del']; ?>')">
					<input type="hidden" name="param1" value="<?php echo $admins[$admins_counter]['param1']; ?>" />
					<input type="hidden" name="<?php echo $admins[$admins_counter]['admin_id_INP']; ?>" value="<?php echo $admins[$admins_counter]['admin_id']; ?>" />
					<input type="submit" value="<?php echo $admins[$admins_counter]['lable_del']; ?>" />
				</form>
			</div>
		<?php } ?>
		<form action="" method="post">
			<input type="hidden" name="param1" value="<?php echo $param1_exit; ?>" />
			<input class="exitbutton" type="submit" value="<?php echo $lable_exit; ?>" />
		</form>
	</div>	
	
</center>
<?php } ?>