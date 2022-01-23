package com.map

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MapStoreAdapter(val sideEffectHandler: (Store<MapState<TileImage>, MapIntent<TileImage>>, MapSideEffect) -> Unit) {
    val scope = MainScope()
    val store: Store<MapState<TileImage>, MapIntent<TileImage>> = scope.createMapStore(
        latitude = 0.0,
        longitude = 0.0,
        startScale = 1.0,
        searchOrCropOrNull = { searchOrCrop(it) },
    ) { store, sideEffect ->
        sideEffectHandler(store, sideEffect)
    }

    fun sendIntent(intent: MapIntent<TileImage>) {
        store.send(intent)
    }

    fun getLastState(): MapState<TileImage> {
        return store.stateFlow.value
    }

    fun addListener(listener: (MapState<TileImage>) -> Unit) {
        scope.launch {
            store.stateFlow.collectLatest {
                listener(it)
            }
        }
    }
}
