package com.map

import androidx.compose.runtime.Composable
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView() {
    PlatformMapView()
}

@Composable
internal expect fun PlatformMapView()
