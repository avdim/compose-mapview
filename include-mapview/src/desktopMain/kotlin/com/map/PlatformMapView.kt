package com.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.IntOffset
import com.map.ui.MapViewAndroidDesktop
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import kotlin.math.roundToInt

@Composable
internal actual fun createImageRepositoryComposable(ioScope: CoroutineScope): TileContentRepository<GpuOptimizedImage> {
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
) {
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

//todo unit tests
actual fun GpuOptimizedImage.cropAndRestoreSize(x: Int, y: Int, targetSize: Int): GpuOptimizedImage {
    val scale: Float = targetSize.toFloat() / TILE_SIZE
    val newSize = maxOf(1, (size * scale).roundToInt())
    try {
        val multiplier =
            when(size) {      // size  scale  targetSize  newSize
                512 -> 1f     //  512   0.5       256      256
                256 -> 0.5f   //  256   0.5       256      128
                128 -> 0.25f  //  128   0.5       256       64
                else -> 0.125f
            }
        val dx = x * newSize / targetSize
        val dy = y * newSize / targetSize
        val newX = srcOffset.x + dx
        val newY = srcOffset.y + dy
        return GpuOptimizedImage(platformSpecificData, IntOffset(newX % TILE_SIZE, newY % TILE_SIZE), newSize)
    } catch (t:Throwable) {
        t.printStackTrace()
        println("Arithmetic")
        throw t
    }
}

actual val GpuOptimizedImage.isBadQuality: Boolean get() = size < TILE_SIZE
