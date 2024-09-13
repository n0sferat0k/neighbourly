package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    val state by mapViewModel.state.collectAsState()

    val webViewState =
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
                    params.householdid?.let {
                        mapViewModel.onHouseholSelected(it)
                    }
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
        navigator.evaluateJavaScript("setDot($lng, $lat, 'current');")
        if (firstGps) {
            navigator.evaluateJavaScript("center($lng, $lat, 15);")
            firstGps = false
        }
    }

    DisposableEffect(mapReady) {
        if (mapReady) {
            GetLocation.addCallback(callback)
        }
        onDispose {
            GetLocation.removeCallback(callback)
        }
    }

    LaunchedEffect(mapReady, state.household) {
        if (mapReady) {
            state.household.let {
                when (it) {
                    null ->
                        navigator.evaluateJavaScript("clearHouseholds()")
                    else -> {
                        val image = it.imageurl ?: "http://neighbourly.go.ro/graphics/houses.png"
                        navigator.evaluateJavaScript("addHousehold(${it.longitude}, ${it.latitude}, ${it.id}, '${it.name}', '$image')")
                        navigator.evaluateJavaScript("center(${it.longitude}, ${it.latitude}, 15);")
                    }
                }
            }
        }
    }

    LaunchedEffect(mapReady, state.neighbourhoods) {
        if (mapReady) {
            if (state.neighbourhoods.isEmpty()) {
                navigator.evaluateJavaScript("clearNeighbourhoods()")
            } else {
                state.neighbourhoods.forEach {
                    navigator.evaluateJavaScript("addNeighbourhood('${it.id}', ${it.geofence})")
                }
            }
        }
    }

    WebView(
        state = webViewState,
        modifier = modifier,
        navigator = navigator,
        webViewJsBridge = jsBridge,
    )
}

@Serializable
data class MapFeedbackModel(
    val mapReady: Boolean? = null,
    val drawData: List<List<Double>>? = null,
    val householdid: Int? = null,
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
                var households = [];
                var neighbourhoods = [];
        
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
                    mapLoaded = true;
    
                    window.kmpJsBridge.callNative("MapFeedback", "{\"mapReady\":true}", function (data) { });
//                    addHousehold(23.6684, 47.6531, 1, "Krucz House", 'http://neighbourly.go.ro/householdsIMGS/boiling-frog.png');
//                    
//                    addNeighbourhood("1", [[23.664421990469435,47.65655129393659],[23.665995201870714,47.64961446944116],[23.670235774323487,47.64969293384053],[23.668970442219916,47.653967631296325],[23.67055763940516,47.656120831152805],[23.6676487837274,47.65622253206254],[23.664421990469435,47.65655129393659]]);
//                    setDot(23.6684, 47.6531, 'current');
//                    center(23.6684, 47.6531, 15)
//                    
//                    window.setTimeout(function() {
//                        addHousehold(23.6784, 47.6531, 1, "Krucz House Moved", 'http://neighbourly.go.ro/householdsIMGS/boiling-frog.png');                    
//                    }, 10000);
//    
//                    window.setTimeout(function() {
//                        addHousehold(23.6884, 47.6531, 2, "Not Krucz House", 'http://neighbourly.go.ro/householdsIMGS/boiling-frog.png');                    
//                    }, 15000);
//                    
//                    window.setTimeout(function() {                    
//                        clearHouseholds();
//                        clearNeighbourhoods();
//                    }, 20000);
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
    
                function clearNeighbourhoods() {
                    for (var key in neighbourhoods) {
                        map.removeLayer(neighbourhoods[key]);
                        map.removeSource(neighbourhoods[key]);
                    }               
                    neighbourhoods = [];                
                }
    
                function addNeighbourhood(id, data) {
                    addGeofence("neighbourhood_" + id, data);
                    neighbourhoods.push("neighbourhood_" + id);
                }
    
                function addGeofence(id, data) {
                    if (mapLoaded) {
                        if (map.getSource(id) !== undefined) {
                            map.getSource(id).setData({
                                'type': 'Feature',
                                'geometry': {
                                    'type': 'Polygon',
                                    'coordinates': [data]
                                }
                            });
                        } else {
                            map.addSource(id, {
                                'type': 'geojson',
                                'data': {
                                    'type': 'Feature',
                                    'geometry': {
                                        'type': 'Polygon',
                                        'coordinates': [data]
                                    }
                                }
                            });
                            map.addLayer({
                                'id': id,
                                'type': 'fill',
                                'source': id,
                                'layout': {},
                                'paint': {
                                    'fill-color': '#ae605b',
                                    'fill-opacity': 0.4
                                }
                            });
                        }
                    }
                }    
        
                function center(longitude, latitude, zoom) {
                    if (mapLoaded) {
                        map.flyTo({
                            center: [longitude, latitude],
                            zoom: zoom
                        });
                    } else {
                        lng = longitude;
                        lat = latitude;                    
                    }
                }
    
                function clearHouseholds() {
                    for (var key in households) {
                        households[key].remove();
                    }
                    households = [];
                }
    
                function addHousehold(longitude, latitude, id, name, imageurl) {
                    if (mapLoaded) {    
                        var el = document.getElementById("household_" + id);
                        if(el == null) {
                            el = document.createElement('div');
                            el.id = "household_" + id;
                            el.className = 'marker';
    
                            households[id] = new maplibregl.Marker({ element: el }).setLngLat([longitude, latitude])                                        
                            households[id].addTo(map);
                        } else {
                            households[id].setLngLat([longitude, latitude]);
                        }
                        
                        el.innerHTML = `<div style="display: flex; flex-direction: column; align-items: center;">
                                            <span style="background:white;padding:4px;border: 2px solid #5BA9AE;border-radius:10px;">` +  name + `</span>
                                            <div style="background:url(`+ imageurl + `);background-size:50px;width:50px;height:50px;border: 2px solid #5BA9AE;border-radius:50%;"></div>                                
                                        </div>`;
                                        
                        el.onclick = function() {
                            window.kmpJsBridge.callNative("MapFeedback", JSON.stringify({ householdid: id }), function (data) { });
                        } 
                    }
                }
                    
                function setDot(longitude, latitude, id) {
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
                                    'circle-radius': 8,
                                    'circle-color': '#5BA9AE'
                                }
                            });
                        }
                    } else {
                        lng = longitude;
                        lat = latitude;                    
                    }
                }
            </script>
        </body>
    </html>
    """.trimIndent()
