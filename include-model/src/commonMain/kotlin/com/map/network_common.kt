package com.map

import io.ktor.client.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

expect val ktorClient: HttpClient

suspend fun loadImages(list: List<String>): List<Picture> {
    return list.map {
        getNetworkScope().async {
            downloadImage(it)
        }
    }.awaitAll()
}

expect suspend fun downloadImage(url: String): Picture
