package com.map

import org.w3c.dom.ImageBitmap

actual class GpuOptimizedImage(
    val platformSpecificData: ImageBitmap,
    val srcOffset: Pt = Pt(0,0),
    val size:Int = TILE_SIZE
)
