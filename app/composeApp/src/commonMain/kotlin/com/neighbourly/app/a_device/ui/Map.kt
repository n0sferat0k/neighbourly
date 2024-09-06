package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import com.neighbourly.app.GeoLocationCallback
import com.neighbourly.app.GetLocation
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.MapViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Composable
fun Map(
    mapViewModel: MapViewModel = viewModel { KoinProvider.KOIN.get<MapViewModel>() },
    modifier: Modifier,
) {
    val state =
        rememberWebViewStateWithHTMLData(data = html).apply {
            webSettings.isJavaScriptEnabled = true
        }
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()
    var firstGps by remember { mutableStateOf(true) }
    var mapReady by remember { mutableStateOf(false) }

    LaunchedEffect(jsBridge) {
        jsBridge.register(
            object : IJsMessageHandler {
                override fun methodName(): String = "MapFeedback"

                override fun handle(
                    message: JsMessage,
                    navigator: WebViewNavigator?,
                    callback: (String) -> Unit,
                ) {
                    val params = Json.decodeFromString<MapFeedbackModel>(message.params)

                    if (params.mapReady == true) {
                        mapReady = true
                    }
                    if (params.drawData != null) {
                        mapViewModel.onDrawn(params.drawData)
                    }
                }
            },
        )
    }

    val callback: GeoLocationCallback = { lat, lng, acc ->
        navigator.evaluateJavaScript("setDot($lat, $lng, 'current');")
        if (firstGps) {
            navigator.evaluateJavaScript("center($lat, $lng, 15);")
            firstGps = false
        }
    }

    DisposableEffect(mapReady) {
        if (mapReady) {
            GetLocation.addCallback(callback)
            // navigator.evaluateJavaScript("setDot($lat, $lng, 'current');")
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

@Serializable
data class MapFeedbackModel(
    val mapReady: Boolean? = null,
    val drawData: List<List<Double>>? = null,
)

val html =
    """
    <!DOCTYPE html>
    <html lang="en">
    
    <head>
        <title>Neighbourly Map</title>
        <meta charset='utf-8'>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel='stylesheet' href='https://unpkg.com/maplibre-gl@4.6.0/dist/maplibre-gl.css' />
        <script src='https://unpkg.com/maplibre-gl@4.6.0/dist/maplibre-gl.js'></script>
    
        <script src="https://www.unpkg.com/turf@3.0.14/turf.min.js"></script>
        <script src="https://www.unpkg.com/@mapbox/mapbox-gl-draw@1.4.3/dist/mapbox-gl-draw.js"></script>
        <link rel="stylesheet" href="https://www.unpkg.com/@mapbox/mapbox-gl-draw@1.4.3/dist/mapbox-gl-draw.css" />
    
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
    
            MapboxDraw.constants.classes.CONTROL_BASE = 'maplibregl-ctrl';
            MapboxDraw.constants.classes.CONTROL_PREFIX = 'maplibregl-ctrl-';
            MapboxDraw.constants.classes.CONTROL_GROUP = 'maplibregl-ctrl-group';
    
            const map = new maplibregl.Map({
                container: 'map',
                style: 'https://api.maptiler.com/maps/streets/style.json?key=VdbWJipihjTW3mb6JxNK',
                center: [0, 0],
                zoom: 1
            });
    
            map.on('load', () => {                
                window.kmpJsBridge.callNative("MapFeedback", "{\"mapReady\":true}", function (data) { });
                mapLoaded = true;
                if (lat != 0 && lng != 0) {
                    setDot(lng, lat, 'current');
                    center(lng, lat, 15);
                }
            });
    
            function enableDraw() {
                const draw = new MapboxDraw({
                    displayControlsDefault: false,
                    controls: {
                        polygon: true,
                        trash: true
                    }
                });
                map.addControl(draw);
    
                updateArea = function(e) {                    
                    if (draw.getAll().features.length > 0) {
                        const data = draw.getAll().features[0].geometry.coordinates[0].map(function (point) {
                            return [point[0], point[1]];
                        });                
                        window.kmpJsBridge.callNative("MapFeedback", JSON.stringify({ drawData: data }), function (data) { });
                    }
                }
    
                map.on('draw.create', updateArea);
                map.on('draw.delete', updateArea);
                map.on('draw.update', updateArea);
            }
    
            function center(latitude, longitude, zoom) {
                if (mapLoaded) {
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
                if (mapLoaded) {
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
