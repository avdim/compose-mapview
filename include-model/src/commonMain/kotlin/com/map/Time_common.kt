package com.map

expect inline fun timeMs(): Long
expect fun <K,V>createConcurrentMap():MutableMap<K,V>
