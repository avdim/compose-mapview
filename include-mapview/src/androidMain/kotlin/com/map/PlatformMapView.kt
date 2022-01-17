package com.map

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.map.ui.MapViewAndroidDesktop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Эта функция с аннотацией Composable, чтобы можно было получить android Context
 */
@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage> {
    return createRealRepository(HttpClient(CIO))
        .decorateWithDiskCache(ioScope, LocalContext.current.cacheDir)
        .adapter { GpuOptimizedImage(it.toImageBitmap()) }
        .distinctDownloadDecorator(ioScope)
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
