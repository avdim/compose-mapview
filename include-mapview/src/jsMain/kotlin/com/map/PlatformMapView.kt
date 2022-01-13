package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    val store = createStore(0.0) { s, intent: Double ->
        s + intent
    }
    val stateFlow: StateFlow<Double> = store.stateFlow
    LibJSCounter(stateFlow) {
        store.send(it)
    }
}
