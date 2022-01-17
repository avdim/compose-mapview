package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width: Int = 800, height: Int = 500) {
    val store: Store<MapState, MapIntent> = createMapStore(width, height)
    val imageRepository = createImageRepositoryComposable()
    val tilesStateFlow = store.stateFlow.mapStateFlow(
        init = ImageTilesGrid(emptyList())
    ) {
        it.calcTiles().downloadImages(imageRepository)//todo не очевиден return тип
    }
    PlatformMapView(
        width = width,
        height = height,
        stateFlow = tilesStateFlow,
        onZoom = { pt, change -> store.send(MapIntent.Zoom(pt, change)) },
        onClick = { store.send(MapIntent.Zoom(it, 2.0)) }
    ) { dx, dy ->
        store.send(MapIntent.Move(Pt(-dx, -dy)))
    }
    Telemetry(store.stateFlow)
}

/**
 * Создать репозиторий для получения tile картинок.
 * В зависимости от платформы будет обёрнут в Декоратор для кэша на диск и (или) in-memory кэш.
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal expect fun createImageRepositoryComposable():TileContentRepository<GpuOptimizedImage>

@Composable
internal expect fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit
)

@Composable
internal expect fun Telemetry(stateFlow: StateFlow<MapState>)

private suspend fun TilesGrid.downloadImages(imageRepository: TileContentRepository<GpuOptimizedImage>):ImageTilesGrid {
    val matrix1: List<List<ImageTile>> = matrix.map {
        it.map { displayTile ->
            getBackgroundScope().async {
                ImageTile(
                    pic = imageRepository.getTileContent(displayTile.tile),
                    display = displayTile
                )
            }
        }.awaitAll()
    }
    return ImageTilesGrid(
        matrix = matrix1
    )
}
