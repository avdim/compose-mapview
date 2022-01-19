package com.map

import kotlin.math.roundToInt

/**
 * Картинка в удобном представлении для рисования на конкретной платформе.
 * Требуется чтобы отрисовка на Canvas происходила быстро.
 */
expect class GpuOptimizedImage {
    val cropSize: Int
    val offsetX: Int
    val offsetY: Int
    fun lightweightDuplicate(offsetX: Int, offsetY: Int, cropSize: Int): GpuOptimizedImage
}
val GpuOptimizedImage.isBadQuality: Boolean get() = cropSize < TILE_SIZE

data class ImageTilesGrid(
    val matrix: Map<DisplayTile, GpuOptimizedImage?>,
)

data class ImageTile(
    val image: GpuOptimizedImage,
    val display: DisplayTile
)

fun GpuOptimizedImage.cropAndRestoreSize(x: Int, y: Int, targetSize: Int): GpuOptimizedImage {
    val scale: Float = targetSize.toFloat() / TILE_SIZE
    val newSize = maxOf(1, (cropSize * scale).roundToInt())
    val dx = x * newSize / targetSize
    val dy = y * newSize / targetSize
    val newX = offsetX + dx
    val newY = offsetY + dy
    return lightweightDuplicate(newX % TILE_SIZE, newY % TILE_SIZE, newSize)
}
