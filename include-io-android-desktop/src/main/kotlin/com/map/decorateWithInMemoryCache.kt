package com.map

import java.util.concurrent.ConcurrentHashMap

fun <T> TileContentRepository<T>.decorateWithInMemoryCache(): TileContentRepository<T> {
    val origin = this
    return object : TileContentRepository<T> {
        val cache: MutableMap<Tile, T> = ConcurrentHashMap() //todo LRU cache

        override suspend fun getTileContent(tile: Tile): T {
            val fromCache = cache[tile]
            if (fromCache != null) {
                return fromCache
            }
            val result = origin.getTileContent(tile)
            cache[tile] = result
            return result
        }
    }
}

