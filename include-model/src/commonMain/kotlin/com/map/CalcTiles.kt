package com.map

fun calcTiles(mapState: MapState, width: Int, height: Int): TilesGrid {
    return TilesGrid(
        2, 2,
        listOf(
            listOf(DisplayTile(200, 0, 0, Tile(1, 0, 0)), DisplayTile(200, 200, 0, Tile(1, 1, 0))),
            listOf(DisplayTile(200, 0, 200, Tile(1, 0, 1)), DisplayTile(200, 200, 200, Tile(1, 1, 1))),
        )
    )
}

