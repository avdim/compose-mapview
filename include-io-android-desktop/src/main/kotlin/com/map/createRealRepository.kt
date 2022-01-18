package com.map

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlin.random.Random

fun createRealRepository(ktorClient: HttpClient) = object : TileContentRepository<ByteArray> {
    override suspend fun getTileContent(tile: Tile): ByteArray {
        if (Config.SIMULATE_NETWORK_PROBLEMS) {
            delay(Random.nextLong(0, 100))
            if (Random.nextInt(100) < 10) {
                throw Exception("Simulate network problems")
            }
            delay(Random.nextLong(0, 3000))
        }
        val result = ktorClient.get<ByteArray>(tile.tileUrl)
        return result
    }
}
