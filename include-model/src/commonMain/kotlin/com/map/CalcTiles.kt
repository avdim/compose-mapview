package com.map

import kotlin.math.log
import kotlin.math.pow

fun calcTiles(mapState: MapState, width: Int, height: Int): TilesGrid {
    val centerTodo: GeoPt = GeoPt(0.5, 0.5)
    fun GeoPt.toDisplay(): Pt {
        TODO()
    }

    fun Int.toGeo(): Double {
        val scale: Double = 1.0 + (mapState.zoom * 10)
        return (this) / (scale * height)
    }

    fun Pt.toGeo(): GeoPt {
        val diff = GeoPt((width / 2).toGeo(), (height / 2).toGeo())
        val x1 = (x - width / 2).toGeo()
        val y1 = (y - height / 2).toGeo()
        val result = centerTodo + GeoPt(x1, y1)
        return result
    }

    val topLeftGeo = Pt(0, 0).toGeo()
    val bottomRightGeo = Pt(width, height).toGeo()
    val geoSize = bottomRightGeo delta topLeftGeo

    val targetZoom = 1.0 / minOf(1.0, maxOf(geoSize.x, geoSize.y)) // 1 .. +Inf
    val zoomLevel = calcZoomLevel(targetZoom)
    val tileScale = targetZoom / calcZoom(zoomLevel)
    val sizePx = (TILE_SIZE * tileScale).toInt()

    val tilesX = (geoSize.x / TILE_SIZE.toGeo()).toInt() + 1
    val tilesY = (geoSize.y / TILE_SIZE.toGeo()).toInt() + 1

    val MAX_TILES_AT_LEVEL = pow2(zoomLevel)
    val startTileX: Int = ((topLeftGeo.x / geoSize.x) * MAX_TILES_AT_LEVEL).toInt()
    val startTileY: Int = ((topLeftGeo.y / geoSize.y) * MAX_TILES_AT_LEVEL).toInt()

    val grid: List<List<DisplayTile>> = buildList {
        for (x in 0 until tilesX) {
            add(buildList {
                for (y in 0 until tilesY) {
                    val tile = Tile(zoomLevel, startTileX + x, startTileY + y)
                    if( tile.x < MAX_TILES_AT_LEVEL && tile.y < MAX_TILES_AT_LEVEL) {
                        add(DisplayTile(sizePx, x * sizePx, y * sizePx, tile))
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

operator fun GeoPt.plus(minus: GeoPt): GeoPt {
    return GeoPt(x + minus.x, y + minus.y)
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
