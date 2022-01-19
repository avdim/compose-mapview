package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

expect fun ByteArray.toImageBitmap(): ImageBitmap
expect fun GpuOptimizedImage.extract():ImageBitmap
