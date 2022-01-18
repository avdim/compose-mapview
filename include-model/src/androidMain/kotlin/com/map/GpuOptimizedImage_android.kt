package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset

actual class GpuOptimizedImage(
    val platformSpecificData: ImageBitmap,
    val srcOffset: IntOffset = IntOffset.Zero,
    val size: Int = TILE_SIZE
)
