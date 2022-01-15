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
//operator fun TilesGrid.get(x:Int, y:Int):DisplayTile = matrix.get(x).get(y)
//operator fun ImageTilesGrid.get(x:Int, y:Int):ImageTile = matrix.get(x).get(y)

data class ImageTilesGrid(
    val lengthX:Int,
    val lengthY:Int,
    val matrix:List<List<ImageTile>>,
)


suspend fun TilesGrid.downloadImages():ImageTilesGrid {
    return ImageTilesGrid(
        lengthX = lengthX,
        lengthY = lengthY,
        matrix = matrix.map {
            it.map { displayTile->
                getNetworkScope().async {
                    with(displayTile.tile) {

                        val img = getImage(z,x,y)
                        ImageTile(img, displayTile)
                    }
                }
            }.awaitAll()
        }
    )
}
