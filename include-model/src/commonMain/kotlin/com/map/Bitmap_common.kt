package com.map

import kotlin.js.JsExport

@JsExport
public data class Picture(
    val url: String,
    val image: AbstractImageData,
    val width: Int = TILE_SIZE,
    val height: Int = TILE_SIZE
)

expect public class AbstractImageData

expect fun Picture.scale(width: Int, height: Int): Picture
