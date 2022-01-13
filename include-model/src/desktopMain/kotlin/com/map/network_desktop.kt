package com.map

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

actual val ktorClient: HttpClient = HttpClient(CIO)

actual fun loadFullImage(source: String): Picture {
    try {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: BufferedImage? = ImageIO.read(input)
        if (bitmap != null) {
            return Picture(
                url = source,
                image = bitmap,
                width = bitmap.width,
                height = bitmap.height
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return Picture(image = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))
}
