package com.map

import kotlin.js.JsExport

public data class Picture(
    val image: AbstractImageData
)

expect public class AbstractImageData
