package com.map

import kotlinx.coroutines.CoroutineScope

data class DisplayTileWithImage<T>(
    val displayTile: DisplayTile,
    val image: T?,
    val tile: Tile,
)

data class GridState<T : Any>(
    val displayTiles: List<DisplayTileWithImage<T>> = emptyList(),
    val cache: Map<Tile, T> = emptyMap(),
)

sealed interface SideEffectGrid {

}

sealed interface IntentGrid<T> {
    class UpdateTiles<T>(val grid: TilesGrid) : IntentGrid<T>
    class TileImageLoaded<T>(val tile: Tile, val image: T) : IntentGrid<T>
}


