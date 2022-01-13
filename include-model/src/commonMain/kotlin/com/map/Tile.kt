package com.map

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * MapTiler tile,
 * doc here https://cloud.maptiler.com/maps/streets/
 */
data class Tile(
    val z:Int,
    val x:Int,
    val y:Int
)

data class DisplayTile(
    val size: Int,
    val x: Int,
    val y: Int,
    val tile: Tile
)

data class ImageTile(
    val pic: Picture,
    val display: DisplayTile
)

data class TilesGrid(
    val lengthX:Int,
    val lengthY:Int,
    val matrix:List<List<DisplayTile>>,
)

data class ImageTilesGrid(
    val lengthX:Int,
    val lengthY:Int,
    val matrix:List<List<ImageTile>>,
)

operator fun TilesGrid.get(x:Int, y:Int):DisplayTile = matrix.get(x).get(y)
operator fun ImageTilesGrid.get(x:Int, y:Int):ImageTile = matrix.get(x).get(y)

suspend fun TilesGrid.downloadImages():ImageTilesGrid {
    return ImageTilesGrid(
        lengthX = lengthX,
        lengthY = lengthY,
        matrix = matrix.map {
            it.map { displayTile->
                getNetworkScope().async {
                    with(displayTile.tile) {
                        val img = loadImage("https://api.maptiler.com/maps/streets/$z/$x/$y.png?key=${com.map.SECRET_API_KEY}")
                        ImageTile(img, displayTile)
                    }
                }
            }.awaitAll()
        }
    )
}
