package com.map.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

actual typealias AbstractImageData = Bitmap

actual fun readAbstractImageDataFromFile(path: String): AbstractImageData {
    try {
        return BitmapFactory.decodeFile(path)
    } catch (t: Throwable) {
        val message = "Error in readAbstractImageDataFromFile"
        println(message)
        t.printStackTrace()
        TODO(message)
    }
}

actual fun Picture.toImageBitmap(): ImageBitmap =
    image.asImageBitmap()

