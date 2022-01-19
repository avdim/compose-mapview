package com.map

import kotlin.math.*

fun MapState.geoLengthToDisplay(geoLength: Double): Int {
    return (height * geoLength * scale).toInt()
}

fun MapState.geoXToDisplay(x: Double): Int = geoLengthToDisplay(x - topLeft.x)
fun MapState.geoYToDisplay(y: Double): Int = geoLengthToDisplay(y - topLeft.y)
fun MapState.geoToDisplay(geoPt: GeoPt): Pt = Pt(geoXToDisplay(geoPt.x), geoYToDisplay(geoPt.y))
fun MapState.displayLengthToGeo(displayLength: Int): Double = displayLength / (scale * height)
fun MapState.displayLengthToGeo(pt: Pt): GeoPt = GeoPt(displayLengthToGeo(pt.x), displayLengthToGeo(pt.y))

fun MapState.displayToGeo(displayPt: Pt): GeoPt {
    val x1 = displayLengthToGeo((displayPt.x))
    val y1 = displayLengthToGeo((displayPt.y))
    return topLeft + GeoPt(x1, y1)
}

val MapState.zoom: Int
    get() {
        return minOf(
            Config.MAX_ZOOM,
            maxOf(
                Config.MIN_ZOOM,
                ceil(log2(geoLengthToDisplay(1.0) / TILE_SIZE.toDouble())).roundToInt()
            )
        )
    }

@Suppress("unused")
val MapState.minScale
    get():Double = 1.0
val MapState.maxScale get():Double = (TILE_SIZE.toDouble() / height) * pow2(Config.MAX_ZOOM)
val MapState.maxTileIndex: Int get() = pow2(zoom)
val MapState.tileSize: Int get() = geoLengthToDisplay(1.0) / maxTileIndex + 1

fun MapState.calcTiles(): TilesGrid {
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
    return TilesGrid(tiles)
}

/**
 * x меняется от 0 до 1  (что соответствует longitude -180 .. 180)
 * y меняется от 0 до 1  (что соответствует latitude 90 .. -90)
 */
data class GeoPt(val x: Double, val y: Double)

val GeoPt.longitude get():Double = x * 360.0 - 180.0
val GeoPt.latitude
    get():Double {
        val latRad = atan(sinh(PI * (1 - 2 * y)))
        return latRad / PI * 180.0
    }

fun createGeoPt(latitude: Double, longitude: Double): GeoPt {
    val x = (longitude + 180) / 360
    val y = (1 - ln(tan(latitude.toRad()) + 1 / cos(latitude.toRad())) / PI) / 2
    return GeoPt(x, y)
}

fun Double.toRad() = this * PI / 180

fun GeoPt.toShortString(): String {
    return "x: ${x.toShortString()}, y: ${y.toShortString()}"
}

data class Pt(val x: Int, val y: Int)

operator fun Pt.minus(other: Pt): Pt = Pt(this.x - other.x, this.y - other.y)
fun Pt.distanceTo(other: Pt): Double {
    val dx = other.x - x
    val dy = other.y - y
    return sqrt(dx * dx.toDouble() + dy * dy.toDouble())
}

operator fun GeoPt.minus(minus: GeoPt): GeoPt {
    return GeoPt(x - minus.x, y - minus.y)
}

operator fun GeoPt.plus(other: GeoPt): GeoPt {
    return GeoPt(x + other.x, y + other.y)
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
