package com.map

/**
 * sample in js http://jsfiddle.net/84P9r/
 */
data class MapState(
    /**
     * 0.0 - (min zoom) .. 1.0 (max zoom)
     */
    val zoom: Double = 0.1,
    /**
     * Latitude -90(South) .. 90(North)
     */
    val lat: Double = 0.0,
    /**
     * Longitude -180(Left) .. 180(Right)
     */
    val lon: Double = 0.0
)

sealed interface MapIntent {
    data class Zoom(val delta: Double) : MapIntent
    data class Move(val dx: Int, val dy: Int) : MapIntent
}

fun createMapStore() = createStore(MapState()) { state: MapState, intent: MapIntent ->
    when(intent) {
        is MapIntent.Zoom -> {
            state.copy(zoom = state.zoom + intent.delta)
        }
        is MapIntent.Move -> {
            state.copy()//todo move
        }
    }
}
