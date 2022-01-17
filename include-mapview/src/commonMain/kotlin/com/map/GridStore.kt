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
                matrix = intent.grid.matrix.map { it.first }.associateWith { null }
            )
            state.addSideEffects(
                intent.grid.matrix.map {
                    SideEffect.LoadTile(it.first, it.second)
                }
            )
        }
        is GridIntent.TileLoaded -> {
            state.copy(
                matrix = state.matrix.toMutableMap().apply {
                    put(intent.tile.display, intent.tile.image)
                }
            ).noSideEffects()
        }
    }
}
