package com.map

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

fun MapState.calcTiles(): TilesGrid {
    val z = 1
    val n = pow2(z)
    val tileSize: Int = geoLengthToDisplay(1.0) / n // todo +1 ?

    val minI = (topLeft.x * n).toInt()
    val minJ = (topLeft.y * n).toInt()

    var tilesX = 0 //todo redundant
    var tilesY = 0 //todo redundant
    val grid: List<List<DisplayTile>> = buildList {
        for (i in minI until Int.MAX_VALUE) {
            val geoX = i.toDouble() / n
            val displayX = geoXToDisplay(geoX)
            if (displayX > width) {
                break
            }
            tilesX++
            add(buildList {
                tilesY = 0
                for (j in minJ until Int.MAX_VALUE) {
                    val geoY = j.toDouble() / n
                    val displayY = geoYToDisplay(geoY)
                    if (displayY > height) {
                        break
                    }
                    tilesY++
                    val tile = Tile(z, i % n, j % n)
                    add(DisplayTile(tileSize, displayX, displayY, tile))
                }
            })
        }
    }
    val result = TilesGrid(tilesX, tilesY, grid)
    return result
}

data class GeoPt(val x: Double, val y: Double)

fun GeoPt.toShortString(): String {
    return "x: ${x.toShortString()}, y: ${y.toShortString()}"
}

data class Pt(val x: Int, val y: Int)

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
