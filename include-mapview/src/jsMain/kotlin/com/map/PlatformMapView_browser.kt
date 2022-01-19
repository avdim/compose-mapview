package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope, mapTilerSecretKey:String): ContentRepository<Tile, GpuOptimizedImage> {
    // Для браузера дисковый кэш не нужен, он и так кэширует картинки.
    return createRealRepository(mapTilerSecretKey)
        .adapter { GpuOptimizedImage(it) }
        .decorateWithLimitRequestsInParallel(ioScope)
        .decorateWithInMemoryCache()
}

actual typealias DisplayModifier = MapViewJsModifier

public interface MapViewJsModifier {
    val width: Int
    val height: Int
}

public fun size(width: Int, height: Int): MapViewJsModifier = object : MapViewJsModifier {
    override val width: Int = width
    override val height: Int = height
}

@Composable
internal actual fun PlatformMapView(
    modifier: DisplayModifier,
    stateFlow: StateFlow<ImageTilesGrid<GpuOptimizedImage>>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit,
    updateSize: (width: Int, height: Int) -> Unit
) {
    updateSize(modifier.width, modifier.height)
    MapViewBrowser(
        width = modifier.width,
        height = modifier.height,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onClick = onClick,
        onMove = onMove
    )
}

