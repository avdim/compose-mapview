package com.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

actual val ktorClient: HttpClient = HttpClient(CIO)

actual fun loadFullImage(source: String): Picture {
    try {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: Bitmap? = BitmapFactory.decodeStream(input)
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

    return Picture(image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
}

