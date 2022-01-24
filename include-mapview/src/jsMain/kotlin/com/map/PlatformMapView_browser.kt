package com.map

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

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
    tiles: List<DisplayTileWithImage<TileImage>>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit,
    updateSize: (width: Int, height: Int) -> Unit
) {
    updateSize(modifier.width, modifier.height)
    MapViewBrowser(
        width = modifier.width,
        height = modifier.height,
        tiles = tiles,
        onZoom = onZoom,
        onClick = onClick,
        onMove = onMove
    )
}

