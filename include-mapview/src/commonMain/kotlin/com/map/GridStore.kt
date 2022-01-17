package com.map

import kotlinx.coroutines.CoroutineScope

sealed interface SideEffect {
    class LoadTile(val tile: DisplayTile, val order: Int):SideEffect
}

sealed interface GridIntent {
    class LoadTile(val tile: DisplayTile) : GridIntent
    class TileLoaded(val tile: ImageTile):GridIntent
}

private var nextOrder: Int = 0

fun CoroutineScope.createGridStore(
    effectHandler: (store: Store<ImageTilesGrid, GridIntent>, SideEffect) -> Unit
) = createStoreWithSideEffect(
    ImageTilesGrid(),
    effectHandler = effectHandler
) { state, intent: GridIntent ->
    when (intent) {
        is GridIntent.LoadTile -> {
            state.addSideEffect(SideEffect.LoadTile(intent.tile, nextOrder++))
        }
        is GridIntent.TileLoaded -> {
            state.copy(matrix = (state.matrix + intent.tile).sortedBy { it.order }.takeLast(40))//todo take last
                .noSideEffects()
        }
    }
}
