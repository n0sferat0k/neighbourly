<?php
$no_pic = "graphics/no_pic.jpg";
$img_src = $_REQUEST['img_url'];
$tip = $_REQUEST['img_tip'];

if (!@file_exists($img_src)) {    
    $img_src = $no_pic;
}
if (is_dir($img_src)) {
    $img_src = $no_pic;
}


switch ($tip) {
        //*****************************************************
        //*****************************************************
        //*****************************************************
        // USED IN ADMIN DO NOT DELETE !!!!!!!!!!!!!!!!!!!!!!!!
    case '0':
        $MAX_WIDTH = 100;
        $MAX_HEIGHT = 100;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '1':
        $MAX_WIDTH = 110;
        $MAX_HEIGHT = 50;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '11':
        $MAX_WIDTH = 30;
        $MAX_HEIGHT = 30;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '12':
        $MAX_WIDTH = 50;
        $MAX_HEIGHT = 50;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '2':
        $MAX_WIDTH = 40;
        $MAX_HEIGHT = 40;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '3':
        $MAX_WIDTH = 100;
        $MAX_HEIGHT = 120;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 0;
        $Vcenter = true;
        $Hcenter = true;
        break;
    case '4':
        $MAX_WIDTH = 70;
        $MAX_HEIGHT = 70;
        $R = 255;
        $G = 255;
        $B = 255;
        $frame_type = 1;
        $Vcenter = true;
        $Hcenter = true;
        break;
        //*****************************************************
        //*****************************************************
        //*****************************************************

}


$what = @getimagesize($img_src);
switch ($what['mime']) {
    case 'image/gif':
        header('content-type: image/gif');
        break;
    case 'image/jpeg':
        header('content-type: image/jpeg');
        break;
    case 'image/png':
        header('content-type: image/png');
        break;
    case 'image/bmp':
        header('content-type: image/bmp');
        break;
}

switch ($what['mime']) {
    case 'image/gif':
        $image = @imagecreatefromgif($img_src);
        break;
    case 'image/jpeg':
        $image = @imagecreatefromjpeg($img_src);
        break;
    case 'image/png':
        $image = @imagecreatefrompng($img_src);
        break;
    case 'image/bmp':
        $image = @imagecreatefrombmp($img_src);
        break;
}

$imgWidth = $what[0];
$imgHeight = $what[1];

//if (($imgWidth>$MAX_WIDTH)||($imgHeight>$MAX_HEIGHT))
if ($imgWidth / $imgHeight > $MAX_WIDTH / $MAX_HEIGHT) {
    $imgHeight = $MAX_WIDTH * $imgHeight / $imgWidth;
    $imgWidth = $MAX_WIDTH;
} else {
    $imgWidth = $MAX_HEIGHT * $imgWidth / $imgHeight;
    $imgHeight = $MAX_HEIGHT;
}

switch ($frame_type) {
    case 0:
        $img = imagecreatetruecolor($MAX_WIDTH, $MAX_HEIGHT);
        break;
    case 1:
        $img = imagecreatetruecolor($MAX_WIDTH + 6, $MAX_HEIGHT + 6);
        break;
    default:;
}
$col = imagecolorallocate($img, $R, $G, $B);
imagefill($img, 0, 0, $col);


if ($Hcenter)
    $Hdif = round((($MAX_WIDTH - $imgWidth) / 2));
else
    $Hdif = 0;

if ($Vcenter)
    $Vdif = round((($MAX_HEIGHT - $imgHeight) / 2));
else
    $Vdif = 0;


switch ($frame_type) {
    case 0:
        imagecopyresampled($img, $image, $Hdif, $Vdif, 0, 0, $imgWidth, $imgHeight, $what[0], $what[1]);
        break;
    case 1:

        $col = imagecolorallocate($img, 165, 165, 165);
        imagerectangle($img, $Hdif, $Vdif, $Hdif + $imgWidth + 5, $Vdif + $imgHeight + 5, $col);
        $col = imagecolorallocate($img, 247, 247, 247);
        imagerectangle($img, $Hdif + 1, $Vdif + 1, $Hdif + $imgWidth + 6, $Vdif + $imgHeight + 4, $col);
        $col = imagecolorallocate($img, 255, 255, 255);
        imagerectangle($img, $Hdif + 2, $Vdif + 2, $Hdif + $imgWidth + 3, $Vdif + $imgHeight + 3, $col);

        imagecopyresampled($img, $image, $Hdif + 3, $Vdif + 3, 0, 0, $imgWidth, $imgHeight, $what[0], $what[1]);
        break;
    default:;
}

// output
switch ($what['mime']) {
    case 'image/gif':
        @imagegif($img);
        @imagegif($img, $img_dst);
        break;
    case 'image/jpeg':
        @imagejpeg($img);
        @imagejpeg($img, $img_dst);
        break;
    case 'image/png':
        @imagepng($img);
        @imagepng($img, $img_dst);
        break;
    case 'image/bmp':
        @imagebmp($img);
        @imagebmp($img, $img_dst);
        break;
}

// destroy
@imagedestroy($img);
