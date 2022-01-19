package com.map

import kotlinx.coroutines.CoroutineScope

data class DisplayTileWithImage<T>(
    val displayTile: DisplayTile,
    val image: T?,
    val tile: Tile,
)


