package com.map

/**
 * параметр T это тип картинки
 */
data class MapState<T>(
    val width: Int = 100, // display width in dp (pixels)
    val height: Int = 100,//display height in dp (pixels)
    val scale: Double = 1.0,
    val topLeft: GeoPt = GeoPt(0.0, 0.0),
    val displayTiles: List<DisplayTileWithImage<T>> = emptyList(),
    // Мутабельный кэш, но мы обязуемся менять и читать его из одного потока
    val cache: MutableMap<Tile, T> = hashMapOf()
)

data class DisplayTileWithImage<T>(
    val displayTile: DisplayTile,
    val image: T?,
    val tile: Tile,
)

val MapState<*>.centerGeo get():GeoPt = displayToGeo(Pt(width / 2, height / 2))
fun <T> MapState<T>.copyAndChangeCenter(targetCenter: GeoPt): MapState<T> =
    copy(
        topLeft = topLeft + targetCenter - centerGeo
    ).correctGeoXY()

/**
 * Корректируем координаты, чтобы они не выходили за край карты.
 */
fun <T> MapState<T>.correctGeoXY(): MapState<T> =
    correctGeoX().correctGeoY()

fun <T> MapState<T>.correctGeoY(): MapState<T> {
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

fun <T> MapState<T>.correctGeoX(): MapState<T> = copy(topLeft = topLeft.copy(x = topLeft.x.mod(1.0)))

