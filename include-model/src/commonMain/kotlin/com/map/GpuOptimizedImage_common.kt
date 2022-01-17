package com.map

/**
 * Картинка в удобном представлении для рисования на конкретной платформе.
 * Требуется чтобы отрисовка на Canvas происходила быстро.
 */
class GpuOptimizedImage(val platformSpecificData: Any)

data class ImageTilesGrid(
    val matrix:List<List<ImageTile>>,
)
data class ImageTile(
    val pic: GpuOptimizedImage,
    val display: DisplayTile
)
