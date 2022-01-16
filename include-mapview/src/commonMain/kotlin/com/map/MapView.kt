package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width: Int = 800, height: Int = 500) {
    val store: Store<MapState, MapIntent> = createMapStore(width, height)
    val imageRepository = createImageRepository()

    val tilesStateFlow = store.stateFlow.mapStateFlow(
        init = ImageTilesGrid(0, 0, emptyList())
    ) {
        it.calcTiles().downloadImages(imageRepository)//todo не очевиден return тип
    }
    PlatformMapView(
        width = width,
        height = height,
        stateFlow = tilesStateFlow,
        onZoom = { store.send(MapIntent.Zoom(it)) },
        onClick = { println("click on $it") }
    ) { dx, dy ->
        store.send(MapIntent.Move(Pt(-dx, -dy)))
    }
    Telemetry(store.stateFlow)
}

@Composable
internal expect fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit
)

@Composable
internal expect fun Telemetry(stateFlow: StateFlow<MapState>)

private suspend fun TilesGrid.downloadImages(imageRepository: ImageRepository):ImageTilesGrid {
    return ImageTilesGrid(
        lengthX = lengthX,
        lengthY = lengthY,
        matrix = matrix.map {
            it.map { displayTile->
                getBackgroundScope().async {
                    ImageTile(
                        pic = imageRepository.getImage(displayTile.tile),
                        display = displayTile
                    )
                }
            }.awaitAll()
        }
    )
}
