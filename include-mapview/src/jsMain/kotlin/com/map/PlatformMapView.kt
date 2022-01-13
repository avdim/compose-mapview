package com.map

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformMapView() {
    val store = createMapViewStore()
    LibJSCounter()
}
