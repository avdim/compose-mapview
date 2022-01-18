package com.map

interface ContentRepository<K, T> {
    suspend fun loadContent(key: K): T
}

fun <K, A, B> ContentRepository<K, A>.adapter(transform: (A) -> B): ContentRepository<K, B> {
    val origin = this
    return object : ContentRepository<K, B> {
        override suspend fun loadContent(key: K): B {
            return transform(origin.loadContent(key))
        }
    }
}

fun <K, T> ContentRepository<K, T>.decorateWithInMemoryCache(): ContentRepository<K, T> {
    val origin = this
    return object : ContentRepository<K, T> {
        val cache: MutableMap<K, T> = createConcurrentMap() //todo LRU cache LinkedHashMap
        override suspend fun loadContent(key: K): T {
            val fromCache = cache[key]
            if (fromCache != null) {
                return fromCache
            }
            val result = origin.loadContent(key)
            cache[key] = result
            return result
        }
    }
}
