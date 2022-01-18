package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Image

actual class GpuOptimizedImage(
    val platformSpecificData: ImageBitmap,
    val srcOffset: IntOffset = IntOffset.Zero,
    val size: Int = TILE_SIZE
)

val GpuOptimizedImage.dstSize:IntSize get() = IntSize(TILE_SIZE, TILE_SIZE)

