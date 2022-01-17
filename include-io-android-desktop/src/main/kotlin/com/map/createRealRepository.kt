package com.map

import io.ktor.client.*
import io.ktor.client.request.*

fun createRealRepository(ktorClient: HttpClient) = object : TileContentRepository<ByteArray> {
    override suspend fun getTileContent(tile: Tile): ByteArray {
        return ktorClient.get<ByteArray>(tile.tileUrl)
    }
}
