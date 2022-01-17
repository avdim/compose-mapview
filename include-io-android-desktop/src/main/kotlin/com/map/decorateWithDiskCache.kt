package com.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

fun TileContentRepository<ByteArray>.decorateWithDiskCache(backgroundScope: CoroutineScope, cacheDir: File): TileContentRepository<ByteArray> {
    val origin = this
    return object : TileContentRepository<ByteArray> {
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

        override suspend fun getTileContent(tile: Tile): ByteArray {
            if (!cacheDir.exists()) {
                return origin.getTileContent(tile)
            }
            val file = with(tile) {
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
                val image = origin.getTileContent(tile)
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
