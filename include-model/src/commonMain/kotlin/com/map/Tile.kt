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


data class DisplayTileAndTile(
    val first: DisplayTile,
    val second: Tile
)
fun DisplayTileAndTile.pair() = Pair<DisplayTile, Tile>(first, second)
data class TilesGrid(
    val matrix:List<DisplayTileAndTile>, //todo pair
)

