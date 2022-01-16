package com.map

import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob

actual typealias AbstractImageData = ImageBitmapContainer

class ImageBitmapContainer(
    val imageBitmap: ImageBitmap
)

actual fun Picture.scale(width: Int, height: Int): Picture {
    return this //todo scale
}
