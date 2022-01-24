package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.ktor.client.*
import io.ktor.client.engine.ios.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Composable
internal actual fun rememberTilesRepository(
    ioScope: CoroutineScope,
    mapTilerSecretKey: String
): ContentRepository<Tile, TileImage> = remember {
    // Для iOS пока без дискового кэша
    createRealRepository(HttpClient(Ios), mapTilerSecretKey)
        .decorateWithLimitRequestsInParallel(ioScope)
        .adapter { TileImage(it.toImageBitmap()) }
        .decorateWithDistinctDownloader(ioScope)
}

internal actual fun getDispatcherIO(): CoroutineContext = Dispatchers.Default

