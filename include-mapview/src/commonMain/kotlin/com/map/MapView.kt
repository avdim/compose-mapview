package com.map

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

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
    mapTilerSecretKey: String,
    latitude: Double? = null,
    longitude: Double? = null,
    startScale: Double? = null,
    externalState: MutableState<MapState> = remember { mutableStateOf(MapState()) },
    onStateChange: (MapState) -> Unit = { externalState.value = it },
    onMapViewClick: (latitude: Double, longitude: Double) -> Boolean = { lat, lon -> true },
) {
    val viewScope = rememberCoroutineScope()
    val ioScope = remember { CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO()) }
    val imageRepository = rememberTilesRepository(ioScope, mapTilerSecretKey)

    var width: Int by remember { mutableStateOf(100) }
    var height: Int by remember { mutableStateOf(100) }
    var cache: Map<Tile, TileImage> by remember { mutableStateOf(mapOf()) }
    val displayTiles: List<DisplayTileWithImage<TileImage>> by derivedStateOf {
        val calcTiles: List<DisplayTileAndTile> = externalState.value.calcTiles(width, height)
        val tilesToDisplay: MutableList<DisplayTileWithImage<TileImage>> = mutableListOf()
        val tilesToLoad: MutableSet<Tile> = mutableSetOf()
        calcTiles.forEach {
            val cachedImage = cache[it.tile]
            if (cachedImage != null) {
                tilesToDisplay.add(DisplayTileWithImage(it.display, cachedImage, it.tile))
            } else {
                tilesToLoad.add(it.tile)
                val croppedImage = cache.searchOrCrop(it.tile)
                tilesToDisplay.add(DisplayTileWithImage(it.display, croppedImage, it.tile))
            }
        }
        ioScope.launch {
            tilesToLoad.forEach { tile ->
                try {
                    val image: TileImage = imageRepository.loadContent(tile)
                    cache = cache + (tile to image) //todo потенциально дорогая операция
                } catch (t: Throwable) {
                    // ignore errors. Tile image loaded with retries
                }
            }
            //todo load tile
        }
        tilesToDisplay
    }

    PlatformMapView(
        modifier = modifier,
        tiles = displayTiles,
        onZoom = { pt: Pt?, change ->
            val state = externalState.value
            val pt = pt ?: Pt(state.width / 2, state.height / 2)
            var multiply = (1 + change)
            if (multiply < 1 / Config.MAX_SCALE_ON_SINGLE_ZOOM_EVENT) {
                multiply = 1 / Config.MAX_SCALE_ON_SINGLE_ZOOM_EVENT
            } else if (multiply > Config.MAX_SCALE_ON_SINGLE_ZOOM_EVENT) {
                multiply = Config.MAX_SCALE_ON_SINGLE_ZOOM_EVENT
            }
            var scale = state.scale * multiply
            if (scale < state.minScale) {
                scale = state.minScale
            }
            if (scale > state.maxScale) {
                scale = state.maxScale
            }
            val scaledState = state.copy(scale = scale)
            val geoDelta = state.displayToGeo(pt) - scaledState.displayToGeo(pt)

            externalState.value = scaledState.copy(topLeft = scaledState.topLeft + geoDelta)
                .correctGeoXY()
        },
        onClick = {
            val state = externalState.value
            if (onMapViewClick(state.displayToGeo(it).latitude, state.displayToGeo(it).longitude)) {

//                mapStore.send(MapIntent.Input.Zoom(it, Config.ZOOM_ON_CLICK))//todo
                externalState.value
            }
        },
        onMove = { dx, dy ->
            val state = externalState.value
            val topLeft = state.topLeft + state.displayLengthToGeo(Pt(-dx, -dy))
            externalState.value = state.copy(topLeft = topLeft).correctGeoXY()
        },
        updateSize = { w, h ->
            width = w
            height = h
            externalState.value = externalState.value.copy(width = w, height = h)
        }
    )
    if (Config.DISPLAY_TELEMETRY) {
        Telemetry(externalState.value)
    }
}

expect interface DisplayModifier

