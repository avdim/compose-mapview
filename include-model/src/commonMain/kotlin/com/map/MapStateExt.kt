package com.map

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt


fun MapState<*>.geoLengthToDisplay(geoLength: Double): Int {
    return (height * geoLength * scale).toInt()
}

fun MapState<*>.geoXToDisplay(x: Double): Int = geoLengthToDisplay(x - topLeft.x)
fun MapState<*>.geoYToDisplay(y: Double): Int = geoLengthToDisplay(y - topLeft.y)
fun MapState<*>.geoToDisplay(geoPt: GeoPt): Pt = Pt(geoXToDisplay(geoPt.x), geoYToDisplay(geoPt.y))
fun MapState<*>.displayLengthToGeo(displayLength: Int): Double = displayLength / (scale * height)
fun MapState<*>.displayLengthToGeo(pt: Pt): GeoPt = GeoPt(displayLengthToGeo(pt.x), displayLengthToGeo(pt.y))

fun MapState<*>.displayToGeo(displayPt: Pt): GeoPt {
    val x1 = displayLengthToGeo((displayPt.x))
    val y1 = displayLengthToGeo((displayPt.y))
    return topLeft + GeoPt(x1, y1)
}

val MapState<*>.zoom: Int
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
val MapState<*>.minScale
    get():Double = 1.0
val MapState<*>.maxScale get():Double = (TILE_SIZE.toDouble() / height) * pow2(Config.MAX_ZOOM)
val MapState<*>.maxTileIndex: Int get() = pow2(zoom)
val MapState<*>.tileSize: Int get() = geoLengthToDisplay(1.0) / maxTileIndex + 1

/**
 * Функция 2^x
 */
private fun pow2(x: Int): Int {
    if (x < 0) {
        return 0
    }
    return 1 shl x
}
