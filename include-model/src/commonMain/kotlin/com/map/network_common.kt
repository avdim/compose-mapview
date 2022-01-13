package com.map

import io.ktor.client.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

expect val ktorClient: HttpClient

suspend fun loadImages(list: List<String>): List<Picture> {
    return list.map {
        getNetworkScope().async {
            loadImage(it)
        }
    }.awaitAll()
}

expect suspend fun loadImage(url: String): Picture
