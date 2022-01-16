package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.dom.Text

@Composable
internal actual fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onZoomAnimate: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    MapViewBrowser(
        width = width,
        height = height,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onZoomAnimate = onZoomAnimate,
        onMove = onMove
    )
    val store = createStore(0.0) { s, intent: Double ->
        s + intent
    }
    val stateFlow: StateFlow<Double> = store.stateFlow
    LibJSCounter(stateFlow) {
        store.send(it)
    }
}

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Text(state.toShortString())
}
