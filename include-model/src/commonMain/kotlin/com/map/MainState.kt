package com.map

import kotlinx.coroutines.CoroutineScope

/**
 * sample in js http://jsfiddle.net/84P9r/
 */
data class MapState(
    /**
     * display width in dp (pixels)
     */
    val width: Int = 100,
    /**
     * display height in dp (pixels)
     */
    val height: Int = 100,
    val scale: Double = 1.0,
    val topLeft: GeoPt = GeoPt(0.0, 0.0)
)

val MapState.centerGeo get():GeoPt = displayToGeo(Pt(width / 2, height / 2))
val MapState.latitude get():Double = centerGeo.latitude
val MapState.longitude get():Double = centerGeo.longitude
fun MapState.copyAndChangeCenter(targetCenter: GeoPt): MapState =
    copy(
        topLeft = topLeft + targetCenter - centerGeo
    ).correctGeoXY()

/**
 * Корректируем координаты, чтобы они не выходили за край карты.
 */
fun MapState.correctGeoXY(): MapState =
    correctGeoX().correctGeoY()

fun MapState.correctGeoY(): MapState {
    val minGeoY = 0.0
    val maxGeoY: Double = 1 - 1 / scale
    return if (topLeft.y < minGeoY) {
        copy(topLeft = topLeft.copy(y = minGeoY))
    } else if (topLeft.y > maxGeoY) {
        copy(topLeft = topLeft.copy(y = maxGeoY))
    } else {
        this
    }
}

fun MapState.correctGeoX(): MapState = copy(topLeft = topLeft.copy(x = topLeft.x.mod(1.0)))

fun MapState.toShortString(): String = buildString {
    appendLine("width: $width")
    appendLine("height: $height")
    appendLine("scale: ${scale.toShortString()}")
    appendLine("zoom: $zoom")
    appendLine("topLeft: ${topLeft.toShortString()}")
    appendLine("center: ${centerGeo.toShortString()}")
    appendLine("lat: ${centerGeo.latitude}, lon: ${centerGeo.longitude}")
}

//todo move to another file
sealed interface MapIntent {
    data class Zoom(val pt: Pt, val delta: Double) : MapIntent
    data class Move(val pt: Pt) : MapIntent
    data class SetSize(val width: Int, val height: Int) : MapIntent
}

fun CoroutineScope.createMapStore(latitude: Double, longitude: Double, startScale: Double): Store<MapState, MapIntent> {
    return createStore(
        MapState(scale = startScale).copyAndChangeCenter(createGeoPt(latitude, longitude))
    ) { state: MapState, intent: MapIntent ->
        when (intent) {
            is MapIntent.SetSize -> {
                state.copy(width = intent.width, height = intent.height)
            }
            is MapIntent.Zoom -> {
                var multiply = (1 + intent.delta)
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
                val geoDelta = state.displayToGeo(intent.pt) - scaledState.displayToGeo(intent.pt)
                scaledState.copy(topLeft = scaledState.topLeft + geoDelta)
                    .correctGeoXY()
            }
            is MapIntent.Move -> {
                val topLeft = state.topLeft + state.displayLengthToGeo(intent.pt)
                state.copy(topLeft = topLeft)
                    .correctGeoXY()
            }
        }
    }
}
