package com.map

import kotlinx.coroutines.CoroutineScope

sealed interface SideEffect {
    class LoadTile(val tile: DisplayTile, val order: Int):SideEffect
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
            state.copy(nextOrder = state.nextOrder + 1)
                .addSideEffect(SideEffect.LoadTile(intent.tile, state.nextOrder))
        }
        is GridIntent.TileLoaded -> {
            state.copy(matrix = (state.matrix + intent.tile).sortedBy { it.order }.takeLast(20))//todo 20
                .noSideEffects()
        }
    }
}
