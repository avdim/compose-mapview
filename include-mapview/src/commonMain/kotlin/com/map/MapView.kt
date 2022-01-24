package com.map

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

data class MapState(
    val latitude: Double,
    val longitude: Double,
    val scale: Double,
)

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
    state: State<MapState> = remember {
        mutableStateOf(MapState(latitude ?: 0.0, longitude ?: 0.0, startScale ?: 1.0))
    },
    onStateChange: (MapState) -> Unit = { (state as? MutableState<MapState>)?.value = it },
    onMapViewClick: (latitude: Double, longitude: Double) -> Boolean = { lat, lon -> true },
) {
    val viewScope = rememberCoroutineScope()
    val ioScope = remember { CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO()) }
    val imageRepository = rememberTilesRepository(ioScope, mapTilerSecretKey)

    var width: Int by remember { mutableStateOf(100) }
    var height: Int by remember { mutableStateOf(100) }
    var cache: Map<Tile, TileImage> by remember { mutableStateOf(mapOf()) }
    val internalState: InternalMapState by derivedStateOf {
        val center = createGeoPt(state.value.latitude, state.value.longitude)
        InternalMapState(width, height, state.value.scale)
            .copyAndChangeCenter(center)
    }
    val displayTiles: List<DisplayTileWithImage<TileImage>> by derivedStateOf {
        val calcTiles: List<DisplayTileAndTile> = internalState.calcTiles()
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
            onStateChange(internalState.zoom(pt, change).toExternalState())
        },
        onClick = {
            if (onMapViewClick(internalState.displayToGeo(it).latitude, internalState.displayToGeo(it).longitude)) {
                onStateChange(internalState.zoom(it, Config.ZOOM_ON_CLICK).toExternalState())
            }
        },
        onMove = { dx, dy ->
            val topLeft = internalState.topLeft + internalState.displayLengthToGeo(Pt(-dx, -dy))
            onStateChange(internalState.copy(topLeft = topLeft).correctGeoXY().toExternalState())
        },
        updateSize = { w, h ->
            width = w
            height = h
            onStateChange(internalState.copy(width = w, height = h).toExternalState())
        }
    )
    if (Config.DISPLAY_TELEMETRY) {
        Telemetry(internalState)
    }
}

expect interface DisplayModifier

fun InternalMapState.toExternalState() =
    MapState(
        centerGeo.latitude,
        centerGeo.longitude,
        scale
    )
