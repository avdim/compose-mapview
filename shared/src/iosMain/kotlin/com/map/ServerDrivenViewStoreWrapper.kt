package com.map

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ServerDrivenViewStoreWrapper(val scope:CoroutineScope, val sideEffectHandler:(MapSideEffect)->Unit) {

    val store = scope.createMapStore<ImageIos>(
        latitude = 0.0,
        longitude = 0.0,
        startScale = 1.0,
        searchOrCropOrNull = { TODO("searchOrCrop(it)") },
    ) { store, sideEffect ->
        when (sideEffect) {
            is MapSideEffect.LoadTile -> {
//                store.send(MapIntent.TileImageLoaded(sideEffect.tile, image as ImageIos))
            }
        }
    }

    fun sendIntent(intent: MapIntent<ImageIos>) {
        store.send(intent)
    }

    fun getLastState(): MapState<ImageIos> {
        return store.stateFlow.value
    }

    fun addListener(listener: (MapState<ImageIos>) -> Unit) {
        scope.launch {
            store.stateFlow.collectLatest {
                listener(it)
            }
        }
    }
}
