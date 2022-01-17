package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual class GpuOptimizedImage(
    val platformSpecificData: ImageBitmap
)

