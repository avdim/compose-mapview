package com.map

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual fun getDispatcherIO(): CoroutineContext = Dispatchers.IO
