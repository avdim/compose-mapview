package com.map.utils

import com.map.model.Picture
import javax.imageio.ImageIO
import java.io.File
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

//todo
//val directory = File(cacheImagePath)
//if (!directory.exists()) {
//    directory.mkdirs()
//}


actual fun isFileExists(path:String):Boolean =
    File(path).exists()

actual fun getFileSeparator():String=
    File.separator


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

        bw.write(picture.source)
        bw.write("\r\n${picture.width}")
        bw.write("\r\n${picture.height}")
        bw.close()

    } catch (e: IOException) {
        e.printStackTrace()
    }
}
