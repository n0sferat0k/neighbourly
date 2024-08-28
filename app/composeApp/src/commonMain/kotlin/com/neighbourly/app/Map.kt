package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLFile
import neighbourly.composeapp.generated.resources.Res


@Composable
fun Map(modifier: Modifier) {
    val state = rememberWebViewStateWithHTMLData( data = html).apply {
        webSettings.isJavaScriptEnabled = true
    }
    val navigator = rememberWebViewNavigator()

//    LaunchedEffect(Unit) {
//        delay(25000)
//        navigator.evaluateJavaScript("new maplibregl.Marker().setLngLat([23.6684110, 47.6530671]).addTo(map);")
//    }

    WebView(
        state = state,
        modifier = modifier,
        navigator = navigator
    )
}
val html = """
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
    
            map.on('load', () => {
                setDot(47.6530671, 23.6684110, 'current');
                center(47.6530671, 23.6684110, 15);
                window.setTimeout(function() {
                        setDot(47.6540671, 23.6684110, 'current');
                        center(47.6540671, 23.6684110, 15);
                    }, 10000); 
            });
    
            function center(latitude, longitudem, zoom) {
                map.flyTo({
                    center: [longitudem, latitude],
                    zoom: zoom
                });
            }
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
""".trimIndent()