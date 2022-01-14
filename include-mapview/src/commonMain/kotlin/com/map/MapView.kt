package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width:Int = 600, height:Int = 700) {
    val store: Store<MapState, MapIntent> = createMapStore()

    store.stateFlow

    val mapState = MapState()
    val tileGrid = calcTiles(mapState, width, height)
    val stateFlow = MutableStateFlow(ImageTilesGrid(0, 0, emptyList()))
    GlobalScope.launch {
        stateFlow.emit(tileGrid.downloadImages())
    }
    PlatformMapView(width, height, stateFlow, { store.send(MapIntent.Zoom(it / 100)) }, { dx, dy -> })
    Telemetry(store.stateFlow)
}

@Composable
internal expect fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
)

@Composable
internal expect fun Telemetry(stateFlow: StateFlow<MapState>)
