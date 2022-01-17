package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@Composable
public fun MapView(modifier: DisplayModifier) {
    val viewScope = rememberCoroutineScope()
    val ioScope = CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO())
    val mapStore: Store<MapState, MapIntent> = viewScope.createMapStore()
    val imageRepository = createImageRepositoryComposable(ioScope)

    //todo val alpha: Float by animateFloatAsState(if (enabled) 1f else 0.5f)
    val gridStore = viewScope.createGridStore { store, sideEffect: SideEffect ->
        when (sideEffect) {
            is SideEffect.LoadTile -> {
                ioScope.launch {
                    try {
                        val tileContent = imageRepository.getTileContent(sideEffect.tile.tile)
                        store.send(GridIntent.TileLoaded(ImageTile(tileContent, sideEffect.tile, sideEffect.order)))
                    } catch (t: Throwable) {
                        println("fail to load tile ${sideEffect.tile}, $t")
                    }
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
        modifier = modifier,
        stateFlow = gridStore.stateFlow,
        onZoom = { pt, change ->
            mapStore.send(
                MapIntent.Zoom(pt ?: Pt(mapStore.state.width / 2, mapStore.state.height / 2), change)
            )
        },
        onClick = { mapStore.send(MapIntent.Zoom(it, 0.8)) },
        onMove = { dx, dy -> mapStore.send(MapIntent.Move(Pt(-dx, -dy))) },
        updateSize = { w, h -> mapStore.send(MapIntent.SetSize(w, h)) }
    )
    Telemetry(mapStore.stateFlow)
}

expect interface DisplayModifier

@Composable
internal expect fun PlatformMapView(
    modifier: DisplayModifier,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit,
    updateSize: (width: Int, height: Int) -> Unit
)

/**
 * Создать репозиторий для получения tile картинок.
 * В зависимости от платформы будет обёрнут в Декоратор для кэша на диск и (или) in-memory кэш.
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal expect fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage>

@Composable
internal expect fun Telemetry(stateFlow: StateFlow<MapState>)
