package com.map

import androidx.compose.runtime.Composable
import com.map.ui.MapViewAndroidDesktop
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
){
    MapViewAndroidDesktop(width = width, height = height, stateFlow = stateFlow, onZoom = onZoom, onMove = onMove)
}
