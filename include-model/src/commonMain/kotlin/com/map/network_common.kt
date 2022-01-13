package com.map

import io.ktor.client.*


expect val ktorClient: HttpClient

fun loadImages(list: List<String>): List<Picture> {
    return list.map {
        loadFullImage(it).scale(200, 200)
    }
}

expect fun loadFullImage(source: String): Picture
