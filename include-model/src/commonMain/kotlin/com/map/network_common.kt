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
expect suspend fun getImage(z: Int, x: Int, y: Int): Picture
suspend fun downloadImageByCoordinates(z: Int, x: Int, y: Int): Picture {
    return downloadImage("https://api.maptiler.com/maps/streets/$z/$x/$y.png?key=${com.map.SECRET_API_KEY}")
}
