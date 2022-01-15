package com.map

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

actual val ktorClient: HttpClient = HttpClient(CIO)

val cache: MutableMap<String, Picture> = ConcurrentHashMap() //todo temp in memory cache

actual suspend fun downloadImage(url: String): Picture {
    if (true) {
        val bitmap =
            if (url.contains("0/0/0")) {
                TEMP_BITMAP_000
            } else if(url.contains("1/0/0")) {
                TEMP_BITMAP_100
            } else if(url.contains("1/0/1")) {
                TEMP_BITMAP_101
            } else if(url.contains("1/1/0")) {
                TEMP_BITMAP_110
            } else if(url.contains("1/1/1")) {
                TEMP_BITMAP_111
            } else {
                TEMP_BITMAP_000
            }
        return Picture(
            url = url,
            image =bitmap ,
            width = TILE_SIZE,
            height = TILE_SIZE
        )
        return Picture(
            url = url,
            image = TEMP_BITMAP,
            width = TILE_SIZE,
            height = TILE_SIZE
        )
    }
    val fromCache = cache[url]
    if (fromCache != null) {
        return fromCache
    }
    val byteArray: ByteArray = ktorClient.get<ByteArray>(url)
//    val bitmap: BufferedImage = ImageIO.read(byteArray.inputStream())
    val result = Picture(
        url = url,
        image = byteArray,
        width = TILE_SIZE,
        height = TILE_SIZE
    )
    cache[url] = result
    return result
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

