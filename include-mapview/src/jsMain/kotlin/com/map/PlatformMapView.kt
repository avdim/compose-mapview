package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun PlatformMapView() {
    val store = createStore(0) { s: Int, intent: Int ->
        s + intent
    }
    val stateFlow: StateFlow<Int> = store.stateFlow
    LibJSCounter(stateFlow) {
        store.send(it)
    }
}
