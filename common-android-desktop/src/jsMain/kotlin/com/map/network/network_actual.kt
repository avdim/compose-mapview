package com.map.network

import io.ktor.client.*
import kotlinx.coroutines.*

actual internal inline fun getAppScope():CoroutineScope = MainScope()
actual internal inline fun getNetworkScope():CoroutineScope = CoroutineScope(Dispatchers.Default)
actual val ktorClient: HttpClient = HttpClient()
