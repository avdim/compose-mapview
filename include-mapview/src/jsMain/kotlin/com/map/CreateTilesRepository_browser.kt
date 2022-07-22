package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

@Composable
internal actual fun rememberTilesRepository(
    ioScope: CoroutineScope,
): ContentRepository<Tile, TileImage> = remember {
    createRealRepository()
        .adapter { TileImage(it) }
        .decorateWithLimitRequestsInParallel(ioScope)
}

internal actual fun getDispatcherIO(): CoroutineContext = Dispatchers.Default

private fun createRealRepository() =
    object : ContentRepository<Tile, ImageBitmap> {
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override suspend fun loadContent(tile: Tile): ImageBitmap {
            val promise: Promise<ImageBitmap> = suspendCoroutine { continuation ->
                val img = Image() // Create new <img> element
                img.onload = {
                    continuation.resume(
                        createImageBitmap(img)
                    )
                }
                img.src = Config.createTileUrl(tile.zoom, tile.x, tile.y)
            }
            return promise.await()
        }
    }

/**
 * default JS browser API to convert <img> to ImageBitmap
 * https://developer.mozilla.org/en-US/docs/Web/API/createImageBitmap
 */
external fun createImageBitmap(data: Image): Promise<ImageBitmap>
external fun createImageBitmap(data: Blob): Promise<ImageBitmap>

