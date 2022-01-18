package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max

@Composable
public fun MapView(modifier: DisplayModifier) {
    val viewScope = rememberCoroutineScope()
    val ioScope = CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO())
    val mapStore: Store<MapState, MapIntent> = viewScope.createMapStore()
    val imageRepository = createImageRepositoryComposable(ioScope)

    val originalTiles: MutableMap<Tile, GpuOptimizedImage> = createConcurrentMap()

    val gridStore = viewScope.createGridStore { store, sideEffect: SideEffect ->
        when (sideEffect) {
            is SideEffect.LoadTile -> {
                ioScope.launch {
                    try {
                        val imgTryCrop = if (Config.TRY_SCALE_WITH_CROP) {
                            originalTiles.searchCropAndPut(sideEffect.tile)
                        } else {
                            null
                        }
                        if (imgTryCrop == null && !originalTiles.containsKey(sideEffect.tile)) {
                            ioScope.launch {
                                yield()
                                if (!originalTiles.containsKey(sideEffect.tile)) {
                                    val image = originalTiles.searchCropAndPut(sideEffect.tile)
                                    if (image != null) {
                                        store.send(GridIntent.TileLoaded(ImageTile(image, sideEffect.displayTile)))
                                    }
                                }
                            }
                        }
                        val image = imgTryCrop ?: imageRepository.getTileContent(sideEffect.tile)
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

fun MutableMap<Tile, GpuOptimizedImage>.searchCropAndPut(tile1: Tile): GpuOptimizedImage? {
    //todo unit tests
    val img1 = get(tile1)
    if (img1 != null) {
        return img1
    }
    var zoom = tile1.zoom
    var x = tile1.x
    var y = tile1.y
    while (zoom > 0) {
        zoom--
        x /= 2
        y /= 2
        val tile2 = Tile(zoom, x, y)
        val img2 = get(tile2)
        if (img2 != null) {
            val deltaZoom = tile1.zoom - tile2.zoom
            val i = tile1.x - (x shl deltaZoom)
            val j = tile1.y - (y shl deltaZoom)
            val size = max(TILE_SIZE ushr deltaZoom, 1)
            val cropImg = img2.crop(i * size, j * size, size, size).scale(TILE_SIZE, TILE_SIZE)
            put(tile1, cropImg)
            return cropImg
        }
    }
    return null
}

expect fun GpuOptimizedImage.crop(x: Int, y: Int, w: Int, h: Int): GpuOptimizedImage
expect fun GpuOptimizedImage.scale(w:Int, h:Int):GpuOptimizedImage
