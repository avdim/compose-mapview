package com.map.utils

import android.content.Context
import android.graphics.*
import com.map.model.Picture
import com.map.utils.cacheImagePostfix
import java.io.File
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

actual fun isFileExists(path:String):Boolean =
    File(path).exists()

actual fun getFileSeparator():String=
    File.separator

actual fun cacheImage(path: String, picture: Picture) {
    try {
        FileOutputStream(path).use { out ->
            picture.image.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val bw =
            BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(path + cacheImagePostfix), StandardCharsets.UTF_8
                )
            )

        bw.write(picture.source)
        bw.write("\r\n${picture.width}")
        bw.write("\r\n${picture.height}")
        bw.close()

    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun clearCache(context: Context) {

    val directory = File(context.cacheDir.absolutePath)

    val files: Array<File>? = directory.listFiles()

    if (files != null) {
        for (file in files) {
            if (file.isDirectory)
                continue

            file.delete()
        }
    }
}
