package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width: Int = 500, height: Int = 700) {
    val store: Store<MapState, MapIntent> = createMapStore(width, height)
    val imageRepository = createImageRepository()

    val tilesStateFlow = store.stateFlow.mapStateFlow(
        init = ImageTilesGrid(0, 0, emptyList())
    ) {
        it.calcTiles().downloadImages(imageRepository)//todo не очевиден return тип
    }
    PlatformMapView(width, height, tilesStateFlow, { store.send(MapIntent.Zoom(it)) }) { dx, dy ->
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
                getNetworkScope().async {
                    ImageTile(
                        pic = imageRepository.getImage(displayTile.tile),
                        display = displayTile
                    )
                }
            }.awaitAll()
        }
    )
}
