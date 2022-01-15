package com.map

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
    appendLine("topLeft: ${topLeft.toShortString()}")
}

sealed interface MapIntent {
    data class Zoom(val delta: Double) : MapIntent
    data class Move(val pt: Pt) : MapIntent
}

fun createMapStore(width: Int, height: Int) =
    createStore(MapState(width = width, height = height)) { state: MapState, intent: MapIntent ->
        when (intent) {
            is MapIntent.Zoom -> {
                state.copy(scale = state.scale + intent.delta)
            }
            is MapIntent.Move -> {
                state.copy(
                    topLeft = state.topLeft + state.displayLengthToGeo(intent.pt),
                )
            }
        }
    }
