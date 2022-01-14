package com.map

fun calcTiles(mapState: MapState, width: Int, height: Int): TilesGrid {
    val tilesX = 3
    val tilesY = 4
    val zoomLevel = (mapState.zoom * MAX_ZOOM_LEVEL).toInt() + maxOf(tilesX, tilesY) - 1
    val size = 200
    val grid: List<List<DisplayTile>> = buildList {
        for (x in 0 until tilesX) {
            add(buildList {
                for (y in 0 until tilesY) {
                    val tile = Tile(zoomLevel, x, y)
                    add(DisplayTile(size, x * size, y * size, tile))
                }
            })
        }
    }
    val result = TilesGrid(tilesX, tilesY, grid)
    return result

    return TilesGrid(
        2, 2,
        listOf(
            listOf(DisplayTile(200, 0, 0, Tile(1, 0, 0)), DisplayTile(200, 200, 0, Tile(1, 1, 0))),
            listOf(DisplayTile(200, 0, 200, Tile(1, 0, 1)), DisplayTile(200, 200, 200, Tile(1, 1, 1))),
        )
    )
}
