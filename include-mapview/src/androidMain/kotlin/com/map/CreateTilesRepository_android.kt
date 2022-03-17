package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


/**
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal actual fun rememberTilesRepository(
    ioScope: CoroutineScope,
    mapTilerSecretKey: String
): ContentRepository<Tile, TileImage> {
    val cacheDir = LocalContext.current.cacheDir
    return remember(cacheDir) {
        createRealRepository(HttpClient(CIO), mapTilerSecretKey)
            .decorateWithLimitRequestsInParallel(ioScope)
            .decorateWithDiskCache(ioScope, cacheDir)
            .adapter { TileImage(it.toImageBitmap()) }
            .decorateWithDistinctDownloader(ioScope)
    }
}

internal actual fun getDispatcherIO(): CoroutineContext = Dispatchers.IO
