package com.map

import kotlinx.coroutines.CoroutineScope

/**
 * sample in js http://jsfiddle.net/84P9r/
 */
data class MapState(
    /**
     * display width in dp (pixels)
     */
    val width: Int,
    /**
     * display height in dp (pixels)
     */
    val height: Int,
    /**
     * 0.1 = little planet
     * 1.0 = no zoom;
     * 10.0 = countries
     * 100.0 = cities
     */
    val scale: Double = 1.0,
//    /**
//     * Latitude -90(South) .. 90(North)
//     */
//    val lat: Double = 0.0,
//    /**
//     * Longitude -180(Left) .. 180(Right)
//     */
//    val lon: Double = 0.0,
//
    val topLeft: GeoPt = GeoPt(0.0, 0.0)
)

fun MapState.toShortString(): String = buildString {
    appendLine("width: $width")
    appendLine("height: $height")
    appendLine("scale: ${scale.toShortString()}")
    appendLine("zoom: $zoom")
    appendLine("topLeft: ${topLeft.toShortString()}")
}

sealed interface MapIntent {
    data class Zoom(val pt:Pt, val delta: Double) : MapIntent
    data class Move(val pt: Pt) : MapIntent
}

fun CoroutineScope.createMapStore(width: Int, height: Int) =
    createStore(MapState(width = width, height = height)) { state: MapState, intent: MapIntent ->
        when (intent) {
            is MapIntent.Zoom -> {
                var multiply = (1 + intent.delta)
                if (multiply < 0.5) {
                    multiply = 0.5
                } else if (multiply > 2.0) {
                    multiply = 2.0
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
