package com.map

import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import platform.posix.*


//typealias ImageIos = CGImage
typealias ImageIos = UIImage

actual class TileImage(
    val platformSpecificData: ImageIos,
    actual val offsetX: Int = 0,
    actual val offsetY: Int = 0,
    actual val cropSize: Int = TILE_SIZE,
) {
    actual fun lightweightDuplicate(offsetX: Int, offsetY: Int, cropSize: Int): TileImage =
        TileImage(
            platformSpecificData,
            offsetX = offsetX,
            offsetY = offsetY,
            cropSize = cropSize
        )
}
