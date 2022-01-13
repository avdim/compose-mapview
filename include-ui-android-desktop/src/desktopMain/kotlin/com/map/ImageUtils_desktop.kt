package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual fun ImageBitmap.toByteArray(): ByteArray = toAwtImage().toByteArray()
actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return baos.toByteArray()
}

private fun todo(imageBitmap: ImageBitmap) {
    imageBitmap.asSkiaBitmap()
    imageBitmap.toAwtImage()
}
