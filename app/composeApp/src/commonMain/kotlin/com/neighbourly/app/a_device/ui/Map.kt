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
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Composable
fun Map(
    viewModel: MapViewModel = viewModel { KoinProvider.KOIN.get<MapViewModel>() },
    navigation: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    modifier: Modifier,
    onDrawn: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsState()

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
                        navigation.goToFindItems(ItemType.getByName(params.type), it)
                    }
                    if (params.mapReady == true) {
                        mapReady = true
                    }
                    if (params.drawData != null) {
                        viewModel.onDrawn(params.drawData)
                        onDrawn?.let { it() }
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
            viewModel.refreshMapContent()
        }
        onDispose {
            GetLocation.removeCallback(callback)
        }
    }

    LaunchedEffect(mapReady, state.lastSyncTs) {
        if (state.lastSyncTs > 0) {
            viewModel.refreshMapContent()
        }
    }

    LaunchedEffect(mapReady, state.drawing) {
        if (state.drawing) {
            navigator.evaluateJavaScript("enableDraw()")
        } else {
            navigator.evaluateJavaScript("disableDraw()")
        }
    }

    LaunchedEffect(mapReady, state.otherHouseholds, state.myHousehold, state.candidate) {
        if (mapReady) {
            val housesToShow = state.otherHouseholds.toMutableList()

            state.myHousehold?.let { household ->
                housesToShow.add(household)
                if (firstGps) {
                    navigator.evaluateJavaScript("center(${household.location.longitude}, ${household.location.latitude}, 15);")
                    firstGps = false
                }
            }

            if (housesToShow.isEmpty()) {
                navigator.evaluateJavaScript("clearHouseholds()")
            } else {
                val js = housesToShow.map {
                    "{" +
                            "'longitude':${it.location.longitude}, " +
                            "'latitude':${it.location.latitude}, " +
                            "'id':${it.id}, " +
                            "'name':'${it.name}', " +
                            "'floatName':'${it.floatName}', " +
                            "'address':'${it.address}', " +
                            "'description':'${it.description}', " +
                            "'donations':${it.donations}, " +
                            "'barterings':${it.barterings}, " +
                            "'sales':${it.sales}, " +
                            "'events':${it.events}, " +
                            "'needs':${it.needs}, " +
                            "'requests':${it.requests}, " +
                            "'skillshare':${it.skillshare}, " +
                            "'imageurl':'${it.imageurl ?: ""}'" +
                            "}"
                }.joinToString(separator = ",", prefix = "updateHouseholds([", postfix = "]);")
                navigator.evaluateJavaScript(js)
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

    LaunchedEffect(mapReady, state.heatmap) {
        if (mapReady) {
            state.heatmap.takeIf { !it.isNullOrEmpty() }?.let {
                val js = it.map { heatmapItem ->
                    "{'type': 'Feature', 'geometry': {'type': 'Point','coordinates': [${heatmapItem.longitude}, ${heatmapItem.latitude}]},'properties': {'frequency': ${heatmapItem.frequency} }}"
                }.joinToString(
                    separator = ",",
                    prefix = "addLocationHeatMap('heatmap', [",
                    postfix = "])"
                )

                navigator.evaluateJavaScript(js)
            } ?: run {
                navigator.evaluateJavaScript("clearLocationHeatMap('heatmap')")
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
    val drawData: List<List<Float>>? = null,
    val householdid: Int? = null,
    val type: String? = null,
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
                .popup {                
                    max-width: 320px !important;
                }
                .item_icon {
                    margin:3px;
                }
                .item_counter {                
                    font-size:10px;
                    line-height:14px;
                    text-align: center;
                    font-weight:bold;
                    display:inline-block;
                    position: fixed;
                    width:14px;
                    height:14px;
                    background:white;
                    border:2px solid red;
                    border-radius:50%;
                    margin-left: -16px;
                    margin-top: 20px;
                }
                .maplibregl-popup-content {                
                    border: 2px solid #5BA9AE;
                    border-radius:10px;                
                }
            </style>
        </head>
        
        <body>
            <div id="map"></div>
            <script>
                const defaultHouseholdImage = 'data:image/png;base64,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';
                const householdMarkerSvg = '<path d="M12 0C7.02944 0 3 4.02944 3 9C3 15.25 12 24 12 24C12 24 21 15.25 21 9C21 4.02944 16.9706 0 12 0ZM12 12.5C10.067 12.5 8.5 10.933 8.5 9C8.5 7.067 10.067 5.5 12 5.5C13.933 5.5 15.5 7.067 15.5 9C15.5 10.933 13.933 12.5 12 12.5Z" fill="#5BA9AE"/>';
    
                var mapLoaded = false;
                var lat = 0;
                var lng = 0;
                var households = [];
                var neighbourhoods = [];            
                var markers = []; 
                var draw;
                var updateArea;
    
                MapboxDraw.constants.classes.CONTROL_BASE = 'maplibregl-ctrl';
                MapboxDraw.constants.classes.CONTROL_PREFIX = 'maplibregl-ctrl-';
                MapboxDraw.constants.classes.CONTROL_GROUP = 'maplibregl-ctrl-group';
        
                const map = new maplibregl.Map({
                    container: 'map',
                    style: 'https://api.maptiler.com/maps/streets/style.json?key=VdbWJipihjTW3mb6JxNK',
                    center: [0, 0],
                    zoom: 1
                });
        
                function callNative(obj) {
                    //alert(JSON.stringify(obj));
                    window.kmpJsBridge.callNative("MapFeedback", JSON.stringify(obj), function (data) { });
                }
    
                map.on('load', () => {                
                    mapLoaded = true;
                    
                    callNative({mapReady:true});
                            
                    // updateHouseholds([
                    //     {'longitude':23.6685, 'latitude':47.653, 'id':1, 'donations':3, 'skillshare':5,'name':'Fam. Krucz', 'floatName':true, 'address':'Catinei 21','description':'This is our little house on the hill. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a massa at est malesuada faucibus. Donec ultricies, velit a aliquet pulvinar, sapien nibh mollis ante, sed egestas est arcu sed mi. Nullam facilisis efficitur libero vitae vulputate. Mauris nec mauris malesuada, mattis risus eu, pharetra urna. Etiam ac tincidunt tellus, ac commodo urna. Phasellus blandit elit at blandit auctor. Nulla facilisi. Suspendisse potenti. Aliquam diam arcu, aliquet non elementum ut, sollicitudin quis massa. Aliquam erat volutpat. Donec tortor est, blandit nec pellentesque sit amet, placerat quis nisi. Nulla nec facilisis diam. Phasellus convallis hendrerit est a auctor. Praesent vitae mauris convallis, tristique lacus at, porttitor orci. Integer consectetur nec dui vel dictum. Vestibulum sit amet purus sit amet lacus blandit semper vitae ac est. ', 'imageurl':''},
                    //     {'longitude':23.6695, 'latitude':47.653, 'id':2, 'name':'Some other household', 'floatName':false, 'address':'Catinei 13', 'description':"caca"}
                    // ]);
                    // addOrUpdateMarker("garbage_1", "#5BA9AE", 23.6685, 47.653, "There is A LOOOOT of garbage here");                
                    // addOrUpdateMarker("garbage_2", "#5BA9AE", 23.6695, 47.653, "There is A LOOOOT of garbage here");                
                    // center(23.6684, 47.6531, 15);
    
                    // updateHouseholds([
                    //                     {'longitude':23.6684, 'latitude':47.6531, 'id':1, 'name':"Krucz House", 'imageurl':'http://localhost/householdsIMGS/boiling-frog.png'},
                    //                     {'longitude':23.6784, 'latitude':47.6731, 'id':2, 'name':"Krucz House with a really long name", 'imageurl':'http://localhost/householdsIMGS/boiling-frog.png'}
                    //                 ]);
    
                    // enableDraw();
                    // disableDraw();
    
                    //addNeighbourhood("1", [[23.664421990469435,47.65655129393659],[23.665995201870714,47.64961446944116],[23.670235774323487,47.64969293384053],[23.668970442219916,47.653967631296325],[23.67055763940516,47.656120831152805],[23.6676487837274,47.65622253206254],[23.664421990469435,47.65655129393659]]);
                    
                    // addLocationHeatMap("heatmap_1", [
                    //     {'type': 'Feature', 'geometry': {'type': 'Point','coordinates': [23.6684, 47.6531]},'properties': {'frequency': 1 }},
                    //     {'type': 'Feature', 'geometry': {'type': 'Point','coordinates': [23.6685, 47.6531]},'properties': {'frequency': 4 }},
                    //     {'type': 'Feature', 'geometry': {'type': 'Point','coordinates': [23.6694, 47.6531]},'properties': {'frequency': 3 }},
                    //     {'type': 'Feature', 'geometry': {'type': 'Point','coordinates': [23.6684, 47.6541]},'properties': {'frequency': 10 }}                    
                    //     // Add more features as needed
                    // ]);
    
                    //setDot(23.6684, 47.6531, 'current');                                
                    //clearHouseholds();
                                                                   
                    
                    if (lat != 0 && lng != 0) {
                        setDot(lng, lat, 'current');
                        center(lng, lat, 21);
                    }
                });
                   
                function addOrUpdateMarker(id, markerColor, longitude, latitude, message) {             
                    if(markers[id] == undefined) {
                        markers[id] = new maplibregl.Marker({color: markerColor})
                                .setPopup(new maplibregl.Popup({offset: 25}).setText(message))
                                .setLngLat([longitude, latitude])
                                .addTo(map);
                    } else {
                        markers[id]                                    
                            .setPopup(new maplibregl.Popup({offset: 25}).setText(message))
                            .setLngLat([longitude, latitude])
                            .addTo(map);
                    }                            
                }
    
                function enableDraw() {
                    draw = new MapboxDraw({
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
                            callNative({ drawData: data });
                        }
                    }
        
                    map.on('draw.create', updateArea);
                    map.on('draw.delete', updateArea);
                    map.on('draw.update', updateArea);
                }
    
                function disableDraw() {
                    // Check if the draw control exists
                    if (draw) {
                        // Remove the draw control from the map
                        map.removeControl(draw);
    
                        // Remove the event listeners for draw.create, draw.delete, and draw.update
                        map.off('draw.create', updateArea);
                        map.off('draw.delete', updateArea);
                        map.off('draw.update', updateArea);
    
                        // Clear the drawn features
                        draw.deleteAll();
                    }
                }
    
                function clearNeighbourhoods() {
                    for (var key in neighbourhoods) {
                        map.removeLayer(neighbourhoods[key]);
                        map.removeSource(neighbourhoods[key]);
                    }               
                    neighbourhoods = [];                
                }
    
                function clearLocationHeatMap(id) {
                    map.removeLayer(id);
                    map.removeSource(id);
                }
    
                function addLocationHeatMap(id, data) {
                    if (mapLoaded) {
                        if (map.getSource(id) !== undefined) {
                            map.getSource(id).setData({
                                'type': 'FeatureCollection',
                                'features': data
                            });
                        } else {
                            map.addSource(id, {
                                'type': 'geojson',
                                'data': {
                                    'type': 'FeatureCollection',
                                    'features': data
                                }
                            });
                            map.addLayer({
                                'id': id,
                                'type': 'heatmap',
                                'source': id,
                                'maxzoom': 20,
                                'paint': {
                                    'heatmap-weight': {
                                        'property': 'frequency', // Replace with the property that reflects frequency
                                        'type': 'exponential',
                                        'stops': [
                                            [0, 0],
                                            [1, 1]
                                        ]
                                    },
                                    'heatmap-intensity': {
                                        'stops': [
                                            [0, 0],
                                            [1, 1]
                                        ]
                                    },
                                    'heatmap-color': [
                                        'interpolate',
                                        ['linear'],
                                        ['heatmap-density'],
                                        0, 'rgba(33,102,172,0)',
                                        0.2, 'rgb(103,169,207)',
                                        0.4, 'rgb(209,229,240)',
                                        0.6, 'rgb(253,219,199)',
                                        0.8, 'rgb(239,138,98)',
                                        1, 'rgb(178,24,43)'
                                    ],
                                    'heatmap-radius': {
                                        'stops': [
                                            [0, 2],
                                            [10, 10],
                                            [20, 20]
                                        ]
                                    },
                                    'heatmap-opacity': {
                                        'default': 1,
                                        'stops': [
                                            [14, 1],
                                            [15, 1],
                                            [20, 0.4]
                                        ]
                                    }
                                }
                            });
                        }
                    }
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
    
                function updateHouseholds(householdsData) {
                    
                    var updatedIds = [];
                    for (var key in householdsData) {
                        var house = householdsData[key];                        
                        addOrUpdateHousehold(house);
                        updatedIds.push(house.id);
                    }
                    
                    for (var key in households) {
                        if (!updatedIds.includes(parseInt(key))) {                        
                            households[key].remove();
                            delete households[key];
                        }
                    }
                }                
    
                function clearHouseholds() {
                    for (var key in households) {
                        households[key].remove();
                    }
                    households = [];
                }
    
                function addOrUpdateHousehold(house) {
                    if (mapLoaded) {    
                        var el = document.getElementById("household_" + house.id);
                        if(el == null) {
                            el = document.createElement('div');
                            el.id = "household_" + house.id;
                            el.className = 'marker';
    
                            households[house.id] = new maplibregl.Marker({ element: el })
                            .setPopup(new maplibregl.Popup({offset: {'bottom': [0, -70]}, className: 'popup'}))
                            .setLngLat([house.longitude, house.latitude])                                        
                            households[house.id].addTo(map);
                        } else {
                            households[house.id].setLngLat([house.longitude, house.latitude]);
                        }
    
                        var image 
                        if(house.imageurl != null && house.imageurl.length > 0) {
                            image = house.imageurl;
                        } else {
                            image = defaultHouseholdImage;
                        }
    
                        var html =      `<div style="opacity:0.65;margin-top:-50%">                                                 
                                            <div style="display: flex; flex-direction: column; align-items: center;">                                                   
                                        `;
                        if(house.name != null && house.name.length > 0 && house.floatName == true) {                
                            html +=     `
                                                <span style="position:absolute;margin-top:-96px;min-width:100px;background:white;padding:3px;border: 2px solid #5BA9AE;border-radius:10px;text-align:center;font-size:10px;line-height:10px;">` +  house.name + `</span>
                                        `;
                        }
    
                        html +=         `       
                                                <div style="position:absolute;margin-top:-63px;background:url(`+ image + `);background-size:45px;width:45px;height:45px;border:2px solid white;border-radius:50%;"></div>
                                                <svg style="position:absolute;margin-top:-70px;z-index:-1" width="70px" height="80px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                    ` + householdMarkerSvg + `
                                                </svg> 
                                            </div>
                                        </div>`;             
                        
                        el.innerHTML = html;
    
    
                        var popupHtml = `
                                        <div style="display: flex; flex-direction: column; align-items: left;">
                                            <span style="padding:3px;text-align:left;font-size:10px;line-height:10px;">` +  house.name + `</span>
                                            <span style="padding:3px;text-align:left;font-size:10px;line-height:10px;">` +  house.address + `</span>
                                            <span style="padding:3px;text-align:left;font-size:10px;line-height:10px;">` +  house.description + `</span>
                                            <div style="display: flex; flex-direction: row; align-items: center;justify-content: space-around;">                                            
                                        `;
    
                        if(house.donations > 0) {
                            popupHtml +=    `                                            
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'DONATION'});">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M20,6h-2.18c0.11,-0.31 0.18,-0.65 0.18,-1 0,-1.66 -1.34,-3 -3,-3 -1.05,0 -1.96,0.54 -2.5,1.35l-0.5,0.67 -0.5,-0.68C10.96,2.54 10.05,2 9,2 7.34,2 6,3.34 6,5c0,0.35 0.07,0.69 0.18,1L4,6c-1.11,0 -1.99,0.89 -1.99,2L2,19c0,1.11 0.89,2 2,2h16c1.11,0 2,-0.89 2,-2L22,8c0,-1.11 -0.89,-2 -2,-2zM15,4c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM9,4c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM20,19L4,19v-2h16v2zM20,14L4,14L4,8h5.08L7,10.83 8.62,12 11,8.76l1,-1.36 1,1.36L15.38,12 17,10.83 14.92,8L20,8v6z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.donations+`</span>
                                                </div>`;
                        }
                        if(house.barrterings > 0) {
                            popupHtml +=    `  
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'BARTER' });">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">                                            
                                                        <path fill="#5BA9AE" d="M16.48,10.41c-0.39,0.39 -1.04,0.39 -1.43,0l-4.47,-4.46l-7.05,7.04l-0.66,-0.63c-1.17,-1.17 -1.17,-3.07 0,-4.24l4.24,-4.24c1.17,-1.17 3.07,-1.17 4.24,0L16.48,9C16.87,9.39 16.87,10.02 16.48,10.41zM17.18,8.29c0.78,0.78 0.78,2.05 0,2.83c-1.27,1.27 -2.61,0.22 -2.83,0l-3.76,-3.76l-5.57,5.57c-0.39,0.39 -0.39,1.02 0,1.41c0.39,0.39 1.02,0.39 1.42,0l4.62,-4.62l0.71,0.71l-4.62,4.62c-0.39,0.39 -0.39,1.02 0,1.41c0.39,0.39 1.02,0.39 1.42,0l4.62,-4.62l0.71,0.71l-4.62,4.62c-0.39,0.39 -0.39,1.02 0,1.41c0.39,0.39 1.02,0.39 1.41,0l4.62,-4.62l0.71,0.71l-4.62,4.62c-0.39,0.39 -0.39,1.02 0,1.41c0.39,0.39 1.02,0.39 1.41,0l8.32,-8.34c1.17,-1.17 1.17,-3.07 0,-4.24l-4.24,-4.24c-1.15,-1.15 -3.01,-1.17 -4.18,-0.06L17.18,8.29z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.barrterings+`</span>
                                                </div>`;
                        }
                        if(house.sales > 0) {
                            popupHtml +=    `  
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'SALE' });">  
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M21.41,11.41l-8.83,-8.83C12.21,2.21 11.7,2 11.17,2H4C2.9,2 2,2.9 2,4v7.17c0,0.53 0.21,1.04 0.59,1.41l8.83,8.83c0.78,0.78 2.05,0.78 2.83,0l7.17,-7.17C22.2,13.46 22.2,12.2 21.41,11.41zM6.5,8C5.67,8 5,7.33 5,6.5S5.67,5 6.5,5S8,5.67 8,6.5S7.33,8 6.5,8z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.sales+`</span>
                                                </div>`;
                        }
                        if(house.events > 0) {
                            popupHtml +=    `  
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'EVENT' });">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M19,5h-2V3H7v2H5C3.9,5 3,5.9 3,7v1c0,2.55 1.92,4.63 4.39,4.94c0.63,1.5 1.98,2.63 3.61,2.96V19H7v2h10v-2h-4v-3.1c1.63,-0.33 2.98,-1.46 3.61,-2.96C19.08,12.63 21,10.55 21,8V7C21,5.9 20.1,5 19,5zM5,8V7h2v3.82C5.84,10.4 5,9.3 5,8zM19,8c0,1.3 -0.84,2.4 -2,2.82V7h2V8z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.events+`</span>
                                                </div>`;
                        }
                        if(house.needs > 0) {
                            popupHtml +=    ` 
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'NEED' });">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M17.73,12.02l3.98,-3.98c0.39,-0.39 0.39,-1.02 0,-1.41l-4.34,-4.34c-0.39,-0.39 -1.02,-0.39 -1.41,0l-3.98,3.98L8,2.29C7.8,2.1 7.55,2 7.29,2c-0.25,0 -0.51,0.1 -0.7,0.29L2.25,6.63c-0.39,0.39 -0.39,1.02 0,1.41l3.98,3.98L2.25,16c-0.39,0.39 -0.39,1.02 0,1.41l4.34,4.34c0.39,0.39 1.02,0.39 1.41,0l3.98,-3.98 3.98,3.98c0.2,0.2 0.45,0.29 0.71,0.29 0.26,0 0.51,-0.1 0.71,-0.29l4.34,-4.34c0.39,-0.39 0.39,-1.02 0,-1.41l-3.99,-3.98zM12,9c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM7.29,10.96L3.66,7.34l3.63,-3.63 3.62,3.62 -3.62,3.63zM10,13c-0.55,0 -1,-0.45 -1,-1s0.45,-1 1,-1 1,0.45 1,1 -0.45,1 -1,1zM12,15c-0.55,0 -1,-0.45 -1,-1s0.45,-1 1,-1 1,0.45 1,1 -0.45,1 -1,1zM14,11c0.55,0 1,0.45 1,1s-0.45,1 -1,1 -1,-0.45 -1,-1 0.45,-1 1,-1zM16.66,20.34l-3.63,-3.62 3.63,-3.63 3.62,3.62 -3.62,3.63z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.needs+`</span>
                                                </div>`;
                        }
                        if(house.requests > 0) {
                            popupHtml +=    ` 
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'REQUEST' });">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M23,17c0,3.31 -2.69,6 -6,6v-1.5c2.48,0 4.5,-2.02 4.5,-4.5H23zM1,7c0,-3.31 2.69,-6 6,-6v1.5C4.52,2.5 2.5,4.52 2.5,7H1zM8.01,4.32l-4.6,4.6c-3.22,3.22 -3.22,8.45 0,11.67s8.45,3.22 11.67,0l7.07,-7.07c0.49,-0.49 0.49,-1.28 0,-1.77c-0.49,-0.49 -1.28,-0.49 -1.77,0l-4.42,4.42l-0.71,-0.71l6.54,-6.54c0.49,-0.49 0.49,-1.28 0,-1.77s-1.28,-0.49 -1.77,0l-5.83,5.83l-0.71,-0.71l6.89,-6.89c0.49,-0.49 0.49,-1.28 0,-1.77s-1.28,-0.49 -1.77,0l-6.89,6.89L11.02,9.8l5.48,-5.48c0.49,-0.49 0.49,-1.28 0,-1.77s-1.28,-0.49 -1.77,0l-7.62,7.62c1.22,1.57 1.11,3.84 -0.33,5.28l-0.71,-0.71c1.17,-1.17 1.17,-3.07 0,-4.24l-0.35,-0.35l4.07,-4.07c0.49,-0.49 0.49,-1.28 0,-1.77C9.29,3.83 8.5,3.83 8.01,4.32z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.requests+`</span>
                                                </div>`;
                        }
                        if(house.skillshare > 0) {
                            popupHtml +=    ` 
                                                <div class="item_icon" onclick="callNative({ householdid: `+house.id+`, type: 'SKILLSHARE' });">
                                                    <svg width="36px" height="36px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path fill="#5BA9AE" d="M17.34,10.19l1.41,-1.41l2.12,2.12c1.17,-1.17 1.17,-3.07 0,-4.24l-3.54,-3.54l-1.41,1.41V1.71L15.22,1l-3.54,3.54l0.71,0.71h2.83l-1.41,1.41l1.06,1.06l-2.89,2.89L7.85,6.48V5.06L4.83,2.04L2,4.87l3.03,3.03h1.41l4.13,4.13l-0.85,0.85H7.6l-5.3,5.3c-0.39,0.39 -0.39,1.02 0,1.41l2.12,2.12c0.39,0.39 1.02,0.39 1.41,0l5.3,-5.3v-2.12l5.15,-5.15L17.34,10.19z" />
                                                    </svg>
                                                    <span class="item_counter">`+house.skillshare+`</span>
                                                </div>`;                    }
    
                        popupHtml +=        `                                            
                                            </div>
                                        </div>
                                        `;
    
                        households[house.id].getPopup().setHTML(popupHtml);                                        
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
