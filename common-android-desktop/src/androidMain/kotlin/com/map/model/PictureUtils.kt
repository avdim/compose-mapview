package com.map.model

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

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

