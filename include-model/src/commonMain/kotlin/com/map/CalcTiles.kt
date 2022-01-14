package com.map

import kotlin.math.log
import kotlin.math.pow

fun calcTiles(mapState: MapState, width: Int, height: Int): TilesGrid {
    val centerTodo: GeoPt = GeoPt(0.5, 0.5)
    fun GeoPt.toDisplay(): Pt {
        TODO()
    }

    fun Int.toGeo():Double {
        val scale:Double = 1.0 + (mapState.zoom * 10)
        return (this) / (scale * height)
    }

    fun Pt.toGeo(): GeoPt {
        val diff = GeoPt((width / 2).toGeo(), (height / 2).toGeo())
        val x1 = (x - width / 2).toGeo()
        val y1 = (y - height / 2).toGeo()

        return centerTodo + GeoPt(x1, y1)
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
    val zoomLevel2 = (mapState.zoom * MAX_ZOOM_LEVEL).toInt() + maxOf(tilesX, tilesY) - 1

    val grid: List<List<DisplayTile>> = buildList {
        for (x in 0 until tilesX) {
            add(buildList {
                for (y in 0 until tilesY) {
                    val tile = Tile(zoomLevel, x, y)
                    add(DisplayTile(sizePx, x * sizePx, y * sizePx, tile))
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
fun calcZoomLevel(zoom: Double):Int {
    return log(zoom, 2.0).toInt() + 1
    // 1.0 -> 1
    // 2.0 -> 1
    // 4.0 -> 2
    // 8.0 -> 3
}
fun calcZoom(zoomLevel:Int):Double {
    return 2.0.pow(zoomLevel)
    when(zoomLevel) {
        0 -> 1.0
        1 -> 2.0
        2 -> 4.0
    }
}
