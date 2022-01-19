package com.map

import kotlinx.coroutines.CoroutineScope

sealed interface MapIntent<T> {
    class TileImageLoaded<T>(val tile: Tile, val image: T) : MapIntent<T>
    sealed interface Input<T> : MapIntent<T> {
        data class Zoom<T>(val pt: Pt, val delta: Double) : Input<T>
        data class Move<T>(val pt: Pt) : Input<T>
        data class SetSize<T>(val width: Int, val height: Int) : Input<T>
    }
}

sealed interface MapSideEffect {
    class LoadTile(val tile: Tile) : MapSideEffect
}

fun <T> CoroutineScope.createMapStore(
    latitude: Double,
    longitude: Double,
    startScale: Double,
    searchOrCropOrNull: Map<Tile, T>.(Tile) -> T?,
    effectHandler: (store: Store<MapState<T>, MapIntent<T>>, MapSideEffect) -> Unit,
): Store<MapState<T>, MapIntent<T>> {

    fun MapState<T>.updateDisplayTiles() = run {
        val tilesToDisplay: MutableList<DisplayTileWithImage<T>> = mutableListOf()
        val tilesToLoad: MutableSet<Tile> = mutableSetOf()
        calcTiles().tiles.forEach {
            val cachedImage = cache[it.tile]
            if (cachedImage != null) {
                tilesToDisplay.add(DisplayTileWithImage(it.display, cachedImage, it.tile))
            } else {
                tilesToLoad.add(it.tile)
                val croppedImage = cache.searchOrCropOrNull(it.tile)
                tilesToDisplay.add(DisplayTileWithImage(it.display, croppedImage, it.tile))
            }
        }
        copy(displayTiles = tilesToDisplay).addSideEffects(
            tilesToLoad.map {
                MapSideEffect.LoadTile(it)
            }
        )
    }

    return createStoreWithSideEffect(
        init = MapState<T>(scale = startScale).copyAndChangeCenter(createGeoPt(latitude, longitude)),
        effectHandler = effectHandler,
    ) { state: MapState<T>, intent: MapIntent<T> ->
        when (intent) {
            is MapIntent.Input -> {

                when (intent) {
                    is MapIntent.Input.SetSize -> {
                        state.copy(width = intent.width, height = intent.height)
                    }
                    is MapIntent.Input.Zoom -> {
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
                    is MapIntent.Input.Move -> {
                        val topLeft = state.topLeft + state.displayLengthToGeo(intent.pt)
                        state.copy(topLeft = topLeft)
                            .correctGeoXY()
                    }
                }.let {
                    if (state != it) {
                        // Если стейт уже изменился, то ещё петесчитаем displayTiles
                        it.updateDisplayTiles()
                    } else {
                        it.noSideEffects()
                    }
                }
            }
            is MapIntent.TileImageLoaded -> {
                state.cache[intent.tile] = intent.image
                val modifiedTiles = state.displayTiles.toMutableList()
                for (i in modifiedTiles.indices) {
                    if (modifiedTiles[i].tile == intent.tile) {
                        modifiedTiles[i] = modifiedTiles[i].copy(
                            image = intent.image
                        )
                    }
                }
                state.copy(
                    displayTiles = modifiedTiles
                ).noSideEffects()
            }
        }
    }
}
