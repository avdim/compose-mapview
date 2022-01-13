package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

actual fun readAbstractImageDataFromFile(path: String): AbstractImageData {
    try {
        return File(path).readBytes()
//        val result: BufferedImage? = ImageIO.read(File(path))
//        return result ?: throw Error("readAbstractImageDataFromFile, result == null")
    } catch (t: Throwable) {
        val message = "Error in readAbstractImageDataFromFile"
        println(message)
        t.printStackTrace()
        TODO(message)
    }
}

actual fun Picture.toImageBitmap(): ImageBitmap = image.toImageBitmap()

//org.jetbrains.skia.Image.makeFromEncoded(
//    toByteArray(image)
//).toComposeImageBitmap()

