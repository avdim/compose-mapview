package com.map

data class DisplayTile(
    val size: Int,//Размер на экране
    val x: Int,//координаты на экране
    val y: Int
)

data class DisplayTileAndTile(
    val display: DisplayTile,
    val tile: Tile
)
