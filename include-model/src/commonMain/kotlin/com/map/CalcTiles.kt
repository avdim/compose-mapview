package com.map


/**
 * Расчёт тайлов, исходя из положения карты и scale-а
 */
fun MapState<*>.calcTiles(): List<DisplayTileAndTile> {
    val minI = (topLeft.x * maxTileIndex).toInt()
    val minJ = (topLeft.y * maxTileIndex).toInt()

    val tiles: List<DisplayTileAndTile> = buildList {
        for (i in minI until Int.MAX_VALUE) {
            val geoX = i.toDouble() / maxTileIndex
            val displayX = geoXToDisplay(geoX)
            if (displayX >= width) {
                break
            }
            for (j in minJ until Int.MAX_VALUE) {
                val geoY = j.toDouble() / maxTileIndex
                val displayY = geoYToDisplay(geoY)
                if (displayY >= height) {
                    break
                }
                val tile = Tile(zoom, i % maxTileIndex, j % maxTileIndex)
                add(
                    DisplayTileAndTile(
                        DisplayTile(tileSize, displayX, displayY),
                        tile
                    )
                )
            }
        }
    }
    return tiles
}
