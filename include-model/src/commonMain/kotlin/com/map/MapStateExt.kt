package com.map

import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.roundToInt


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


@Suppress("unused")
val MapState.minScale
    get():Double = 1.0
val MapState.maxScale get():Double = (TILE_SIZE.toDouble() / height) * pow2(Config.MAX_ZOOM)

/**
 * Функция 2^x
 */
fun pow2(x: Int): Int {
    if (x < 0) {
        return 0
    }
    return 1 shl x
}
