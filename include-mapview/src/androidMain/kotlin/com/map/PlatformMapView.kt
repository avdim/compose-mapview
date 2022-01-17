package com.map

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.map.ui.MapViewAndroidDesktop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal actual fun createImageRepositoryComposable(): TileContentRepository<GpuOptimizedImage> {
    return createRealRepository()
        .decorateWithDiskCache(LocalContext.current.cacheDir)
        .adapter { GpuOptimizedImage(it.toImageBitmap()) }
        .decorateWithInMemoryCache()
}

@Composable
internal actual fun PlatformMapView(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    MapViewAndroidDesktop(
        width = width,
        height = height,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onClick = onClick,
        onMove = onMove
    )
}

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Column {
        Text(state.toString())
    }
}
