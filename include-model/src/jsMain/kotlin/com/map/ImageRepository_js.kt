package com.map

import kotlinx.coroutines.await
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

fun createRealRepository() = object : ContentRepository<Tile, GpuOptimizedImage> {
    override suspend fun loadContent(key: Tile): GpuOptimizedImage {
        val promise: Promise<ImageBitmap> = suspendCoroutine { continuation ->
            val img = Image() // Create new img element
            img.onload = {
                continuation.resume(
                    createImageBitmap(img)
                )
            }
            img.src = key.tileUrl
        }
        return GpuOptimizedImage(promise.await())
    }
}


external fun createImageBitmap(data: Blob): Promise<ImageBitmap>
external fun createImageBitmap(data: Image): Promise<ImageBitmap>
