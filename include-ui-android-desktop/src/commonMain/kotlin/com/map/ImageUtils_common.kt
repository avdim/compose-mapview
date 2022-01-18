package com.map

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

expect fun ImageBitmap.toByteArray(): ByteArray
expect fun ByteArray.toImageBitmap(): ImageBitmap
expect fun GpuOptimizedImage.extract():ImageBitmap
expect fun GpuOptimizedImage.srcOffset():IntOffset
expect fun GpuOptimizedImage.srcSize(): IntSize
