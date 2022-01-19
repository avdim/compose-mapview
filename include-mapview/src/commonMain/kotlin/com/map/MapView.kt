package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*

/**
 * MapView to display Earth tile maps. API provided by cloud.maptiler.com
 *
 * @param modifier to specify size strategy for this composable
 *
 * @param mapTilerSecretKey secret API key for cloud.maptiler.com
 * Here you can get this key: https://cloud.maptiler.com/maps/streets/  (register and look at url field ?key=...#)
 * For build sample projects, in file: local.properties, set key: `mapTilerSecretKey=xXxXxXxXxXxXx`
 *
 * @param latitude initial Latitude of map center.
 * Available values between [-90.0 (South) .. 90.0 (North)]
 *
 * @param longitude initial Longitude of map center
 * Available values between [-180.0 (Left) .. 180.0 (Right)]
 *
 * @param startScale initial scale
 * (value around 1.0   = entire Earth view),
 * (value around 30.0  = Countries),
 * (value around 150.0 = Cities),
 * (value around 40000.0 = Street's)
 *
 * @param onMapViewClick handle click event with point coordinates (latitude, longitude)
 * return true to enable zoom on click
 * return false to disable zoom on click
 */
@Composable
public fun MapView(
    modifier: DisplayModifier,
    mapTilerSecretKey:String,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    startScale:Double=1.0,
    onMapViewClick: (latitude: Double, longitude: Double) -> Boolean = { lat, lon -> true },
) {
    val viewScope = rememberCoroutineScope()
    val ioScope = CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO())
    val mapStore: Store<MapState, MapIntent> = viewScope.createMapStore(latitude, longitude, startScale)
    val imageRepository = createImageRepositoryComposable(ioScope, mapTilerSecretKey)


    val tilesHashMap: MutableMap<Tile, GpuOptimizedImage> = createConcurrentMap()//todo mutable state

    val gridStore = viewScope.createGridStore<GpuOptimizedImage>(
        isBadQuality = { it.isBadQuality },
        searchCropAndPut = { tilesHashMap.searchCropAndPut(it) }
    ) { store, sideEffect: SideEffectGrid ->
        when (sideEffect) {
            is SideEffectGrid.LoadTile -> {
                ioScope.launch {
                    try {
                        val image: GpuOptimizedImage =
                            (if (Config.SCALE_ONLY_WITH_CROP) tilesHashMap.searchCropAndPut(sideEffect.tile) else null)
                                ?: imageRepository.loadContent(sideEffect.tile)

                        tilesHashMap[sideEffect.tile] = image
                        store.send(IntentGrid.TileLoaded(DisplayTileWithImage(image, sideEffect.displayTile)))
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
            gridStore.send(IntentGrid.NewTiles(grid))
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
        onClick = {
            val state = mapStore.state
            if (onMapViewClick(state.displayToGeo(it).latitude, state.displayToGeo(it).longitude)) {
                mapStore.send(MapIntent.Zoom(it, Config.ZOOM_ON_CLICK))
            }
        },
        onMove = { dx, dy -> mapStore.send(MapIntent.Move(Pt(-dx, -dy))) },
        updateSize = { w, h -> mapStore.send(MapIntent.SetSize(w, h)) }
    )
    if (Config.DISPLAY_TELEMETRY) {
        Telemetry(mapStore.stateFlow)
    }
}

expect interface DisplayModifier

/**
 * Создать репозиторий для получения tile картинок.
 * В зависимости от платформы будет обёрнут в Декоратор для кэша на диск и (или) in-memory кэш.
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal expect fun createImageRepositoryComposable(ioScope: CoroutineScope, mapTilerSecretKey:String): ContentRepository<Tile, GpuOptimizedImage>

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
            val cropImg = img2.cropAndRestoreSize(i * size, j * size, size)
            put(tile1, cropImg)
            return cropImg
        }
    }
    return null
}
