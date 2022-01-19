package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Composable
internal actual fun createTilesRepository(
    ioScope: CoroutineScope,
    mapTilerSecretKey: String
): ContentRepository<Tile, TileImage> {
    return createRealRepository(mapTilerSecretKey)
        .adapter { TileImage(it) }
        .decorateWithLimitRequestsInParallel(ioScope)
}

internal actual fun getDispatcherIO(): CoroutineContext = Dispatchers.Default

