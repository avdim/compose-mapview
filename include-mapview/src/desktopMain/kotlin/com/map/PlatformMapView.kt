package com.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.map.ui.MapViewAndroidDesktop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope):TileContentRepository<GpuOptimizedImage> {
    // Для HOME директории MacOS требует разрешения.
    // Чтобы не просить разрешений созданим кэш во временной директории.
    val cacheDir = File(System.getProperty("java.io.tmpdir")).resolve(CACHE_DIR_NAME)
    return createRealRepository(HttpClient(CIO))
        .decorateWithDiskCache(ioScope, cacheDir)
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
){
    MapViewAndroidDesktop(
        modifier = modifier,
        isInTouchMode = false,
        stateFlow = stateFlow,
        onZoom = onZoom,
        onClick = onClick,
        onMove = onMove,
        updateSize = updateSize
    )
}

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState>) {
    val state by stateFlow.collectAsState()
    Column(Modifier.background(Color(0x77ffFFff))) {
        Text(state.toShortString())
    }
}

