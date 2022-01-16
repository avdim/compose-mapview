package com.map

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.map.ui.MapViewAndroidDesktop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onZoomAnimate: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
){
    MapViewAndroidDesktop(
        width = width,
        height = height,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onZoomAnimate = onZoomAnimate,
        onMove = onMove
    )
}

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Column {
        Text(state.toString())
    }
}
