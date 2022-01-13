package com.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

actual fun getAppScope(): CoroutineScope = MainScope()
actual fun getNetworkScope(): CoroutineScope = CoroutineScope(Dispatchers.Default)
