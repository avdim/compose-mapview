package com.map

/**
 * MapTiler tile,
 * doc here https://cloud.maptiler.com/maps/streets/
 */
data class Tile(
    val zoom:Int,
    val x:Int,
    val y:Int
)

data class DisplayTile(
    val size: Int,
    val x: Int,
    val y: Int
)

data class TilesGrid(
    val matrix:List<Pair<DisplayTile, Tile>>,
)

