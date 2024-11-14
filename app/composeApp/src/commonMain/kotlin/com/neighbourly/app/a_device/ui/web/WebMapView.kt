package com.neighbourly.app.a_device.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.WebMapViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType
import kotlinx.serialization.json.Json


@Composable
fun WebMapView(
    modifier: Modifier = Modifier,
    viewModel: WebMapViewModel = androidx.lifecycle.viewmodel.compose.viewModel { KoinProvider.KOIN.get<WebMapViewModel>() },
    navigation: NavigationViewModel = androidx.lifecycle.viewmodel.compose.viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    val webViewState = rememberWebViewStateWithHTMLData(data = mapHtml)
    val navigator = rememberWebViewNavigator()
    val jsBridge = rememberWebViewJsBridge()
    var firstGps by remember { mutableStateOf(true) }
    var mapReady by remember { mutableStateOf(false) }
    val jsMessageHandler = remember {
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
                    navigation.goBack()
                }
            }
        }
    }

    val callback: GeoLocationCallback = remember {
        { lat, lng, acc ->
            navigator.evaluateJavaScript("setDot($lng, $lat, 'current');")
        }
    }

    WebView(
        state = webViewState,
        modifier = modifier,
        navigator = navigator,
        webViewJsBridge = jsBridge,
    )

    DisposableEffect(jsBridge) {
        jsBridge.register(jsMessageHandler)
        onDispose {
            jsBridge.unregister(jsMessageHandler)
            mapReady = false
        }
    }

    DisposableEffect(mapReady) {
        if (mapReady) {
            GetLocation.addCallback(callback)
            viewModel.refreshMapContent()
        }
        onDispose {
            GetLocation.removeCallback(callback)
            viewModel.clearMapContent()
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
            } ?: run {
                navigator.evaluateJavaScript("centerToDot();")
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
}


