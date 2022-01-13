package com.map

import java.io.*
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO

//todo
//val directory = File(cacheImagePath)
//if (!directory.exists()) {
//    directory.mkdirs()
//}

actual fun cacheImage(path: String, picture: Picture) {
    try {
        ImageIO.write(picture.image, "png", File(path))

        val bw =
            BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(path + cacheImagePostfix),
                    StandardCharsets.UTF_8
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

fun isFileExists(path:String):Boolean = File(path).exists()

fun getFileSeparator():String= File.separator
