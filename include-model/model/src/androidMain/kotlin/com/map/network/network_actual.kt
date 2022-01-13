package com.map.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*

actual fun getAppScope():CoroutineScope = MainScope()
actual fun getNetworkScope():CoroutineScope = CoroutineScope(Dispatchers.IO)
actual val ktorClient: HttpClient = HttpClient(CIO)
