package com.map.network

import io.ktor.client.*
import kotlinx.coroutines.*

actual fun getAppScope():CoroutineScope = MainScope()
actual fun getNetworkScope():CoroutineScope = CoroutineScope(Dispatchers.Default)
actual val ktorClient: HttpClient = HttpClient()
