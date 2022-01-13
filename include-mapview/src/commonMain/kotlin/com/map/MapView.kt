package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width:Int = 600, height:Int = 700) {
    val mapState = MapState()
    val tileGrid = calcTiles(mapState, width, height)
    val stateFlow = MutableStateFlow(ImageTilesGrid(0, 0, emptyList()))
    GlobalScope.launch {
        stateFlow.emit(tileGrid.downloadImages())
    }
    PlatformMapView(width, height, stateFlow, {}, { dx, dy -> })
}

@Composable
internal expect fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
)
