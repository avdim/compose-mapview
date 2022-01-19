package com.map

import kotlin.coroutines.CoroutineContext

//todo move to another module
expect fun getDispatcherIO(): CoroutineContext
