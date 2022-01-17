package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@Composable
public fun MapView(width: Int = 800, height: Int = 500) {
    val viewScope = rememberCoroutineScope()
    val ioScope = rememberCoroutineScope { getDispatcherIO() }
    val mapStore: Store<MapState, MapIntent> = viewScope.createMapStore(width, height)
    val imageRepository = createImageRepositoryComposable(ioScope)

    val gridStore = viewScope.createGridStore { store, sideEffect: SideEffect ->
        when (sideEffect) {
            is SideEffect.LoadTile -> {
                viewScope.launch {
                    val tileContent = imageRepository.getTileContent(sideEffect.tile.tile)
                    store.send(GridIntent.TileLoaded(ImageTile(tileContent, sideEffect.tile, sideEffect.order)))
                }
            }
        }
    }
    viewScope.launch {
        mapStore.stateFlow.collect { state ->
            val grid = state.calcTiles()
            grid.matrix.forEach { displayTile ->
                gridStore.send(GridIntent.LoadTile(displayTile))
            }
        }
    }

    PlatformMapView(
        width = width,
        height = height,
        stateFlow = gridStore.stateFlow,
        onZoom = { pt, change -> mapStore.send(MapIntent.Zoom(pt, change)) },
        onClick = { mapStore.send(MapIntent.Zoom(it, 0.8)) }
    ) { dx, dy ->
        mapStore.send(MapIntent.Move(Pt(-dx, -dy)))
    }
    Telemetry(mapStore.stateFlow)
}

/**
 * Создать репозиторий для получения tile картинок.
 * В зависимости от платформы будет обёрнут в Декоратор для кэша на диск и (или) in-memory кэш.
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal expect fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage>

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
