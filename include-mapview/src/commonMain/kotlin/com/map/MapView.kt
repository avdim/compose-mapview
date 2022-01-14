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

    val tilesStateFlow = store.stateFlow.mapStateFlow(
        init = ImageTilesGrid(0, 0, emptyList())
    ) {
        calcTiles(it, width, height).downloadImages()
    }
    PlatformMapView(width, height, tilesStateFlow, { store.send(MapIntent.Zoom(it)) }, { dx, dy -> store.send(MapIntent.Move(dx, dy))})
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
