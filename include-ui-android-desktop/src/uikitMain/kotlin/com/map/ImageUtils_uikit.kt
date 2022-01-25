package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    println("before toImageBitmap")
    val result = Image.makeFromEncoded(this).toComposeImageBitmap()
    println("toImageBitmap, result: $result")
    return result
}
actual fun TileImage.extract():ImageBitmap = platformSpecificData

