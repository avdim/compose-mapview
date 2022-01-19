package com.map

import kotlinx.coroutines.CoroutineScope

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
