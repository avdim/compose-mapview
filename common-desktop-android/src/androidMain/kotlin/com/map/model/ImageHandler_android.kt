/*
 * Copyright 2020-2021 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package com.map.model

import com.map.model.Picture
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.map.model.getNameURL
import com.map.utils.cacheImage
import com.map.utils.cacheImagePostfix
import com.map.utils.scaleBitmapAspectRatio
import com.map.utils.toPx
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

actual fun isFileExists(path:String):Boolean =
    File(path).exists()

actual fun getFileSeparator():String=
    File.separator

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
                source = source,
                image = bitmap,
                name = getNameURL(source),
                width = bitmap.width,
                height = bitmap.height
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return Picture(image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
}

actual fun Picture.scale(width: Int, height: Int): Picture =
    copy(
        width = width,
        height = height,
        image = scaleBitmapAspectRatio(image.copy(Bitmap.Config.ARGB_8888, true), width, height)
    )
