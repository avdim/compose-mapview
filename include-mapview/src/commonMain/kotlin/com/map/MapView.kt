package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    startScale: Double = 1.0,
    onMapViewClick: (latitude: Double, longitude: Double) -> Boolean = { lat, lon -> true },
) {
    val viewScope = rememberCoroutineScope()
    val ioScope = remember { CoroutineScope(SupervisorJob(viewScope.coroutineContext.job) + getDispatcherIO()) }
    val imageRepository = remember { createTilesRepository(ioScope, mapTilerSecretKey) }
    val mapStore: Store<MapState<TileImage>, MapIntent<TileImage>> = remember {
        viewScope.createMapStore(
            latitude = latitude,
            longitude = longitude,
            startScale = startScale,
            searchOrCropOrNull = { searchOrCrop(it) },
        ) { store, sideEffect ->
            when (sideEffect) {
                is MapSideEffect.LoadTile -> {
                    ioScope.launch {
                        try {
                            val image: TileImage = imageRepository.loadContent(sideEffect.tile)
                            store.send(MapIntent.TileImageLoaded(sideEffect.tile, image))
                        } catch (t: Throwable) {
                            // ignore errors. Tile image loaded with retries
                        }
                    }
                }
            }
        }
    }

    SideEffect {
        mapStore.send(MapIntent.Input.Recomposition(latitude, longitude, startScale))
    }

    PlatformMapView(
        modifier = modifier,
        stateFlow = mapStore.stateFlow,
        onZoom = { pt, change ->
            mapStore.send(
                MapIntent.Input.Zoom(pt ?: Pt(mapStore.state.width / 2, mapStore.state.height / 2), change)
            )
        },
        onClick = {
            val state = mapStore.state
            if (onMapViewClick(state.displayToGeo(it).latitude, state.displayToGeo(it).longitude)) {
                mapStore.send(MapIntent.Input.Zoom(it, Config.ZOOM_ON_CLICK))
            }
        },
        onMove = { dx, dy -> mapStore.send(MapIntent.Input.Move(Pt(-dx, -dy))) },
        updateSize = { w, h -> mapStore.send(MapIntent.Input.SetSize(w, h)) }
    )
    if (Config.DISPLAY_TELEMETRY) {
        Telemetry(mapStore.stateFlow)
    }
}

expect interface DisplayModifier


