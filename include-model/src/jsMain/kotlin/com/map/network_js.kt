package com.map

import io.ktor.client.*

actual val ktorClient: HttpClient = HttpClient()

actual suspend fun loadFullImage(url: String): Picture {
    TODO("Not yet implemented")
}
