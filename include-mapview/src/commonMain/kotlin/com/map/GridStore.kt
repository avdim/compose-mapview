package com.map

import kotlinx.coroutines.CoroutineScope

sealed interface SideEffect {
    class LoadTile(val displayTile: DisplayTile, val tile: Tile) : SideEffect
}

sealed interface GridIntent {
    class NewTiles(val grid: TilesGrid) : GridIntent
    class TileLoaded(val tile: ImageTile) : GridIntent
}

fun CoroutineScope.createGridStore(
    effectHandler: (store: Store<ImageTilesGrid, GridIntent>, SideEffect) -> Unit
) = createStoreWithSideEffect(
    ImageTilesGrid(emptyMap()),
    effectHandler = effectHandler
) { state, intent: GridIntent ->
    when (intent) {
        is GridIntent.NewTiles -> {
            state.copy(
                matrix = intent.grid.matrix.map { it.first to tilesHashMap.searchCropAndPut(it.second) }.toMap()
            ).addSideEffects(
                intent.grid.matrix.map {
                    SideEffect.LoadTile(it.first, it.second)
                }
            )
        }
        is GridIntent.TileLoaded -> {
            if (state.matrix.containsKey(intent.tile.display)) {
                val previous = state.matrix[intent.tile.display]
                if (previous == null || previous.isBadQuality) {
                    if (previous != null) {
                        if(state.matrix.size > 64) {
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
