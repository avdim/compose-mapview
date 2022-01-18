package com.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import com.map.ui.MapViewAndroidDesktop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

/**
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage> {
    return createRealRepository(HttpClient(CIO))
        .decorateWithLimitRequestsInParallel(ioScope)
        .decorateWithDiskCache(ioScope, LocalContext.current.cacheDir)
        .adapter { GpuOptimizedImage(it.toImageBitmap()) }
        .decorateWithDistinctDownloader(ioScope)
        .decorateWithInMemoryCache()
}

actual typealias DisplayModifier = Modifier

@Composable
internal actual fun PlatformMapView(
    modifier: DisplayModifier,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit,
    updateSize: (width: Int, height: Int) -> Unit
) {
    MapViewAndroidDesktop(
        modifier = Modifier.fillMaxSize(),
        isInTouchMode = true,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onClick = onClick,
        onMove = onMove,
        updateSize = updateSize,
    )
}

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Column {
        Text(state.toString())
    }
}

actual val GpuOptimizedImage.isBadQuality: Boolean get() = size < TILE_SIZE //TODO DUPLICATE
actual fun GpuOptimizedImage.cropAndRestoreSize(x: Int, y: Int, targetSize: Int): GpuOptimizedImage { //TODO DUPLICATE
    val scale: Float = targetSize.toFloat() / TILE_SIZE
    val newSize = maxOf(1, (size * scale).roundToInt())
    val dx = x * newSize / targetSize
    val dy = y * newSize / targetSize
    val newX = srcOffset.x + dx
    val newY = srcOffset.y + dy
    return GpuOptimizedImage(platformSpecificData, IntOffset(newX % TILE_SIZE, newY % TILE_SIZE), newSize)
}
