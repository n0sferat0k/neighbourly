<?php
include_once("./admin/ADMIN_INCLUDE/connect_db.inc.php");

if (isset($_REQUEST['userid'])) {
    $users = mysqli_query($connection, "SELECT 
        users_titlu_EN AS name, 
        users_pic AS image, 
        users_add_numerics_0 / 10000000 AS latitude, 
        users_add_numerics_1 / 10000000 AS longitude
        FROM users WHERE users_id = " . mysqli_real_escape_string($connection, $_REQUEST['userid']) .  " 
        LIMIT 1") or die(mysqli_error($connection));
    $user = mysqli_fetch_assoc($users);
}
?>
<!DOCTYPE html>
<html lang="en">

<head>
    <title>Neighbourly Map</title>
    <meta charset='utf-8'>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel='stylesheet' href='https://unpkg.com/maplibre-gl@4.6.0/dist/maplibre-gl.css' />
    <script src='https://unpkg.com/maplibre-gl@4.6.0/dist/maplibre-gl.js'></script>
    <style>
        body {
            margin: 0;
            padding: 0;
        }

        html,
        body,
        #map {
            height: 100%;
        }
    </style>
</head>

<body>
    <div id="map"></div>

    <script>        
        const map = new maplibregl.Map({
            container: 'map',
            style: 'https://api.maptiler.com/maps/streets/style.json?key=VdbWJipihjTW3mb6JxNK',
            center: [0, 0],
            zoom: 15
        });
        <?php
        if (isset($user)) {            
            $coords = "[" . $user['longitude'] . "," . $user['latitude'] . "]";
            $thumb = "admin/thumb.php?img_tip=12&img_url=../" . $user['image'];            
        ?>            
            map.setCenter(<?php echo $coords ?>);

            // create a DOM element for the marker
            const el = document.createElement('div');
            el.className = 'marker';
            el.style.backgroundImage = `url(<?php echo $thumb; ?>)`;            
            el.style.width = `50px`;
            el.style.height = `50px`;

            // add marker to map
            // new maplibregl.Marker({
            //         element: el
            //     })
            //     .setLngLat(<?php echo $coords ?>)
            //     .addTo(map);


            // Add a marker at the user's location
            // new maplibregl.Marker()
            //     .setLngLat(<?php echo $coords ?>)
            //     .addTo(map);
        <?php
        }
        ?>

        map.on('load', () => {
                map.addSource('maine', {
                    'type': 'geojson',
                    'data': {
                        'type': 'Feature',
                        'geometry': {
                            'type': 'Polygon',
                            'coordinates': [
                                [
                                    [-67.13734351262877, 45.137451890638886],
                                    [-66.96466, 44.8097],
                                    [-68.03252, 44.3252],
                                    [-69.06, 43.98],
                                    [-70.11617, 43.68405],
                                    [-70.64573401557249, 43.090083319667144],
                                    [-70.75102474636725, 43.08003225358635],
                                    [-70.79761105007827, 43.21973948828747],
                                    [-70.98176001655037, 43.36789581966826],
                                    [-70.94416541205806, 43.46633942318431],
                                    [-71.08482, 45.3052400000002],
                                    [-70.6600225491012, 45.46022288673396],
                                    [-70.30495378282376, 45.914794623389355],
                                    [-70.00014034695016, 46.69317088478567],
                                    [-69.23708614772835, 47.44777598732787],
                                    [-68.90478084987546, 47.184794623394396],
                                    [-68.23430497910454, 47.35462921812177],
                                    [-67.79035274928509, 47.066248887716995],
                                    [-67.79141211614706, 45.702585354182816],
                                    [-67.13734351262877, 45.137451890638886]
                                ]
                            ]
                        }
                    }
                });
                map.addLayer({
                    'id': 'maine',
                    'type': 'fill',
                    'source': 'maine',
                    'layout': {},
                    'paint': {
                        'fill-color': '#5BA9AE',
                        'fill-opacity': 0.8
                    }
                });

                setDot(47.6530671, 23.6684110, 'current');
                window.setTimeout(function() {
                    setDot(47.6535671, 23.6684110, 'current');
                }, 2000);                
            });

            function setDot(latitude, longitude, id) {
                if (map.getSource(id) !== undefined) {                    
                    map.getSource(id).setData({
                        'type': 'Point',
                        'coordinates': [longitude, latitude]
                    });
                } else {
                    map.addSource(id, {
                        'type': 'geojson',
                        'data': {
                            'type': 'Point',
                            'coordinates': [longitude, latitude]
                        }
                    });
                    map.addLayer({
                        'id': id,
                        'source': id,
                        'type': 'circle',
                        'paint': {
                            'circle-radius': 10,
                            'circle-color': '#007cbf'
                        }
                    });
                }
            }
    </script>
</body>

</html>