package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.dom.Text

@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage> {
    // Для js дисковый кэш не нужен. Браузер и так кэширует картинки.
    return decorateWithInMemoryCache(createDownloadImageRepository())
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
    stateFlow: StateFlow<ImageTilesGrid>,
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

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Text(state.toShortString())
}
