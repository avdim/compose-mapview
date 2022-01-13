package com.map.network

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Для простоты примера я минимально использую корутины и эта функиця запускает корутины самым простым способом
 * В настоящем коде следует использовать CoroutineScope и Job-ы
 */

val APP_SCOPE by lazy { getAppScope() }

fun launchAppScope(block: suspend () -> Unit) {
    APP_SCOPE.launch {
        block()
    }
}

expect fun getAppScope():CoroutineScope
expect fun getNetworkScope():CoroutineScope
expect val ktorClient: HttpClient
