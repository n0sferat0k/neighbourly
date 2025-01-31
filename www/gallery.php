<?php 
    error_reporting(E_ALL);
    include_once("./admin/ADMIN_INCLUDE/connect_db.inc.php");
    
    if (isset($_REQUEST['itemId'])) {    
        $images = mysqli_query(
            mysql: $connection, 
            query: "SELECT items_imgs_pic AS image, items_imgs_id AS id FROM items_imgs WHERE items_id = " . mysqli_real_escape_string($connection, $_REQUEST['itemId'])
        ) or die(mysqli_error($connection));                
    }
    if (isset($_REQUEST['householdId'])) {    
        $images = mysqli_query(
            mysql: $connection, 
            query: "SELECT households_pic AS image, households_id AS id FROM households WHERE households_id = " . mysqli_real_escape_string($connection, $_REQUEST['householdId'])
        ) or die(mysqli_error($connection));                
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Image Gallery</title>    
    <link href="css/lightbox.css" rel="stylesheet">
    <style>
        .gallery {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
        }
        .image {
            display: block;
            margin: 10px;
            float: left;
            width: 160px;
            height: 160px;
            overflow: hidden;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 5px rgb(0, 0, 0);
            transition: 0.3s;
            object-fit: cover;
        }
    </style>
</head>
<body>
    <div class="gallery" id="gallery">
        <?php
            while($image = mysqli_fetch_assoc($images)) {
                ?>
                    <a href="<?php echo $image['image'];?>" data-lightbox="mygallery" data-title="Image <?php echo $image['id'];?>" id="image_<?php echo $image['id'];?>">
                        <img class="image" src="<?php echo $image['image'];?>" alt="Image <?php echo $image['id'];?>">
                    </a>    
                <?php
            }
        ?>
    </div>    
    <script src="js/lightbox-plus-jquery.js"></script>
    <script src="js/jquery-ui.js"></script>
    <script src="js/jquery-mobile.js"></script>
        
    <script>
      lightbox.option({
        'enableSwipeOnTouchDevices': true,
        'alwaysShowNavOnTouchDevices': true,
      })
        <?php
            if (isset($_REQUEST['imageId'])) {
                ?>
                    window.onload = function() {
                        window.setTimeout(function() {          
                            $('#image_<?php echo $_REQUEST['imageId'];?>').click();
                        }, 500);        
                    }  
                <?php
            }                
        ?>
    </script>       
</body>
</html>

