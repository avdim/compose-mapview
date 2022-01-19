package com.map

import kotlinx.coroutines.CoroutineScope

data class GridStoreState<T:Any>(
    val mapTileToImage: Map<DisplayTile, T?>,
)

data class DisplayTileWithImage<T>(
    val image: T,
    val display: DisplayTile
)

sealed interface SideEffectGrid {
    class LoadTile(val displayTile: DisplayTile, val tile: Tile) : SideEffectGrid
}

sealed interface IntentGrid<T> {
    class UpdateTiles<T>(val grid: TilesGrid) : IntentGrid<T>
    class TileImageLoaded<T>(val tileWithImage: DisplayTileWithImage<T>) : IntentGrid<T>
}

fun <T:Any> CoroutineScope.createGridStore(
    isBadQuality: (T)->Boolean,
    searchCropAndPut: (Tile) -> T?,
    effectHandler: (store: Store<GridStoreState<T>, IntentGrid<T>>, SideEffectGrid) -> Unit
) = createStoreWithSideEffect(
    GridStoreState(emptyMap()),
    effectHandler = effectHandler
) { state, intent: IntentGrid<T> ->
    when (intent) {
        is IntentGrid.UpdateTiles -> {
            state.copy(
                mapTileToImage = intent.grid.matrix.map { it.display to searchCropAndPut(it.tile) }.toMap()
            ).addSideEffects(
                intent.grid.matrix.map {
                    SideEffectGrid.LoadTile(it.display, it.tile)
                }
            )
        }
        is IntentGrid.TileImageLoaded -> {
            if (state.mapTileToImage.containsKey(intent.tileWithImage.display)) {
                val previous = state.mapTileToImage[intent.tileWithImage.display]
                if (previous == null || isBadQuality(previous)) {
                    if (previous != null) {
                        if (state.mapTileToImage.size > 64) {
                            println("state.matrix.size: ${state.mapTileToImage.size}")
                        }
                    }
                    state.copy(
                        mapTileToImage = state.mapTileToImage.toMutableMap().apply {
                            put(intent.tileWithImage.display, intent.tileWithImage.image)
                        }
                    )
                } else {
                    state
                }
            } else {
                state
            }.noSideEffects()
        }
    }
}

