package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()
actual fun GpuOptimizedImage.extract():ImageBitmap = platformSpecificData

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return baos.toByteArray()
}

