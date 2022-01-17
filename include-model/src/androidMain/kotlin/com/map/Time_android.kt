package com.map

import java.util.concurrent.ConcurrentHashMap

actual inline fun timeMs(): Long = System.currentTimeMillis()
actual fun <K,V>createConcurrentMap():MutableMap<K,V> = ConcurrentHashMap()
