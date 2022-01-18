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
                matrix = intent.grid.matrix.map { it.first to tilesHashMap.searchCropAndPut(it.second) }.toMap() //todo тормозит
            ).addSideEffects(
                intent.grid.matrix.map {
                    SideEffect.LoadTile(it.first, it.second)
                }
            )
        }
        is GridIntent.TileLoaded -> {
            if (state.matrix.containsKey(intent.tile.display)) {
                state.copy(
                    matrix = state.matrix.toMutableMap().apply {
                        put(intent.tile.display, intent.tile.image)
                    }
                )
            } else {
                state
            }.noSideEffects()
        }
    }
}
