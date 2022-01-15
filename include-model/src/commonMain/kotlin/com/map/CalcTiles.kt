package com.map

import kotlin.math.log

fun MapState.geoToDisplay(geoLength: Double): Int {
    return (height * geoLength * scale).toInt()
}

fun MapState.geoToDisplay(geoPt: GeoPt): Pt {
    return Pt(
        geoToDisplay(geoPt.x),
        geoToDisplay(geoPt.y),
    )
}

fun MapState.displayToGeo(displayLength: Int): Double =
    displayLength / (scale * height)

fun MapState.displayToGeo(displayPt: Pt): GeoPt {
    val x1 = displayToGeo((displayPt.x))
    val y1 = displayToGeo((displayPt.y))
    return GeoPt(x1, y1)
}

fun MapState.calcTiles(): TilesGrid {
    val visibleTopLeft = displayToGeo(Pt(0, 0))
    val visibleTopRight = displayToGeo(Pt(width, height))
    val visibleGeoSize = visibleTopRight delta visibleTopLeft

//    val targetZoom = 1.0 / minOf(1.0, maxOf(geoSize.x, geoSize.y)) // 1 .. +Inf
    val zoomLevel = calcZoomLevel(scale)
    val tileScale = scale / calcZoom(zoomLevel)
    val sizePx = (TILE_SIZE * tileScale).toInt()

    val tilesX = (visibleGeoSize.x / displayToGeo(TILE_SIZE)).toInt() + 1
    val tilesY = (visibleGeoSize.y / displayToGeo(TILE_SIZE)).toInt() + 1

    val maxTilesAtLevel = pow2(zoomLevel)
    val startTileX: Int = maxOf(0, ((topLeft.x / visibleGeoSize.x) * maxTilesAtLevel).toInt()) //todo bad maxOf
    val startTileY: Int = maxOf(0, ((topLeft.y / visibleGeoSize.y) * maxTilesAtLevel).toInt())

    val firstTileGeoX = startTileX * visibleGeoSize.x / maxTilesAtLevel
    val firstTileGeoY = startTileY * visibleGeoSize.y / maxTilesAtLevel
    val firstTileGeo = GeoPt(firstTileGeoX, firstTileGeoY)
    val deltaDisplay = geoToDisplay(topLeft - firstTileGeo)

    val grid: List<List<DisplayTile>> = buildList {
        for (x in 0 until tilesX) {
            add(buildList {
                for (y in 0 until tilesY) {
                    val tile = Tile(zoomLevel, startTileX + x, startTileY + y)
                    if (tile.x < maxTilesAtLevel && tile.y < maxTilesAtLevel) {
                        add(DisplayTile(sizePx, deltaDisplay.x + x * sizePx,deltaDisplay.y + y * sizePx, tile))
                    }
                }
            })
        }
    }
    val result = TilesGrid(tilesX, tilesY, grid)
    return result
}

data class GeoPt(val x: Double, val y: Double)
data class Pt(val x: Int, val y: Int)

infix fun GeoPt.delta(minus: GeoPt): GeoPt {
    return this - minus
}

operator fun GeoPt.minus(minus: GeoPt): GeoPt {
    return GeoPt(x - minus.x, y - minus.y)
}

operator fun GeoPt.plus(other: GeoPt): GeoPt {
    return GeoPt(x + other.x, y + other.y)
}

fun calcZoomLevel(zoom: Double): Int {
    return log(zoom, 2.0).toInt() + 1
    // 1.0 -> 1
    // 2.0 -> 1
    // 4.0 -> 2
    // 8.0 -> 3
}

fun calcZoom(zoomLevel: Int): Double {
    return pow2(zoomLevel - 1).toDouble()
}

/**
 * 2^x
 */
fun pow2(x: Int): Int {
    if (x < 0) {
        return 0
    }
    return 1 shl x
}
