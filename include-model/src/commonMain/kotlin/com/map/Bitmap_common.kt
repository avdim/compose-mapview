package com.map

import kotlin.js.JsExport

@JsExport
public data class Picture(
    val url: String,
    val image: AbstractImageData,
    val width: Int = 512,
    val height: Int = 512
)

expect public class AbstractImageData

expect fun Picture.scale(width: Int, height: Int): Picture
