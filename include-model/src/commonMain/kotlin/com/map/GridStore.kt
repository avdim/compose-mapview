package com.map

import kotlinx.coroutines.CoroutineScope

data class DisplayTileWithImage<T>(
    val displayTile: DisplayTile,
    val image: T,
    val tile: Tile,
)

data class GridState<T : Any>(
    val displayTiles: List<DisplayTileWithImage<T>> = emptyList(),
    val cache: Map<Tile, T> = emptyMap(),
    val croppedCache: Map<Tile, T> = emptyMap(),
)

sealed interface SideEffectGrid {
    class LoadTile(val tile: Tile) : SideEffectGrid
}

sealed interface IntentGrid<T> {
    class UpdateTiles<T>(val grid: TilesGrid) : IntentGrid<T>
    class TileImageLoaded<T>(val tile: Tile, val image: T) : IntentGrid<T>
}

fun <T : Any> CoroutineScope.createGridStore(
    searchOrCropOrNull: Map<Tile, T>.(Tile) -> T?,
    effectHandler: (store: Store<GridState<T>, IntentGrid<T>>, SideEffectGrid) -> Unit
) = createStoreWithSideEffect(
    init = GridState(),
    effectHandler = effectHandler
) { state, intent: IntentGrid<T> ->
    when (intent) {
        is IntentGrid.UpdateTiles -> {
            val tilesToDisplay: MutableList<DisplayTileWithImage<T>> = mutableListOf()
            val tilesToLoad: MutableList<Tile> = mutableListOf()
            intent.grid.tiles.forEach {
                val cachedImage = state.cache[it.tile]
                if (cachedImage != null) {
                    tilesToDisplay.add(DisplayTileWithImage(it.display, cachedImage, it.tile))
                } else {
                    tilesToLoad.add(it.tile)
                    val croppedImage = state.cache.searchOrCropOrNull(it.tile)
                    if (croppedImage != null) {
                        tilesToDisplay.add(DisplayTileWithImage(it.display, croppedImage, it.tile))
                        //todo maybe add croppedCache ?
                    }
                }
            }
            state.copy(
                displayTiles = tilesToDisplay
            ).addSideEffects(
                tilesToLoad.map {
                    SideEffectGrid.LoadTile(it)
                }
            )
        }
        is IntentGrid.TileImageLoaded -> {
            val modifiedTiles = state.displayTiles.toMutableList()
            for (i in modifiedTiles.indices) {
                if (modifiedTiles[i].tile == intent.tile) {
                    modifiedTiles[i] = modifiedTiles[i].copy(
                        image = intent.image
                    )
                }
            }
            state.copy(
                displayTiles = modifiedTiles,
                cache = state.cache + (intent.tile to intent.image)
            ).noSideEffects()
        }
    }
}

