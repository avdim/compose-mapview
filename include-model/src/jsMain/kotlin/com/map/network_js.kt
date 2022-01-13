package com.map

import io.ktor.client.*

actual val ktorClient: HttpClient = HttpClient()

actual suspend fun loadImage(url: String): Picture {
    TODO("Not yet implemented")
}
