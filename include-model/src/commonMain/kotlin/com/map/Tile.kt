package com.map

/**
 * MapTiler tile,
 * doc here https://cloud.maptiler.com/maps/streets/
 */
data class Tile(
    val zoom: Int,
    val x: Int,
    val y: Int
)

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
