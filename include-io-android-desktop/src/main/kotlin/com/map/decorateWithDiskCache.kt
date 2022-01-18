package com.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

fun ContentRepository<Tile, ByteArray>.decorateWithDiskCache(backgroundScope: CoroutineScope, cacheDir: File): ContentRepository<Tile, ByteArray> {
    val origin = this
    return object : ContentRepository<Tile, ByteArray> {
//    val cacheDir: File? //todo для Java можно переделать на nio.Path для неблокирующих операций
        //val cacheDir = System.getProperty("user.home")!! + File.separator + "map-view-cache" + File.separator

        init {
            try {
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                println("Can't create cache dir $cacheDir")
            }
        }

        override suspend fun loadContent(key: Tile): ByteArray {
            if (!cacheDir.exists()) {
                return origin.loadContent(key)
            }
            val file = with(key) {
                cacheDir.resolve("tile-$zoom-$x-$y.png")
            }
            //todo вставать в synchronized блокировку по ключу tile
            val fromCache: ByteArray? =
                if (file.exists()) {
                    try {
                        file.readBytes()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        println("Can't read file $file")
                        println("Will work without disk cache")
                        null
                    }
                } else {
                    null
                }
            val result = if (fromCache != null) {
                fromCache
            } else {
                val image = origin.loadContent(key)
                backgroundScope.launch {
                    // save to cacheDir
                    try {
                        file.writeBytes(image)
                    } catch (t: Throwable) {
                        println("Can't save image to file $file")
                        println("Will work without disk cache")
                    }
                }
                image
            }
            return result
        }


    }
}
