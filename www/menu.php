<?php
    error_reporting(E_ERROR|E_PARSE|E_STRICT);
    include("admin/ADMIN_INCLUDE/connect_db.inc.php");
    include("session.php");
    include("texts.php");

    if($_SESSION['loggedin']) {

        if((isset($_REQUEST['cmd'])) && ($_REQUEST['cmd'] == "toggle")) {
            $data = mysqli_query("SELECT * FROM alegeri WHERE mancaruri_id = " . $_REQUEST['mancaruri_id'] . " AND day = '" . $_REQUEST['day'] . "' AND valid = 1 and utilizatori_id = " . $_SESSION['id']) or die(mysqli_error());
            if(mysqli_num_rows($data) > 0) {
                while($row = mysqli_fetch_array($data)) {
                    mysqli_query("DELETE FROM alegeri WHERE alegeri_id = " . $row['alegeri_id']) or die(mysqli_error());
                }
                die("0");
            }
            else {
                mysqli_query("INSERT INTO alegeri (mancaruri_id, utilizatori_id, day, valid) VALUES (" . $_REQUEST['mancaruri_id'] . ", " . $_SESSION['id'] . ", '" . $_REQUEST['day'] . "', 1)") or die(mysqli_error());
                die("1");
            }

        }

        $sql_luni = "SELECT * FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) LEFT JOIN meniu_luni ME ON (A.mancaruri_id = ME.meniu_luni_add_numerics_0) LEFT JOIN mancaruri M ON (ME.meniu_luni_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_luni_add_numerics_1 = C.categorii_id) WHERE A.valid = 1 AND ME.meniu_luni_add_numerics_0 IS NOT NULL AND A.day = 'monday'";
        $sql_marti = "SELECT * FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) LEFT JOIN meniu_marti ME ON (A.mancaruri_id = ME.meniu_marti_add_numerics_0) LEFT JOIN mancaruri M ON (ME.meniu_marti_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_marti_add_numerics_1 = C.categorii_id) WHERE A.valid = 1 AND ME.meniu_marti_add_numerics_0 IS NOT NULL AND A.day = 'tuesday'";
        $sql_miercuri = "SELECT * FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) LEFT JOIN meniu_miercuri ME ON (A.mancaruri_id = ME.meniu_miercuri_add_numerics_0) LEFT JOIN mancaruri M ON (ME.meniu_miercuri_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_miercuri_add_numerics_1 = C.categorii_id) WHERE A.valid = 1 AND ME.meniu_miercuri_add_numerics_0 IS NOT NULL AND A.day = 'wednsday'";
        $sql_joi = "SELECT * FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) LEFT JOIN meniu_joi ME ON (A.mancaruri_id = ME.meniu_joi_add_numerics_0) LEFT JOIN mancaruri M ON (ME.meniu_joi_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_joi_add_numerics_1 = C.categorii_id) WHERE A.valid = 1 AND ME.meniu_joi_add_numerics_0 IS NOT NULL AND A.day = 'thursday'";
        $sql_vineri = "SELECT * FROM alegeri A LEFT JOIN utilizatori U ON (U.utilizatori_id = A.utilizatori_id) LEFT JOIN meniu_vineri ME ON (A.mancaruri_id = ME.meniu_vineri_add_numerics_0) LEFT JOIN mancaruri M ON (ME.meniu_vineri_add_numerics_0 = M.mancaruri_id) LEFT JOIN categorii C ON (ME.meniu_vineri_add_numerics_1 = C.categorii_id) WHERE A.valid = 1 AND ME.meniu_vineri_add_numerics_0 IS NOT NULL AND A.day = 'friday'";


        $data_luni = mysqli_query($sql_luni)or die(mysqli_error());
        $data_marti = mysqli_query($sql_marti)or die(mysqli_error());
        $data_miercuri = mysqli_query($sql_miercuri)or die(mysqli_error());
        $data_joi = mysqli_query($sql_joi)or die(mysqli_error());
        $data_vineri = mysqli_query($sql_vineri)or die(mysqli_error());

        ?>
            <link rel="stylesheet" type="text/css" href="css/general.css"/>
            <div class="menutitle"><?php echo $texts['yourchoices'][$language];?></div>
            <table cellspacing="0" cellpadding="0" style="width:100%">
                <?php
                    if(mysqli_num_rows($data_luni) > 0) {
                        ?>
                            <tr>
                                <td width="15%" valign="top" class="menulable"><?php echo $texts['monday'][$language]?>:</td>
                                <td width="85%" valign="top" class="menuentry">
                                    <?php
                                        while($row = mysqli_fetch_array($data_luni)) {
                                            echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language] . "<br />";
                                        }
                                    ?>
                                </td>
                            </tr>
                        <?php
                    }
                    if(mysqli_num_rows($data_marti) > 0) {
                        ?>
                            <tr>
                                <td width="15%" valign="top" class="menulable"><?php echo $texts['tuesday'][$language]?>:</td>
                                <td width="85%" valign="top" class="menuentry">
                                    <?php
                                        while($row = mysqli_fetch_array($data_marti)) {
                                            echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language] . "<br />";
                                        }
                                    ?>
                                </td>
                            </tr>
                        <?php
                    }
                    if(mysqli_num_rows($data_miercuri) > 0) {
                        ?>
                            <tr>
                                <td width="15%" valign="top" class="menulable"><?php echo $texts['wednsday'][$language]?>:</td>
                                <td width="85%" valign="top" class="menuentry">
                                    <?php
                                        while($row = mysqli_fetch_array($data_miercuri)) {
                                            echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language] . "<br />";
                                        }
                                    ?>
                                </td>
                            </tr>
                        <?php
                    }
                    if(mysqli_num_rows($data_joi) > 0) {
                        ?>
                            <tr>
                                <td width="15%" valign="top" class="menulable"><?php echo $texts['thursday'][$language]?>:</td>
                                <td width="85%" valign="top" class="menuentry">
                                    <?php
                                        while($row = mysqli_fetch_array($data_joi)) {
                                            echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language] . "<br />";
                                        }
                                    ?>
                                </td>
                            </tr>
                        <?php
                    }
                    if(mysqli_num_rows($data_vineri) > 0) {
                        ?>
                            <tr>
                                <td width="15%" valign="top" class="menulable"><?php echo $texts['friday'][$language]?>:</td>
                                <td width="85%" valign="top" class="menuentry">
                                    <?php
                                        while($row = mysqli_fetch_array($data_vineri)) {
                                            echo $row['categorii_titlu_' . $language] . " - " . $row['mancaruri_titlu_' . $language] . "<br />";
                                        }
                                    ?>
                                </td>
                            </tr>
                        <?php
                    }
                ?>
            </table>
        <?php
    }
?>
