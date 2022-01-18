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

actual fun GpuOptimizedImage.cropAndRestoreSize(x: Int, y: Int, targetSize: Int): GpuOptimizedImage {
    val scale: Float = targetSize.toFloat() / TILE_SIZE
    val newSize = maxOf(1, (size * scale).roundToInt())
    try {
        val multiplier =
            when(size) {
                512 -> 2f
                256 -> 1f
                128 -> 0.5f
                64 -> 0.25f
                32 -> 0.125f
                else -> {
                    0.125f/2
                }
            }
        val newX = srcOffset.x + ((x * scale).roundToInt()*multiplier).roundToInt()
        val newY = srcOffset.y + ((y * scale).roundToInt()*multiplier).roundToInt()
        return GpuOptimizedImage(platformSpecificData, IntOffset(newX % TILE_SIZE, newY % TILE_SIZE), newSize)
    } catch (t:Throwable) {
        t.printStackTrace()
        println("Arithmetic")
        throw t
    }
}

//actual fun GpuOptimizedImage.cropAndRestoreSize2(i: Int, j: Int, deltaZoom: Int): GpuOptimizedImage {
//    var deltaZoom = deltaZoom
//    var size = this.size
//    var x = srcOffset.x
//    var y = srcOffset.y
//    while (size > 1 && deltaZoom > 0) {
//        val i = i - (x shl deltaZoom)
//        val j = j - (y shl deltaZoom)
//        deltaZoom -= 1
//        size /= 2
//
//    }
//    srcOffset
//    srcSize
//
//    try {
//        if (false) { //todo remove
//            get().asSkiaBitmap().asComposeImageBitmap()
//        }
//        val cropped = cropImage(get().toAwtImage(), Rectangle(x, y, w, h))
//        val scaled = scaleBitmapAspectRatio(cropped, targetW, targetH)
//        val result = GpuOptimizedImage(scaled.toComposeImageBitmap())
//        return result
//    } catch (t: Throwable) {
//        println("debug")
//        throw t
//    }
//}

actual val GpuOptimizedImage.isBadQuality: Boolean get() = size < TILE_SIZE
