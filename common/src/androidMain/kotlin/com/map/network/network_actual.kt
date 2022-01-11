package com.map.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*

actual internal inline fun getAppScope():CoroutineScope = MainScope()
actual val ktorClient: HttpClient = HttpClient(CIO)
