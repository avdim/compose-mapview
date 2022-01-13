package com.map

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

actual val ktorClient: HttpClient = HttpClient(CIO)

actual suspend fun loadFullImage(url: String): Picture {
    val byteArray: ByteArray = ktorClient.get<ByteArray>(url)
//    val bitmap: BufferedImage = ImageIO.read(byteArray.inputStream())//todo redundant inputStream()
    return Picture(
        url = url,
        image = byteArray,
        width = TILE_SIZE,
        height = TILE_SIZE
    )
//    return Picture(url = url, image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)) //default picture
}

private fun loadFullImageBlocking(source: String): Picture {
    TODO()
//    try {
//        val url = URL(source)
//        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//        connection.connectTimeout = 5000
//        connection.connect()
//
//        val input: InputStream = connection.inputStream
//        val bitmap: BufferedImage? = ImageIO.read(input)
//        if (bitmap != null) {
//            return Picture(
//                url = source,
//                image = bitmap,
//                width = bitmap.width,
//                height = bitmap.height
//            )
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }
//
//    return Picture(url = source, image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))
}

