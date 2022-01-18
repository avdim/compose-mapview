package com.map

import kotlinx.coroutines.await
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

fun createRealRepository() = object : TileContentRepository<GpuOptimizedImage> {
    override suspend fun getTileContent(tile: Tile): GpuOptimizedImage {
        val promise: Promise<ImageBitmap> = suspendCoroutine { continuation ->
            val img = Image() // Create new img element
            img.onload = {
                continuation.resume(
                    createImageBitmap(img)
                )
            }
            img.src = tile.tileUrl
        }
        return GpuOptimizedImage(promise.await())
    }
}

fun TileContentRepository<GpuOptimizedImage>.decorateWithInMemoryCache(): TileContentRepository<GpuOptimizedImage> {
    val origin = this
    return object : TileContentRepository<GpuOptimizedImage> {
        val cache: MutableMap<Tile, GpuOptimizedImage> = HashMap()
        override suspend fun getTileContent(tile: Tile): GpuOptimizedImage {
            val fromCache = cache[tile]
            if (fromCache != null) {
                return fromCache
            }
            val result = origin.getTileContent(tile)
            cache[tile] = result
            return result
        }
    }
}

external fun createImageBitmap(data: Blob): Promise<ImageBitmap>
external fun createImageBitmap(data: Image): Promise<ImageBitmap>
