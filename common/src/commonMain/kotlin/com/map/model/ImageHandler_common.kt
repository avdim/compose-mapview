package com.map.model

fun loadImages(list: List<String>): List<Picture> {
    return list.map {
        loadFullImage(it).scale(200, 200)
    }
}

expect fun loadFullImage(source: String): Picture
expect fun Picture.scale(width: Int, height: Int): Picture
