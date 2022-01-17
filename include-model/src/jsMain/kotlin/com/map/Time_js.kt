package com.map

actual inline fun timeMs(): Long = kotlin.js.Date.now().toLong()
actual fun <K,V>createConcurrentMap():MutableMap<K,V> = mutableMapOf()
