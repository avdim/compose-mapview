package com.map

import kotlinx.coroutines.CoroutineScope

data class ImageTilesGrid<T:Any>(
    val matrix: Map<DisplayTile, T?>,
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
    class TileLoaded<T>(val tile: DisplayTileWithImage<T>) : IntentGrid<T>
}

fun <T:Any> CoroutineScope.createGridStore(
    isBadQuality: (T)->Boolean,
    searchCropAndPut: (Tile) -> T?,
    effectHandler: (store: Store<ImageTilesGrid<T>, IntentGrid<T>>, SideEffectGrid) -> Unit
) = createStoreWithSideEffect(
    ImageTilesGrid(emptyMap()),
    effectHandler = effectHandler
) { state, intent: IntentGrid<T> ->
    when (intent) {
        is IntentGrid.UpdateTiles -> {
            state.copy(
                matrix = intent.grid.matrix.map { it.display to searchCropAndPut(it.tile) }.toMap()
            ).addSideEffects(
                intent.grid.matrix.map {
                    SideEffectGrid.LoadTile(it.display, it.tile)
                }
            )
        }
        is IntentGrid.TileLoaded -> {
            if (state.matrix.containsKey(intent.tile.display)) {
                val previous = state.matrix[intent.tile.display]
                if (previous == null || isBadQuality(previous)) {
                    if (previous != null) {
                        if (state.matrix.size > 64) {
                            println("state.matrix.size: ${state.matrix.size}")
                        }
                    }
                    state.copy(
                        matrix = state.matrix.toMutableMap().apply {
                            put(intent.tile.display, intent.tile.image)
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

