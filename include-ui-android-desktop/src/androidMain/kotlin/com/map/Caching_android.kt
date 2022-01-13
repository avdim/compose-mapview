package com.map

import android.content.Context
import android.graphics.Bitmap
import java.io.*
import java.nio.charset.StandardCharsets

fun isFileExists(path:String):Boolean = File(path).exists()
fun getFileSeparator():String= File.separator

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

        bw.write(picture.url)
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
