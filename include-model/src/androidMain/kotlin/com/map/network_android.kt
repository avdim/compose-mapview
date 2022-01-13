package com.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

actual val ktorClient: HttpClient = HttpClient(CIO)

actual suspend fun loadFullImage(url: String): Picture {
    val byteArray: ByteArray = ktorClient.get<ByteArray>(url)
    return Picture(
        url = url,
        image = byteArray,
        width = 512,
        height = 512
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
