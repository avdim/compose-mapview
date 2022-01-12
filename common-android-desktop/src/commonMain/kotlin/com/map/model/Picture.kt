package com.map.model

data class Picture(
    var source: String = "",
    var name: String = "",
    var image: AbstractImageData,
    var width: Int = 0,
    var height: Int = 0,
    var id: Int = 0
)

expect class AbstractImageData
