package com.map

data class MapState(
    val width: Int = 100, // display width in dp (pixels)
    val height: Int = 100,//display height in dp (pixels)
    val scale: Double = 1.0,
    val topLeft: GeoPt = GeoPt(0.0, 0.0)
)

val MapState.centerGeo get():GeoPt = displayToGeo(Pt(width / 2, height / 2))
fun MapState.copyAndChangeCenter(targetCenter: GeoPt): MapState =
    copy(
        topLeft = topLeft + targetCenter - centerGeo
    ).correctGeoXY()

/**
 * Корректируем координаты, чтобы они не выходили за край карты.
 */
fun MapState.correctGeoXY(): MapState =
    correctGeoX().correctGeoY()

fun MapState.correctGeoY(): MapState {
    val minGeoY = 0.0
    val maxGeoY: Double = 1 - 1 / scale
    return if (topLeft.y < minGeoY) {
        copy(topLeft = topLeft.copy(y = minGeoY))
    } else if (topLeft.y > maxGeoY) {
        copy(topLeft = topLeft.copy(y = maxGeoY))
    } else {
        this
    }
}

fun MapState.correctGeoX(): MapState = copy(topLeft = topLeft.copy(x = topLeft.x.mod(1.0)))

