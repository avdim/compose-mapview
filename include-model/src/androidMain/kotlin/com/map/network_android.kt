package com.map

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

actual val ktorClient: HttpClient = HttpClient(CIO)

actual suspend fun getImage(z: Int, x: Int, y: Int): Picture = downloadImageByCoordinates(z,x,y)
actual suspend fun downloadImage(url: String): Picture {
    val byteArray: ByteArray = ktorClient.get<ByteArray>(url)
    return Picture(
        url = url,
        image = byteArray,
        width = TILE_SIZE,
        height = TILE_SIZE
    )
}

private fun loadFullImageBlocking(url: String): Picture {
//    try {
//        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
//        connection.connectTimeout = 5000
//        connection.connect()
//
//        val input: InputStream = connection.inputStream
//        val bitmap: Bitmap? = BitmapFactory.decodeStream(input)
//        if (bitmap != null) {
//            return Picture(
//                url = url,
//                image = bitmap,
//                width = bitmap.width,
//                height = bitmap.height
//            )
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//    }

    TODO()
//    return Picture(url = url, image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
}
