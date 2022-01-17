package com.map

/**
 * Картинка в удобном представлении для рисования на конкретной платформе.
 * Требуется чтобы отрисовка на Canvas происходила быстро.
 */

expect class GpuOptimizedImage

data class ImageTilesGrid(
    val matrix:List<ImageTile> = emptyList(),
)
data class ImageTile(
    val image: GpuOptimizedImage,
    val display: DisplayTile
)
