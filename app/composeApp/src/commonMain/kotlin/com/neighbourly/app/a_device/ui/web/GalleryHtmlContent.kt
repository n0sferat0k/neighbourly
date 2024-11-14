package com.neighbourly.app.a_device.ui.web

fun galleryHtml(images:Map<Int, String>) = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Image Gallery</title>
        <link href="http://neighbourly.go.ro/css/lightbox.min.css" rel="stylesheet">
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
        <div class="gallery">
        ${
            images.map { (key, image) ->
                """
                    <a href="$image" data-lightbox="itemgallery" data-title="Image $key">
                        <img class="image" src="$image" alt="Image $key">
                    </a>                        
                """.trimIndent()
            }.joinToString()            
        }   
        </div>
        <script src="http://neighbourly.go.ro/js/lightbox-plus-jquery.min.js"></script>        
    </body>
</html>
""".trimIndent()