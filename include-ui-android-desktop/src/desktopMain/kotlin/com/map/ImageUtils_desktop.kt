package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual fun ImageBitmap.toByteArray(): ByteArray = toAwtImage().toByteArray()
actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()
actual fun GpuOptimizedImage.extract():ImageBitmap = platformSpecificData
actual fun GpuOptimizedImage.srcOffset(): IntOffset = srcOffset
actual fun GpuOptimizedImage.srcSize(): IntSize = IntSize(size, size)

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return baos.toByteArray()
}

private fun todo(imageBitmap: ImageBitmap) {
    imageBitmap.asSkiaBitmap()
    imageBitmap.toAwtImage()
}
