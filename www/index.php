<?php
    error_reporting(E_ERROR|E_PARSE|E_STRICT);
    include("admin/ADMIN_INCLUDE/connect_db.inc.php");
    include("session.php");
    include("texts.php");

    $sql_luni = "SELECT * FROM meniu_luni ME LEFT JOIN mancaruri M ON (ME.meniu_luni_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_luni_add_numerics_1 = C.categorii_id) LEFT JOIN (SELECT A.mancaruri_id AS mid, U.* FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) WHERE A.valid = 1 AND A.day = 'monday' AND U.utilizatori_id = " . $_SESSION['id'] . ") AS AM ON (AM.mid = ME.meniu_luni_add_numerics_0)  ORDER BY C.categorii_sortindex ASC";
    $sql_marti = "SELECT * FROM meniu_marti ME LEFT JOIN mancaruri M ON (ME.meniu_marti_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_marti_add_numerics_1 = C.categorii_id) LEFT JOIN (SELECT A.mancaruri_id AS mid, U.* FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) WHERE A.valid = 1 AND A.day = 'tuesday' AND U.utilizatori_id = " . $_SESSION['id'] . ") AS AM ON (AM.mid = ME.meniu_marti_add_numerics_0)  ORDER BY C.categorii_sortindex ASC";
    $sql_miercuri = "SELECT * FROM meniu_miercuri ME LEFT JOIN mancaruri M ON (ME.meniu_miercuri_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_miercuri_add_numerics_1 = C.categorii_id) LEFT JOIN (SELECT A.mancaruri_id AS mid, U.* FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) WHERE A.valid = 1 AND A.day = 'wednsday' AND U.utilizatori_id = " . $_SESSION['id'] . ") AS AM ON (AM.mid = ME.meniu_miercuri_add_numerics_0)  ORDER BY C.categorii_sortindex ASC";
    $sql_joi = "SELECT * FROM meniu_joi ME LEFT JOIN mancaruri M ON (ME.meniu_joi_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_joi_add_numerics_1 = C.categorii_id) LEFT JOIN (SELECT A.mancaruri_id AS mid, U.* FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) WHERE A.valid = 1 AND A.day = 'thursday' AND U.utilizatori_id = " . $_SESSION['id'] . ") AS AM ON (AM.mid = ME.meniu_joi_add_numerics_0)  ORDER BY C.categorii_sortindex ASC";
    $sql_vineri = "SELECT * FROM meniu_vineri ME LEFT JOIN mancaruri M ON (ME.meniu_vineri_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_vineri_add_numerics_1 = C.categorii_id) LEFT JOIN (SELECT A.mancaruri_id AS mid, U.* FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) WHERE A.valid = 1 AND A.day = 'friday' AND U.utilizatori_id = " . $_SESSION['id'] . ") AS AM ON (AM.mid = ME.meniu_vineri_add_numerics_0)  ORDER BY C.categorii_sortindex ASC";

    $data_luni = mysqli_query($connection, $sql_luni)or die(mysqli_error());
    $data_marti = mysqli_query($connection, $sql_marti)or die(mysqli_error());
    $data_miercuri = mysqli_query($connection, $sql_miercuri)or die(mysqli_error());
    $data_joi = mysqli_query($connection, $sql_joi)or die(mysqli_error());
    $data_vineri = mysqli_query($connection, $sql_vineri)or die(mysqli_error());
?>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Softvision - Food pages</title>
		<!--**************************************************************************************** ESENTIAL JAVA SCRIPTS, DO NOT REMOVE-->
		<!----> <script src="admin/javascripts/lib/prototype.js" type="text/javascript"></script>
		<!----> <script src="admin/javascripts/src/scriptaculous.js" type="text/javascript"></script>
        <!----> <script language="javascript" type="text/javascript" src="admin/javascripts/POPUP_V9.js"></script>
		<!----> <script src="admin/javascripts/src/unittest.js" type="text/javascript"></script>
		<!--*****************************************************************************************************************************-->

        <link rel="stylesheet" type="text/css" href="css/general.css"/>
        <script language="JavaScript" type="text/javascript">
            var maxDailyPrice = 12;

            function updateMenu() {
                new Ajax.Request("menu.php",{
                    method: 'get',
                    parameters: "",
                    onSuccess: function(transport){$("menu_cont").innerHTML = transport.responseText;}
                });
            }

            function toggle_selection(check, day, mancaruri_id, price) {

                var priceTotal = $(day + "_price");
                var priceAlert = $(day + "_alert");
                var newPrice;

                if(check.className == "option_check state_unchecked") {
                    newPrice = price + parseFloat(priceTotal.innerHTML);
                    if(newPrice <= maxDailyPrice) {
                        priceTotal.innerHTML = newPrice;
                    }
                    else {
                        priceAlert.innerHTML = newPrice;
                        Effect.Appear(priceAlert, { duration: 1.0 });
                        Effect.Fade(priceAlert, { duration: 4.0, from: 1, to: 0 });
                        Effect.Fade(priceTotal, { duration: 5.0, from: 0, to: 1 });
                        return;
                    }
                }
                else {
                    newPrice = parseFloat(priceTotal.innerHTML) - price;
                    priceTotal.innerHTML = newPrice;
                }

                new Ajax.Request("menu.php?cmd=toggle&mancaruri_id=" + mancaruri_id + "&day=" + day,{
                    method: 'get',
                    onSuccess: function(transport){
                        if(transport.responseText == "1") {
                            check.className = "option_check state_checked";
                        }
                        else if(transport.responseText == "0") {
                            check.className = "option_check state_unchecked";
                        }
                        else {
                            alert("ERROR: " + transport.responseText);
                        }
                        updateMenu();
                    }
                });
            }
        </script>
    </head>
	<body onload="updateMenu()">
        <table cellspacing="0" cellpadding="0" style="border:0px;width:100%" >
            <tr>
                <td width="65%">
                    <div class="header_left">
                        <div class="loginout_cont">
                            <?php
                                if($_SESSION['loggedin']) {
                                    ?>
                                        <div class="welcome">
                                            <div class="language">
                                                <a href="?cmd=setlang&language=RO">RO</a>
                                                |
                                                <a href="?cmd=setlang&language=EN">EN</a>
                                            </div>
                                            <form action="" method="post" id="logoutform">
                                                <input type="hidden" name="cmd" value="logout"/>
                                            </form>
                                            <?php
                                                echo $texts['welcome'][$language] . " " . $_SESSION['user'] . ", <a href=\"#\" onclick=\"document.getElementById('logoutform').submit()\">" . $texts['leaving'][$language] . "</a> ?" ;
                                            ?>
                                        </div>
                                        <form action="" method="post">
                                            <input type="hidden" name="cmd" value="chpass"/>
                                            <div class="label"><?php echo $texts['newpassword'][$language];?></div>
                                            <div class="input_cont">
                                                <input type="password" class="input" name="password"/>
                                            </div>
                                            <input type="submit" class="okbutton" value="" />
                                        </form>
                                    <?php
                                }
                                else {
                                    ?>
                                        <form action="" method="post">
                                            <input type="hidden" name="cmd" value="login"/>
                                            <div class="label"><?php echo $texts['user'][$language];?></div>
                                            <div class="input_cont">
                                                <input type="text" class="input" name="user"/>
                                            </div>
                                            <div class="label"><?php echo $texts['password'][$language];?></div>
                                            <div class="input_cont">
                                                <input type="password" class="input" name="password"/>
                                            </div>
                                            <input type="submit" class="okbutton" value="" />
                                        </form>
                                    <?php
                                }
                            ?>

                        </div>
                        <div class="pic_cont">
                            <img src="admin/thumb.php?img_tip=4&img_url=../<?php echo $_SESSION['pic'];?>" />
                        </div>
                        <table cellspacing="0" cellpadding="0" style="border:0px;width:100%;margin-top:94px" >
                            <tr>
                                <?php
                                    $data = mysqli_query("SELECT * FROM intro WHERE 1 LIMIT 1") or die(mysqli_error());
                                    $row = mysqli_fetch_array($data);
                                    $titlu = $row['intro_titlu_' . $language];
                                    $text = $row['intro_text_' . $language];
                                ?>
                                <td width="180px">&nbsp;</td>
                                <td width="120px" style="border-top:1px solid blue" valign="top">
                                    <div class="header_titlu">
                                        <?php echo $titlu;?>&nbsp;
                                    </div>
                                </td>
                                <td style="border-top:1px solid blue" valign="top">
                                    <div class="header_text">
                                        <?php echo $text;?>&nbsp;
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </td>
                <td width="35%">
                    <div class="header_right" id="menu_cont"></div>
                </td>
            </tr>
        </table>
        <?php
            if($_SESSION['loggedin']) {
                ?>
                    <table cellspacing="0" cellpadding="0" style="border:0px;width:100%" >
                        <tr height="100px">
                            <td width="20px">&nbsp;</td>
                            <td class="bigmenu_dayname"><div class="bigmenu_lable monday"></div></td>
                            <td class="bigmenu_content">
                                <?php
                                    $pret = 0;
                                    while($row = mysqli_fetch_array($data_luni)) {
                                        if($row['utilizatori_id'])
                                            $pret += $row['mancaruri_add_numerics_0'];
                                        ?>
                                            <div class="option_cont">
                                                <div onclick="toggle_selection(this,'monday', <?php echo $row['mancaruri_id'];?>, <?php echo $row['mancaruri_add_numerics_0'];?>)" class="option_check <?php echo (($row['utilizatori_id'])?"state_checked":"state_unchecked");?>"></div>
                                                <div class="option_details">
                                                    <div class="option_name"><?php echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language];?></div>
                                                    <div class="option_price">[<?php echo $row['mancaruri_add_numerics_0'];?> Lei]</div>
                                                </div>
                                            </div>
                                        <?php
                                    }
                                ?>
                            </td>
                            <td width="115px" class="bigmenu_content">
                                <div class="note">
                                    <div class="note_label"><?php echo $texts['total'][$language];?>:</div>
                                    <div class="note_price" id="monday_price"><?php echo $pret;?></div>
                                    <div class="note_alert" id="monday_alert"></div>
                                </div>
                            </td>
                            <td width="20px"></td>
                        </tr>
                        <tr height="100px">
                            <td width="20px">&nbsp;</td>
                            <td class="bigmenu_dayname"><div class="bigmenu_lable tuesday"></div></td>
                            <td class="bigmenu_content">
                                <?php
                                    $pret = 0;
                                    while($row = mysqli_fetch_array($data_marti)) {
                                        if($row['utilizatori_id'])
                                            $pret += $row['mancaruri_add_numerics_0'];
                                        ?>
                                            <div class="option_cont">
                                                <div onclick="toggle_selection(this,'tuesday', <?php echo $row['mancaruri_id'];?>, <?php echo $row['mancaruri_add_numerics_0'];?>)" class="option_check <?php echo (($row['utilizatori_id'])?"state_checked":"state_unchecked");?>"></div>
                                                <div class="option_details">
                                                    <div class="option_name"><?php echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language];?></div>
                                                    <div class="option_price">[<?php echo $row['mancaruri_add_numerics_0'];?> Lei]</div>
                                                </div>
                                            </div>
                                        <?php
                                    }
                                ?>
                            </td>
                            <td width="115px" class="bigmenu_content">
                                <div class="note">
                                    <div class="note_label"><?php echo $texts['total'][$language];?>:</div>
                                    <div class="note_price" id="tuesday_price"><?php echo $pret;?></div>
                                    <div class="note_alert" id="tuesday_alert"></div>
                                </div>
                            </td>
                            <td width="20px">&nbsp;</td>
                        </tr>
                        <tr height="100px">
                            <td width="20px">&nbsp;</td>
                            <td class="bigmenu_dayname"><div class="bigmenu_lable wednsday"></div></td>
                            <td class="bigmenu_content">
                                <?php
                                    $pret = 0;
                                    while($row = mysqli_fetch_array($data_miercuri)) {
                                        if($row['utilizatori_id'])
                                            $pret += $row['mancaruri_add_numerics_0'];
                                        ?>
                                            <div class="option_cont">
                                                <div onclick="toggle_selection(this,'wednsday', <?php echo $row['mancaruri_id'];?>, <?php echo $row['mancaruri_add_numerics_0'];?>)" class="option_check <?php echo (($row['utilizatori_id'])?"state_checked":"state_unchecked");?>"></div>
                                                <div class="option_details">
                                                    <div class="option_name"><?php echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language];?></div>
                                                    <div class="option_price">[<?php echo $row['mancaruri_add_numerics_0'];?> Lei]</div>
                                                </div>
                                            </div>
                                        <?php
                                    }
                                ?>
                            </td>
                            <td width="115px" class="bigmenu_content">
                                <div class="note">
                                    <div class="note_label"><?php echo $texts['total'][$language];?>:</div>
                                    <div class="note_price" id="wednsday_price"><?php echo $pret;?></div>
                                    <div class="note_alert" id="wednsday_alert"></div>
                                </div>
                            </td>
                            <td width="20px">&nbsp;</td>
                        </tr>
                        <tr height="100px">
                            <td width="20px">&nbsp;</td>
                            <td class="bigmenu_dayname"><div class="bigmenu_lable thursday"></div></td>
                            <td class="bigmenu_content">
                                <?php
                                    $pret = 0;
                                    while($row = mysqli_fetch_array($data_joi)) {
                                        if($row['utilizatori_id'])
                                            $pret += $row['mancaruri_add_numerics_0'];
                                        ?>
                                            <div class="option_cont">
                                                <div onclick="toggle_selection(this,'thursday', <?php echo $row['mancaruri_id'];?>, <?php echo $row['mancaruri_add_numerics_0'];?>)" class="option_check <?php echo (($row['utilizatori_id'])?"state_checked":"state_unchecked");?>"></div>
                                                <div class="option_details">
                                                    <div class="option_name"><?php echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language];?></div>
                                                    <div class="option_price">[<?php echo $row['mancaruri_add_numerics_0'];?> Lei]</div>
                                                </div>
                                            </div>
                                        <?php
                                    }
                                ?>
                            </td>
                            <td width="115px" class="bigmenu_content">
                                <div class="note">
                                    <div class="note_label"><?php echo $texts['total'][$language];?>:</div>
                                    <div class="note_price" id="thursday_price"><?php echo $pret;?></div>
                                    <div class="note_alert" id="thursday_alert"></div>
                                </div>
                            </td>
                            <td width="20px">&nbsp;</td>
                        </tr>
                        <tr height="100px">
                            <td width="20px">&nbsp;</td>
                            <td class="bigmenu_dayname"><div class="bigmenu_lable friday"></div></td>
                            <td class="bigmenu_content">
                                <?php
                                    $pret = 0;
                                    while($row = mysqli_fetch_array($data_vineri)) {
                                        if($row['utilizatori_id'])
                                            $pret += $row['mancaruri_add_numerics_0'];
                                        ?>
                                            <div class="option_cont">
                                                <div onclick="toggle_selection(this,'friday', <?php echo $row['mancaruri_id'];?>, <?php echo $row['mancaruri_add_numerics_0'];?>)" class="option_check <?php echo (($row['utilizatori_id'])?"state_checked":"state_unchecked");?>"></div>
                                                <div class="option_details">
                                                    <div class="option_name"><?php echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language];?></div>
                                                    <div class="option_price">[<?php echo $row['mancaruri_add_numerics_0'];?> Lei]</div>
                                                </div>
                                            </div>
                                        <?php
                                    }
                                ?>
                            </td>
                            <td width="115px" class="bigmenu_content">
                                <div class="note">
                                    <div class="note_label"><?php echo $texts['total'][$language];?>:</div>
                                    <div class="note_price" id="friday_price"><?php echo $pret;?></div>
                                    <div class="note_alert" id="friday_alert"></div>
                                </div>
                            </td>
                            <td width="20px">&nbsp;</td>
                        </tr>
                    </table>
                <?php
            }
        ?>
    </body>
</html>