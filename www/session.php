<?php
    session_start();

    $_SESSION['loggedin'] = (isset($_SESSION['loggedin']))?$_SESSION['loggedin']:false;
    $_SESSION['user'] = (isset($_SESSION['user']))?$_SESSION['user']:"";
    $_SESSION['pic'] = (isset($_SESSION['pic']))?$_SESSION['pic']:"";
    $_SESSION['id'] = (isset($_SESSION['id']))?$_SESSION['id']:-1;

    //IF A LANGUAGE SET OPERATION WAS JUST PERFORMED SET SESSION VARIABLE
    if((isset($_REQUEST['cmd']))&&($_REQUEST['cmd']=="setlang"))
    {
        $_SESSION['language'] = $_REQUEST['language'];
    }
    if((isset($_REQUEST['cmd']))&&($_REQUEST['cmd']=="login"))
    {
        if(isset($_REQUEST))
        $user = $_REQUEST['user'];
        $pass = (($_REQUEST['password'] != "")?md5($_REQUEST['password']):"");
        $sql = "SELECT * FROM utilizatori WHERE utilizatori_titlu_RO = '" . $user . "' AND utilizatori_text_RO = '" . $pass . "'";
        $data = mysqli_query($sql) or die(mysqli_error() . $sql);
        if(mysqli_num_rows($data) > 0) {
            $row = mysqli_fetch_array($data);
            $_SESSION['loggedin'] = true;
            $_SESSION['user'] = $row['utilizatori_titlu_RO'];
            $_SESSION['pic'] = $row['utilizatori_pic'];
            $_SESSION['id'] = $row['utilizatori_id'];
        }

        $_SESSION['language'] = $_REQUEST['language'];
    }
    if((isset($_REQUEST['cmd']))&&($_REQUEST['cmd']=="logout"))
    {
        session_destroy();
        session_start();
        $_SESSION['loggedin'] = false;
        $_SESSION['user'] = "";
        $_SESSION['pic'] = "";
        $_SESSION['id'] = -1;

        $_SESSION['language'] = $_REQUEST['language'];
    }
    if((isset($_REQUEST['cmd']))&&($_REQUEST['cmd']=="chpass"))
    {
        if($_SESSION['loggedin']) {
            $sql = "UPDATE utilizatori SET utilizatori_text_RO = '" . md5($_REQUEST['password']) . "'";
            mysqli_query($sql) or die(mysqli_error() . $sql);
        }
        $data = mysqli_query($sql) or die(mysqli_error() . $sql);
    }
    $language = (isset($_SESSION['language']))?$_SESSION['language']:"RO";

?>