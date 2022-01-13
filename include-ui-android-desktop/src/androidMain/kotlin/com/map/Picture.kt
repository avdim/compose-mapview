package com.map

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

actual fun readAbstractImageDataFromFile(path: String): AbstractImageData {
    try {
        return File(path).readBytes()
//        return BitmapFactory.decodeFile(path)
    } catch (t: Throwable) {
        val message = "Error in readAbstractImageDataFromFile"
        println(message)
        t.printStackTrace()
        TODO(message)
    }
}

actual fun Picture.toImageBitmap(): ImageBitmap = image.toImageBitmap()
//    image.asImageBitmap()

