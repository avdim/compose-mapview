package com.map

import kotlinx.coroutines.CoroutineScope

sealed interface SideEffect {
    class LoadTile(val tile: DisplayTile):SideEffect
}

sealed interface GridIntent {
    class LoadTile(val tile: DisplayTile) : GridIntent
    class TileLoaded(val tile: ImageTile):GridIntent
}

fun CoroutineScope.createGridStore(
    effectHandler: (store: Store<ImageTilesGrid, GridIntent>, SideEffect) -> Unit
) = createStoreWithSideEffect(
    ImageTilesGrid(),
    effectHandler = effectHandler
) { state, intent: GridIntent ->
    when (intent) {
        is GridIntent.LoadTile -> {
            state.addSideEffect(SideEffect.LoadTile(intent.tile))
        }
        is GridIntent.TileLoaded -> {
            state.copy(matrix = (state.matrix + intent.tile).takeLast(60))//todo take last
                .noSideEffects()
        }
    }
}
