package com.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

actual fun getAppScope() = MainScope()
actual fun getBackgroundScope(): CoroutineScope = CoroutineScope(SupervisorJob() +  Dispatchers.IO)

