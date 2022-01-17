package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.js.JsExport

@JsExport
@Composable
public fun MapView(width: Int = 800, height: Int = 500) {
    val viewScope = rememberCoroutineScope()
    val ioScope = rememberCoroutineScope { getDispatcherIO() }
    val mviStore: Store<MapState, MapIntent> = viewScope.createMapStore(width, height)
    val imageRepository = createImageRepositoryComposable(ioScope)
    val tilesStateFlow = mviStore.stateFlow.mapStateFlow(
        scope = viewScope,
        init = ImageTilesGrid(emptyList())
    ) {
        it.calcTiles().downloadImages(ioScope, imageRepository)//todo не очевиден return тип
    }
    PlatformMapView(
        width = width,
        height = height,
        stateFlow = tilesStateFlow,
        onZoom = { pt, change -> mviStore.send(MapIntent.Zoom(pt, change)) },
        onClick = { mviStore.send(MapIntent.Zoom(it, 2.0)) }
    ) { dx, dy ->
        mviStore.send(MapIntent.Move(Pt(-dx, -dy)))
    }
    Telemetry(mviStore.stateFlow)
}

/**
 * Создать репозиторий для получения tile картинок.
 * В зависимости от платформы будет обёрнут в Декоратор для кэша на диск и (или) in-memory кэш.
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal expect fun createImageRepositoryComposable(ioScope: CoroutineScope):TileContentRepository<GpuOptimizedImage>

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

private suspend fun TilesGrid.downloadImages(scope:CoroutineScope, imageRepository: TileContentRepository<GpuOptimizedImage>):ImageTilesGrid {
    val matrix1: List<List<ImageTile>> = matrix.map {
        it.map { displayTile ->
            scope.async {
                ImageTile(
                    image = imageRepository.getTileContent(displayTile.tile),
                    display = displayTile
                )
            }
        }.awaitAll()
    }
    return ImageTilesGrid(
        matrix = matrix1
    )
}
