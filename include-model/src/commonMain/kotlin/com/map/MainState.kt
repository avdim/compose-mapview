package com.map

/**
 * sample in js http://jsfiddle.net/84P9r/
 */
data class MapState(
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
    val topLeft: GeoPt = GeoPt(0.0, 0.0),
    val mousePoint: GeoPt = GeoPt(0.4, 0.4),
    val width: Int,
    val height: Int,
)

sealed interface MapIntent {
    data class Zoom(val delta: Double) : MapIntent
    data class Move(val pt: Pt) : MapIntent
}

fun createMapStore(width: Int, height: Int) = createStore(MapState(width = width, height = height)) { state: MapState, intent: MapIntent ->
    when(intent) {
        is MapIntent.Zoom -> {
            state.copy(scale = state.scale + intent.delta)
        }
        is MapIntent.Move -> {
            state.copy(
                topLeft = state.topLeft + state.displayToGeo(intent.pt),
                mousePoint = state.mousePoint + state.displayToGeo(intent.pt)
            )
        }
    }
}
