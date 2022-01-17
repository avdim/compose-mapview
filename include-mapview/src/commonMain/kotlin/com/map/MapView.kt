package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow

@Composable
public fun MapView(modifier: DisplayModifier) {
    val viewScope = rememberCoroutineScope()
    val ioScope = CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO())
    val mapStore: Store<MapState, MapIntent> = viewScope.createMapStore()
    val imageRepository = createImageRepositoryComposable(ioScope)

    val originalTiles:MutableMap<Tile, GpuOptimizedImage> = createConcurrentMap()

    val gridStore = viewScope.createGridStore { store, sideEffect: SideEffect ->
        when (sideEffect) {
            is SideEffect.LoadTile -> {
                ioScope.launch {
                    try {
                        launch {
                            if (!originalTiles.containsKey(sideEffect.tile)) {
                                delay(10)
                                if (!originalTiles.containsKey(sideEffect.tile)) {
                                    val image = originalTiles.searchCropAndPut(sideEffect.tile)
                                    if (image != null) {
                                        store.send(GridIntent.TileLoaded(ImageTile(image, sideEffect.displayTile)))
                                    }
                                }
                            }
                        }
                        val image = imageRepository.getTileContent(sideEffect.tile)
                        originalTiles[sideEffect.tile] = image
                        store.send(GridIntent.TileLoaded(ImageTile(image, sideEffect.displayTile)))
                    } catch (t: Throwable) {
                        println("fail to load tile ${sideEffect.displayTile}, $t")
                    }
                }
            }
        }
    }
    viewScope.launch {
        mapStore.stateFlow.collect { state ->
            val grid = state.calcTiles()
            gridStore.send(GridIntent.NewTiles(grid))
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

fun MutableMap<Tile, GpuOptimizedImage>.searchCropAndPut(tile:Tile):GpuOptimizedImage? {
    return null
}

//expect fun GpuOptimizedImage.crop():GpuOptimizedImage
