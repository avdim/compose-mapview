package com.map

import android.content.Context
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap

fun createDownloadImageRepository(): ImageRepository = createRealRepository()

private fun createRealRepository() = object : ImageRepository {
    val ktorClient: HttpClient = HttpClient(CIO)

    override suspend fun getImage(tile: Tile): Picture {
        val byteArray = ktorClient.get<ByteArray>(tile.tileUrl)
        return Picture(
            image = byteArray
        )
    }
/*    private fun loadFullImageBlocking(url: String): Picture {
    try {
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()
        val input: InputStream = connection.inputStream
        val bitmap: Bitmap? = BitmapFactory.decodeStream(input)
        if (bitmap != null) {
            return Picture(
                url = url,
                image = bitmap,
                width = bitmap.width,
                height = bitmap.height
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Picture(url = url, image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
    }*/
}

fun decorateWithInMemoryCache(imageRepository: ImageRepository): ImageRepository = object : ImageRepository {
    val cache: MutableMap<Tile, Picture> = ConcurrentHashMap()//todo LRU cache как в video Тагира Валеева LinkedHashMap
    override suspend fun getImage(tile: Tile): Picture {
        //todo вставать в блокировку по ключу или вешать обработчики на ожидание по ключу как в видео Романа Елизарова, actor
        val fromCache = cache[tile]
        if (fromCache != null) {
            return fromCache
        }
        val result = imageRepository.getImage(tile)
        cache[tile] = result
        return result
    }
}

fun decorateWithDiskCache(context:Context, imageRepository: ImageRepository): ImageRepository = object : ImageRepository {
    val cacheDir:File = context.cacheDir

    override suspend fun getImage(tile: Tile): Picture {
        val file = with(tile) {
            cacheDir.resolve("map-view-tile-$zoom-$x-$y.png")
        }
        //todo вставать в synchronized блокировку по ключу tile
        val bytes: ByteArray? =
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
        if (bytes == null) {
            val image = imageRepository.getImage(tile)
            getBackgroundScope().launch {
                // save image
                try {
                    file.writeBytes(image.image)
                } catch (t: Throwable) {
                    println("Can't save image to file $file")
                    println("Will work without disk cache")
                }
            }
            return image
        }
        return Picture(
            bytes
        )
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
}
