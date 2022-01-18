package com.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toByteArray(): ByteArray = asAndroidBitmap().toByteArray()
actual fun ByteArray.toImageBitmap(): ImageBitmap = toAndroidBitmap().asImageBitmap()
actual fun GpuOptimizedImage.extract():ImageBitmap = platformSpecificData
actual fun GpuOptimizedImage.srcOffset(): IntOffset = srcOffset
actual fun GpuOptimizedImage.srcSize(): IntSize = IntSize(size, size)

fun Bitmap.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, baos)
    return baos.toByteArray()
}

fun ByteArray.toAndroidBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size);
}
