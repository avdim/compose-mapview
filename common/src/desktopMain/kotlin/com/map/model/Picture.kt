package com.map.model

import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

actual typealias AbstractImageData = BufferedImage

actual fun PictureInfo.saveToFile(path: String) {
    TODO("save to file")
}

actual fun readAbstractImageDataFromFile(path: String): AbstractImageData {
    try {
        val result: BufferedImage? = ImageIO.read(File(path))
        return result ?: throw Error("readAbstractImageDataFromFile, result == null")
    } catch (t: Throwable) {
        val message = "Error in readAbstractImageDataFromFile"
        println(message)
        t.printStackTrace()
        TODO(message)
    }
}

actual fun readPictureInfoFromFile(path: String): PictureInfo {
    val read = BufferedReader(
        InputStreamReader(
            FileInputStream(path),
            StandardCharsets.UTF_8
        )
    )

    val source = read.readLine()
    val width = read.readLine().toInt()
    val height = read.readLine().toInt()

    read.close()
    return PictureInfo(
        source = source,
        width = width,
        height = height
    )
}

