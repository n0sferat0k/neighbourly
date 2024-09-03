package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.neighbourly.app.GeoLocationCallback
import com.neighbourly.app.GetLocation


@Composable
fun Map(modifier: Modifier) {
    val state = rememberWebViewStateWithHTMLData(data = html).apply {
        webSettings.isJavaScriptEnabled = true
    }
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()
    var firstGps by remember { mutableStateOf(true) }
    var loadGps by remember { mutableStateOf(false) }

    LaunchedEffect(jsBridge) {
        jsBridge.register(object : IJsMessageHandler {

            override fun methodName(): String {
                return "MapReady"
            }

            override fun handle(
                message: JsMessage,
                navigator: WebViewNavigator?,
                callback: (String) -> Unit
            ) {
                loadGps = true
            }
        })
    }

    val callback: GeoLocationCallback = { lat, lng, acc ->
        navigator.evaluateJavaScript("setDot($lat, $lng, 'current');")
        if (firstGps) {
            navigator.evaluateJavaScript("center($lat, $lng, 15);")
            firstGps = false
        }
    }

    DisposableEffect(loadGps) {
        if (loadGps) {
            GetLocation.addCallback(callback)
        }
        onDispose {
            GetLocation.removeCallback(callback)
        }
    }

    WebView(
        state = state,
        modifier = modifier,
        navigator = navigator,
        webViewJsBridge = jsBridge,
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
            var mapLoaded = false;
            var lat = 0;
            var lng = 0;
            const map = new maplibregl.Map({
                container: 'map',
                style: 'https://api.maptiler.com/maps/streets/style.json?key=VdbWJipihjTW3mb6JxNK',
                center: [0, 0],
                zoom: 1
            });
        
            map.on('load', () => {
                window.kmpJsBridge.callNative("MapReady","{}",function (data) {});
                mapLoaded = true;
                if(lat != 0 && lng != 0) {
                    setDot(lng, lat, 'current');
                    center(lng, lat, 15);
                }                
            });
    
            function center(latitude, longitude, zoom) {
                if(mapLoaded) {
                    map.flyTo({
                        center: [longitude, latitude],
                        zoom: zoom
                    });
                } else {
                    lat = latitude;
                    lng = longitude;
                }
            }
            function setDot(latitude, longitude, id) {
                if(mapLoaded) {
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
                } else {
                    lat = latitude;
                    lng = longitude;
                }
            }
        </script>
    </body>
    
    </html>
""".trimIndent()