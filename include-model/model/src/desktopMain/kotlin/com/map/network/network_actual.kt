package com.map.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

actual fun getAppScope() = MainScope()
actual fun getNetworkScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)
actual val ktorClient: HttpClient = HttpClient(CIO)
