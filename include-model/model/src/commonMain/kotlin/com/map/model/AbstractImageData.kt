
package com.map.model

import kotlin.js.JsExport

@JsExport
data class Picture(
    var source: String = "",
    var image: AbstractImageData,
    var width: Int = 0,
    var height: Int = 0,
    var id: Int = 0
)

expect class AbstractImageData
