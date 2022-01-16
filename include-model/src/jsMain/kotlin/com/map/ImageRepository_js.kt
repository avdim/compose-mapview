package com.map

import io.ktor.client.*
import kotlinx.coroutines.await
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

fun createDownloadImageRepository(): ImageRepository = createRealRepository()

private fun createRealRepository() = object : ImageRepository {
    val ktorClient: HttpClient = HttpClient()

    override suspend fun getImage(tile: Tile): Picture {
        val promise: Promise<ImageBitmap> = suspendCoroutine { continuation ->
            val img = Image() // Create new img element
            img.onload = {
                continuation.resume(
                    createImageBitmap(img)
                )
            }
            img.src = tile.tileUrl
        }
        return Picture(
            image = ImageBitmapContainer(promise.await())
        )
    }
}

fun decorateWithInMemoryCache(imageRepository: ImageRepository): ImageRepository = object : ImageRepository {
    val cache: MutableMap<Tile, Picture> = HashMap()
    override suspend fun getImage(tile: Tile): Picture {
        val fromCache = cache[tile]
        if (fromCache != null) {
            return fromCache
        }
        val result = imageRepository.getImage(tile)
        cache[tile] = result
        return result
    }
}

external fun createImageBitmap(data: Blob):Promise<ImageBitmap>
external fun createImageBitmap(data: Image):Promise<ImageBitmap>
