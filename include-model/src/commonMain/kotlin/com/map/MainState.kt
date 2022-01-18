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

fun MapState.toShortString(): String = buildString {
    appendLine("width: $width")
    appendLine("height: $height")
    appendLine("scale: ${scale.toShortString()}")
    appendLine("zoom: $zoom")
    appendLine("topLeft: ${topLeft.toShortString()}")
}

sealed interface MapIntent {
    data class Zoom(val pt: Pt, val delta: Double) : MapIntent
    data class Move(val pt: Pt) : MapIntent
    data class SetSize(val width: Int, val height: Int) : MapIntent
}

fun CoroutineScope.createMapStore() =
    createStore(MapState()) { state: MapState, intent: MapIntent ->
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
