package com.map

data class DisplayTile(
    val size: Int,
    val x: Int,
    val y: Int
)

data class DisplayTileAndTile(
    val display: DisplayTile,
    val tile: Tile
)

data class TilesGrid(
    val matrix: List<DisplayTileAndTile>,
)
