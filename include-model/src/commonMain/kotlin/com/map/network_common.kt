package com.map

import io.ktor.client.*


expect val ktorClient: HttpClient

fun loadImages(list: List<String>): List<Picture> {
    return list.map {
        loadFullImage(it)
    }
}

expect fun loadFullImage(source: String): Picture
